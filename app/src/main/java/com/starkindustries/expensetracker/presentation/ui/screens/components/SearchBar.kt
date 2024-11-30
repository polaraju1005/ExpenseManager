package com.starkindustries.expensetracker.presentation.ui.screens.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starkindustries.expensetracker.R
import com.starkindustries.expensetracker.ui.theme.Purple80
import kotlinx.coroutines.delay
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    rotatingWords: List<String> = listOf(
        "Search by description",
        "Search by transaction type"
    )
) {
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var animateOffset by remember { mutableFloatStateOf(20f) }
    var isTextVisible by remember { mutableStateOf(true) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = animateOffset, animationSpec = tween(
            durationMillis = 500, easing = LinearOutSlowInEasing
        ), label = ""
    )
    val animatedOpacity by animateFloatAsState(
        targetValue = if (isTextVisible) 1f else 0f, animationSpec = tween(
            durationMillis = 700, easing = LinearOutSlowInEasing
        ), label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            isTextVisible = true
            animateOffset = 0f
            delay(1000L)

            isTextVisible = false
            animateOffset = -10f
            delay(500L)

            currentWordIndex = (currentWordIndex + 1) % rotatingWords.size
            animateOffset = 10f
            delay(500L)
        }
    }

    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChanged(it)
        },
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.height(20.dp)) {
                    Text(
                        text = rotatingWords[currentWordIndex],
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .offset(y = animatedOffset.dp)
                            .alpha(animatedOpacity)
                    )
                }
            }
        },
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.ic_search_compose),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Search")
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            errorTextColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = Purple80,
            errorCursorColor = MaterialTheme.colorScheme.error,
            focusedBorderColor = Color.LightGray.copy(alpha = 0.7f),
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.7f),
            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = Color.Transparent,
            unfocusedLabelColor = Color.Transparent,
            disabledLabelColor = Color.Transparent,
            errorLabelColor = MaterialTheme.colorScheme.error
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}