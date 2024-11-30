package com.starkindustries.expensetracker.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.starkindustries.expensetracker.R
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.starkindustries.expensetracker.presentation.ui.screens.components.BottomNavigationBar
import com.starkindustries.expensetracker.presentation.ui.screens.components.SearchBar
import com.starkindustries.expensetracker.presentation.viewmodel.TransactionViewModel
import com.starkindustries.expensetracker.ui.theme.Purple80
import com.starkindustries.expensetracker.ui.theme.TextColorSecondary
import com.starkindustries.expensetracker.utils.DateFormatter
import kotlin.math.roundToInt

@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionViewModel,
    onTransactionDeleted: (Long) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val transactions by viewModel.getAllTransactions().collectAsState(initial = emptyList())
    var selectedItem by remember { mutableIntStateOf(0) }
    val shouldShowBottomBar = true

    Box(
        modifier = Modifier
            .background(Color(0xFFf7f7fe))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp)
        ) {
            SearchBar(
                query = searchText,
                onQueryChanged = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
                    .height(52.5.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp),
                color = TextColorSecondary,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(transactions.filter {
                    // Filter transactions by both description and type
                    it.description?.contains(searchText, ignoreCase = true) == true ||
                            it.type.contains(searchText, ignoreCase = true) ||
                            searchText.isEmpty()
                }) { transaction ->
                    TransactionListItem(
                        transaction = transaction,
                        onDelete = { onTransactionDeleted(transaction.id) },
                        navController
                    )
                }
            }
        }

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 92.dp)
            .shadow(8.dp, shape = RoundedCornerShape(32.dp))
            .background(Purple80, shape = RoundedCornerShape(16.dp))
            .height(56.dp)
            .fillMaxWidth(0.5f)
            .clickable {
                navController.navigate("transaction_entry_screen")
            }) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Add New",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedItem = selectedItem,
            onItemSelected = { index ->
                selectedItem = index
            },
            shouldShow = shouldShowBottomBar
        )
    }
}


@Composable
fun TransactionListItem(
    transaction: TransactionEntity, onDelete: () -> Unit, navController: NavController
) {
    var isSwiped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                isSwiped = offsetX <= -100f
            }
        }
        .clickable {
            navController.navigate("transaction_details_screen/${transaction.id}")
        }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) },
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                ) {
                    Text(
                        text = transaction.description ?: "No Description",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = transaction.type, style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    transaction.date.let { it ->
                        DateFormatter.formatDate(it)?.let {
                            Text(
                                text = it, style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "₹ ${transaction.amount}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (isSwiped) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
                .align(Alignment.CenterEnd)
                .clickable { onDelete() }
                .padding(6.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.swipe_delete),
                    contentDescription = "Delete",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTransactionListScreen() {
}
