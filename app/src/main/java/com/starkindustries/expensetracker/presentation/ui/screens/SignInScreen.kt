package com.starkindustries.expensetracker.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    onSignInClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSignInClicked) {
            Text(text = "Sign in with Google")
        }
    }
}
