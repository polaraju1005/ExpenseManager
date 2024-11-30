package com.starkindustries.expensetracker.presentation.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
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
        var updatedDate by remember { mutableStateOf(transaction?.date?.toString() ?: "") }
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }

        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                updatedDate = "$dayOfMonth/${month + 1}/$year"
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

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
                        IconButton(onClick = {
                            if (updatedAmount.isNotEmpty()) {
                                val updatedTransaction = transaction!!.copy(
                                    description = updatedDescription,
                                    amount = updatedAmount.toDoubleOrNull() ?: 0.0,
                                    date = updatedDate
                                )
                                viewModel.updateTransaction(updatedTransaction)
                                isEditMode = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid amount",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_check),
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
                            viewModel.deleteTransaction(transactionId,context)
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
            ) {
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

                    if (isEditMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, Color(0xFFe0e0e5)))
                                .clickable(onClick = { datePickerDialog.show() })
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = updatedDate,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Filled.CalendarToday,
                                    contentDescription = "Select Date",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color(0xFFa1a1ab)
                                )
                            }
                        }
                    } else {
                        DateFormatter.formatDate(updatedDate.ifEmpty { "No Date" })?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                fontSize = 17.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = " *",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        ) {
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
                                    .padding(8.dp)
                            )

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart),
                                color = Color.Gray,
                                thickness = 1.dp
                            )
                        }
                    }
                } else {
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
