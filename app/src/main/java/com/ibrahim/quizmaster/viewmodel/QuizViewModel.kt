package com.ibrahim.quizmaster.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ibrahim.quizmaster.data.model.QuizQuestion
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ibrahim.quizmaster.data.repository.QuizRepository
import com.ibrahim.quizmaster.data.repository.UserRepository
import com.ibrahim.quizmaster.utils.XlsxParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.getOrNull

class QuizViewModel : ViewModel() {
    private val repository = QuizRepository()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = UserRepository()
    private val _quiz = MutableStateFlow(QuizQuestion())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _lastQuestion = MutableStateFlow<String?>(null)
    val lastQuestion = _lastQuestion.asStateFlow()

    val quiz: StateFlow<QuizQuestion> = _quiz.onStart {
        fetchQuestions()

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(
            stopTimeoutMillis = 5000,
        ),
        initialValue = QuizQuestion()
    )


    var questions by mutableStateOf<List<QuizQuestion>>(emptyList())  // Store fetched questions
        private set

    var currentIndex by mutableStateOf(0)  // Track current question index
        private set

    var score by mutableStateOf(0)  // Store the user’s score
        private set
    var numberOfQuestion by mutableStateOf(0)  // number of question
        private set

    var isLoading by mutableStateOf(true)  // Loading state for UI
        private set

