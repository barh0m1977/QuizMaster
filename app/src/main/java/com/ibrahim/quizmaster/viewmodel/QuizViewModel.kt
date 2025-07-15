package com.ibrahim.quizmaster.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ibrahim.quizmaster.data.model.QuizQuestion
import com.ibrahim.quizmaster.data.repository.QuizRepository
import com.ibrahim.quizmaster.data.repository.UserRepository
import com.ibrahim.quizmaster.utils.XlsxParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuizQuestion()
    )

    var questions by mutableStateOf<List<QuizQuestion>>(emptyList())
        private set

    var currentIndex by mutableIntStateOf(0)
        private set

    var score by mutableIntStateOf(0)
        private set

    var numberOfQuestion by mutableIntStateOf(0)
        private set

    var isLoading by mutableStateOf(true)
        private set

    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchQuestions()
    }

    fun getLastQuestionFromFirestore() {
        viewModelScope.launch {
            try {
                val lastQuestionDoc = db.collection("questions")
                    .orderBy("numberOfQuestion", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (lastQuestionDoc.isEmpty) {
                    _lastQuestion.value = "No recent question found."
                } else {
                    val lastQuestion = lastQuestionDoc.documents.firstOrNull()
                    val questionText = lastQuestion?.getString("question")
                    val numberOfQuestion = lastQuestion?.getLong("numberOfQuestion")?.toInt()
                    _lastQuestion.value =
                        if (questionText != null && numberOfQuestion != null)
                            "Q$numberOfQuestion: $questionText"
                        else "Incomplete question data found."
                }
            } catch (e: Exception) {
                Log.e("Firestore Error", "Error fetching last question: ${e.message}")
            }
        }
    }

    fun getDataByQuestion(question: String) {
        repository.getQuestions(question, callback = { fetchedQuestions ->
            questions = fetchedQuestions.filter { it.question == question }
            isLoading = false
        }, onError = { exception ->
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
            user.getUserScoreAndLastQuestion { userScore, numberOfQuestion ->
                if (numberOfQuestion != null) {
                    repository.getQuestions(
                        callback = { fetchedQuestions ->
                            questions = fetchedQuestions.filter {
                                it.numberOfQuestion >= numberOfQuestion
                            }
                            isLoading = false
                            restartQuiz { }
                        },
                        onError = { exception ->
                            errorMessage = exception.message
                            isLoading = false
                        }
                    )
                } else {
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

    // ✅ جديد: تعيين index مع حفظ التقدم
    fun updateCurrentIndex(index: Int) {
        currentIndex = index
        currentQuestion?.let { question ->
            user.saveScore(score, question.numberOfQuestion)
        }
    }

    // ✅ تم التعديل لتحديث التقدم عند الانتقال
    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            updateCurrentIndex(currentIndex + 1)
        }
    }

    fun checkAnswer(selectedAnswer: String, onQuizEnd: () -> Unit) {
        currentQuestion?.let { question ->
            if (selectedAnswer == question.correctAnswer) {
                score++
                user.saveScore(score, question.numberOfQuestion)
                if (isLastQuestion()) {
                    onQuizEnd()
                } else {
                    nextQuestion()
                }
            } else {
                user.saveScore(score, question.numberOfQuestion)
                onQuizEnd()
            }
        }
    }

    fun isLastQuestion(): Boolean = currentIndex == questions.size - 1

    fun restartQuiz(onComplete: () -> Unit) {
        if (questions.isEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                restartQuiz(onComplete)
            }, 500)
            return
        }

        user.getUserScoreAndLastQuestion { userScore, numberOfQuestion ->
            if (userScore != null && numberOfQuestion != null) {
                score = userScore
                val lastIndex = questions.indexOfFirst { it.numberOfQuestion == numberOfQuestion }
                currentIndex = if (lastIndex != -1 && lastIndex < questions.size) {
                    lastIndex
                } else {
                    0
                }
            } else {
                currentIndex = 0
                score = 0
            }
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
        val questionData = hashMapOf(
            "question" to question,
            "options" to options,
            "correctAnswer" to correctAnswer,
            "numberOfQuestion" to number
        )

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
            Toast.makeText(
                context,
                if (success) "Upload successful!" else "Upload failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
