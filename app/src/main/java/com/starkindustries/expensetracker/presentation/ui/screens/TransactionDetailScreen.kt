package com.starkindustries.expensetracker.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.starkindustries.expensetracker.R
import com.starkindustries.expensetracker.presentation.viewmodel.TransactionViewModel
import com.starkindustries.expensetracker.ui.theme.Purple80
import com.starkindustries.expensetracker.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: Long,
    viewModel: TransactionViewModel,
    onBackPressed: () -> Unit
) {
    val transaction by viewModel.getTransactionById(transactionId).collectAsState(initial = null)
    var isEditMode by remember { mutableStateOf(false) }

    if (transaction != null) {
        var updatedDescription by remember { mutableStateOf(transaction?.description ?: "") }
        var updatedAmount by remember { mutableStateOf(transaction?.amount.toString()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf7f7fe))
        ) {
            TopAppBar(
                title = {
                    Text(text = transaction!!.type)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        // Save the transaction changes
                        IconButton(onClick = {
                            val updatedTransaction = transaction!!.copy(
                                description = updatedDescription,
                                amount = updatedAmount.toDoubleOrNull() ?: 0.0
                            )
                            viewModel.updateTransaction(updatedTransaction)
                            isEditMode = false
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Save"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            isEditMode = true
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit"
                            )
                        }
                        IconButton(onClick = {
                            viewModel.deleteTransaction(transactionId)
                            navController.popBackStack()
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Delete"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(vertical = 1.dp)
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Transaction Type Display (Read-Only)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = transaction!!.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = Purple80,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = DateFormatter.formatDate(transaction!!.date.toString()) ?: "No Time",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description Section (Editable if in Edit Mode)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditMode) {
                        OutlinedTextField(
                            value = updatedDescription,
                            onValueChange = { updatedDescription = it },
                            label = { Text("") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Purple80,
                                unfocusedBorderColor = Color(0xFFe0e0e5),
                                cursorColor = Color.Black
                            )
                        )
                    } else {
                        Text(
                            text = transaction!!.description ?: "No Description",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Section (Editable if in Edit Mode)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            // Input Field for the Amount
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .weight(2f)
                            ) {
                                // BasicTextField for Amount Input
                                BasicTextField(
                                    value = updatedAmount,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]*(\\.[0-9]{0,2})?\$"))) {
                                            updatedAmount = newValue
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 40.dp) // Create space for "₹" symbol
                                        .align(androidx.compose.ui.Alignment.CenterStart)
                                )

                                // "₹" symbol to the left of the input field
                                Text(
                                    text = "₹",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(start = 16.dp)  // Align "₹" correctly within the padding
                                        .align(androidx.compose.ui.Alignment.CenterStart)
                                )

                                // Divider below the input field
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(androidx.compose.ui.Alignment.BottomStart),
                                    color = Color.Gray,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }

                    else {
                        Text(
                            text = "₹ ${transaction!!.amount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}
