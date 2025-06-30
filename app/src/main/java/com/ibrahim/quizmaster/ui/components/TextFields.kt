package com.ibrahim.quizmaster.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ibrahim.quizmaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocaleOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange, // Let the parent handle state changes
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(id = R.color.hint_green), // Change border color when focused
            unfocusedBorderColor = colorResource(id = R.color.Gray), // Default border color
            cursorColor = colorResource(id = R.color.hint_green), // Cursor color
            focusedLabelColor = colorResource(id = R.color.hint_green), // Label color when focused
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoBox(
    label: String,
    value: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        enabled = false,
        readOnly = true,
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    )

}
