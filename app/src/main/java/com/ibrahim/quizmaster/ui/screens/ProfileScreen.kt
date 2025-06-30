package com.ibrahim.quizmaster.ui.screens

import android.net.Uri
import android.widget.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ibrahim.quizmaster.R
import com.ibrahim.quizmaster.ui.components.BasicButton
import com.ibrahim.quizmaster.ui.components.ProfileInfoBox
import com.ibrahim.quizmaster.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val userState = userViewModel.user.collectAsState()
    val user = userState.value
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Toggle between view/edit mode
    var isEditMode by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
// Initialize fields when entering edit mode
    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            username = user?.userName ?: ""
            email = user?.email ?: ""
        }
    }


// Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            userViewModel.uploadProfileImage(uri, context)
        }
    }

// Use selected image if available, otherwise load from Firebase, fallback to default
    val painter = rememberAsyncImagePainter(
        model = imageUri.value ?: user?.image.takeIf { !it.isNullOrEmpty() },
        placeholder = painterResource(id = R.drawable.profile),
        error = painterResource(id = R.drawable.profile)
    )



    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(stringResource(R.string.welcome)+user.userName) }, actions = {
            IconButton(onClick = {
                isEditMode = !isEditMode // Toggle edit mode
            }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(30.dp)
                )
            }
        })
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Profile image
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable(enabled = isEditMode) {
                            imagePickerLauncher.launch("image/*")
                        }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Editable Fields
                OutlinedTextField(
                    value = if (isEditMode) {
                        username
                    } else user.userName,
                    onValueChange = {
                        if (isEditMode) {
                            username = it
                        } else user.userName = it
                    },
                    label = { Text(stringResource(R.string.username)) },
                    enabled = isEditMode,
                    readOnly = !isEditMode,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = colorResource(id = R.color.Gray),
                        disabledLabelColor = Color.Gray,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledContainerColor = colorResource(id = R.color.background)
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = if (isEditMode) {
                        email
                    } else user.email,
                    onValueChange = {
                        if (isEditMode) {
                            email = it
                        } else user.email = it
                    },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    enabled = isEditMode,
                    readOnly = !isEditMode,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = colorResource(id = R.color.Gray),
                        disabledLabelColor = Color.Gray,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledContainerColor = colorResource(id = R.color.background)
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                )
                ProfileInfoBox(label = stringResource(R.string.score), value = "${user?.score ?: 0}")
                Spacer(modifier = Modifier.height(24.dp))

                // Show "Submit" button only when in edit mode
                if (isEditMode) {
                    OutlinedButton(
                        onClick = {
                            // Save/update logic here
                            if (email.isNotEmpty() && username.isNotEmpty()&&imageUri.value!=null) {

                                userViewModel.updateUserProfile(username,email)
                                userViewModel.updateUserProfile(imageUri.value.toString())
                                isEditMode=false
                            }

                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.hint_green),
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Gray

                        ),
                        shape = RoundedCornerShape(16.dp), // Rounded corners for smooth UI
                        border = BorderStroke(
                            2.dp,
                            colorResource(id = R.color.black)
                        ), // Border color
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Set button height
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Adds a soft shadow
                            .clip(RoundedCornerShape(16.dp)) // Ensures proper clipping
                            .background(colorResource(id = R.color.hint_green))
                    ) {
                        Text(
                            text = stringResource(R.string.submit),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(vertical = 8.dp) // Add padding to text
                        )
                    }
                }
            }
        }
    }
}