    // Get the current question safely
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)


    var errorMessage by mutableStateOf<String?>(null)  // Handle errors
        private set


    // Initialize and fetch questions from Firestore
    init {

        fetchQuestions()
        Log.e("Questions List", questions.map { it.numberOfQuestion }.toString())
    }


    fun getLastQuestionFromFirestore() {
        viewModelScope.launch {
            try {
                // Fetch the most recent question based on timestamp
                val lastQuestionDoc = db.collection("questions")
                    .orderBy("numberOfQuestion", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (lastQuestionDoc.isEmpty) {
                    Log.e("Firestore", "No recent question found.")
                    _lastQuestion.value = "No recent question found."
                } else {
                    // Extract the first document from the query result
                    val lastQuestion = lastQuestionDoc.documents.firstOrNull()

                    // Get the question, numberOfQuestion, and other fields
                    val questionText = lastQuestion?.getString("question")
                    val numberOfQuestion = lastQuestion?.getLong("numberOfQuestion")?.toInt()

                    // Handle null values and update state
                    if (questionText != null && numberOfQuestion != null) {
                        _lastQuestion.value = "Q${numberOfQuestion}: $questionText"
                    } else {
                        _lastQuestion.value = "Incomplete question data found."
                    }

                    Log.d("Firestore", "Last question: ${_lastQuestion.value}")
                }
            } catch (e: Exception) {
                Log.e("Firestore Error", "Error fetching last question: ${e.message}")
            }
        }
    }

    /**
     * resultsId to get question -> edit it
     */
    fun getDataByQuestion(question: String) {
        repository.getQuestions(question, callback = { fetchedQuestions ->
            questions =fetchedQuestions.filter { it.question == question }
            isLoading =false
        }, onError = {exception ->
            errorMessage = exception.message
            isLoading = false
        })
    }


    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            db.collection("questions")
                .orderBy("question")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    val results = documents.mapNotNull { it.getString("question") }

                    _searchResults.value = results

                }
        } else {
            _searchResults.value = emptyList()
        }
    }


    fun fetchQuestions() {
        isLoading = true
        if (user.checkUserScore() > 0) {
            // User has some score, so get the numberOfQuestion and fetch accordingly
            user.getUserScoreAndLastQuestion { userScore, numberOfQuestion ->
                if (numberOfQuestion != null) {
                    // Fetch questions starting from the user's last question number
                    repository.getQuestions(
                        callback = { fetchedQuestions ->
                            questions =
                                fetchedQuestions.filter { it.numberOfQuestion >= numberOfQuestion }
                            isLoading = false
                            restartQuiz { }  // Restart the quiz from the last question the user was at
                        },
                        onError = { exception ->
                            errorMessage = exception.message
                            isLoading = false
                        }
                    )
                } else {
                    // No progress, so fetch all questions
                    repository.getQuestions(
                        callback = { fetchedQuestions ->
                            questions = fetchedQuestions.sortedBy { it.numberOfQuestion }
                            isLoading = false
                            restartQuiz { }
                        },
                        onError = { exception ->
                            errorMessage = exception.message
                            isLoading = false
                        }
                    )
                }
            }
        } else {
            // If no score exists (new user), fetch all questions
            repository.getQuestions(
                callback = { fetchedQuestions ->
                    questions = fetchedQuestions.sortedBy { it.numberOfQuestion }
                    isLoading = false
                    restartQuiz { }
                },
                onError = { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            )
        }
    }

    // Move to the next question
    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
        }
    }

    // Check if the selected answer is correct
    fun checkAnswer(selectedAnswer: String, onQuizEnd: () -> Unit) {
        currentQuestion?.let { question ->
            if (selectedAnswer == question.correctAnswer) {
                score++
                user.saveScore(score, question.numberOfQuestion)
                if (isLastQuestion()) {
                    onQuizEnd() // Navigate to result screen when all questions are answered correctly

                } else {
                    nextQuestion()  // Move to the next question
                }
            } else {
                user.saveScore(score, question.numberOfQuestion)
                onQuizEnd() // Navigate immediately when wrong answer is selected

            }

        }

    }


    // Check if this is the last question
    fun isLastQuestion(): Boolean = currentIndex == questions.size - 1

    // Restart the quiz
    fun restartQuiz(onComplete: () -> Unit) {
        if (questions.isEmpty()) {
            // If questions are still empty, delay and retry
            Handler(Looper.getMainLooper()).postDelayed({
                restartQuiz(onComplete)
            }, 500)
            return
        }

        var lastIndex: Int = 0
        user.getUserScoreAndLastQuestion { userScore, numberOfQuestion ->
            Log.e("lastIsFirst", numberOfQuestion.toString() + "one")

            if (userScore != null && numberOfQuestion != null) {
                score = userScore
                Log.e("lastIsFirst", lastIndex.toString() + "two")
                // Find the last question's index by numberOfQuestion
                lastIndex = questions.indexOfFirst { it.numberOfQuestion == numberOfQuestion }
                Log.e("lastIsFirst", lastIndex.toString() + "three")
                // If found, set currentIndex to that question
                currentIndex = if (lastIndex != -1 && lastIndex < questions.size) {
                    lastIndex
                } else {
                    0
                }
            } else {
                currentIndex = 0
                score = 0
            }

            // Call onComplete callback after updating index and score
            onComplete()
        }
    }


    fun submitQuestion(
        context: Context,
        question: String,
        options: List<String>,
        correctAnswer: String,
        number: Int
    ) {
        // Create a new question object
        val questionData = hashMapOf(
            "question" to question,
            "options" to options, // Store options as a List
            "correctAnswer" to correctAnswer,
            "numberOfQuestion" to number
        )

        // Upload to Firestore
        db.collection("questions")
            .add(questionData)
            .addOnSuccessListener {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Question Submitted Successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun submitFile(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        val questions = XlsxParser.parseQuestionFromExcel(context, uri)

        if (questions.isEmpty()) {
            onResult(false)
            return
        }

        uploadToFirebase(questions, onResult)
    }

    private fun uploadToFirebase(questions: List<QuizQuestion>, onResult: (Boolean) -> Unit) {
        val collection = db.collection("questions")
        var successCount = 0
        var hasFailed = false

        for (q in questions) {
            collection.add(q)
                .addOnSuccessListener {
                    successCount++
                    // ✅ Check if all uploads succeeded
                    if (successCount == questions.size && !hasFailed) {
                        onResult(true)
                    }
                }
                .addOnFailureListener {
                    if (!hasFailed) {
                        hasFailed = true
                        onResult(false)
                    }
                }
        }
    }

    fun uploadFile(context: Context, uri: Uri) {
        submitFile(context, uri) { success ->
            if (success) {
                Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Upload failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}