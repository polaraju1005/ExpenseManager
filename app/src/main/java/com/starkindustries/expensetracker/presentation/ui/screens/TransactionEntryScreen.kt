package com.starkindustries.expensetracker.presentation.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starkindustries.expensetracker.R
import com.starkindustries.expensetracker.data.local.db.entities.TransactionEntity
import com.starkindustries.expensetracker.presentation.viewmodel.TransactionViewModel
import com.starkindustries.expensetracker.ui.theme.Purple80
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    viewModel: TransactionViewModel, onTransactionAdded: () -> Unit, onBackPressed: () -> Unit
) {
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> date = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf7f7fe))
    ) {
        TopAppBar(title = {
            Text(
                text = if (type == "Expense") "Record Expense" else "Record Income",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp
            )
        }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back"
                )
            }
        }, modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RadioOption(
                label = "Expense",
                selected = type == "Expense",
                onSelect = { type = "Expense" })

            RadioOption(
                label = "Income",
                selected = type == "Income",
                onSelect = { type = "Income" })
        }

        Spacer(modifier = Modifier.height(8.dp))

        LabeledOutlinedTextField(
            label = "Date",
            content = date.ifEmpty { "Select Date" },
            onClick = { datePickerDialog.show() },
            isRequired = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabeledOutlinedTextField(
            label = "Description",
            content = description,
            onValueChange = { description = it },
            isRequired = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabeledAmountField(
            content = amount,
            onValueChange = { amount = it },
            isRequired = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (date.isNotEmpty() && type.isNotEmpty() && amount.isNotEmpty()) {
                    val transaction = TransactionEntity(
                        id = 0,
                        date = date,
                        type = type,
                        amount = amount.toDouble(),
                        description = description
                    )
                    viewModel.addTransaction(transaction)
                    onTransactionAdded()
                } else {
                    Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .height(56.dp),
            enabled = date.isNotEmpty() && type.isNotEmpty() && amount.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4c3cce)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Save", color = Color.White)
        }
    }
}

@Composable
fun LabeledAmountField(
    content: String,
    onValueChange: (String) -> Unit = {},
    isRequired: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isRequired) {
                    Text(
                        text = " *",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red
                    )
                }
            }

            Row(
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    BasicTextField(
                        value = content,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]*(\\.[0-9]{0,2})?\$"))) {
                                onValueChange(newValue)
                            }
                        },
                        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp)
                            .align(Alignment.CenterStart)
                    )

                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterStart)
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
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledOutlinedTextField(
    label: String,
    content: String,
    onValueChange: (String) -> Unit = {},
    onClick: (() -> Unit)? = null,
    isRequired: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }

        if (onClick != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, Color(0xFFe0e0e5)))
                    .clickable(onClick = onClick)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = content,
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
            OutlinedTextField(
                value = content,
                onValueChange = onValueChange,
                label = { Text("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Purple80,
                    unfocusedBorderColor = Color(0xFFe0e0e5),
                    cursorColor = Color.Black
                )
            )
        }
    }
}

@Composable
fun RadioOption(
    label: String, selected: Boolean, onSelect: () -> Unit
) {
    val backgroundColor = if (selected) Color.Transparent else Color.White
    val borderColor = if (selected) Purple80 else Color(0xFFe0e0e5)

    Surface(modifier = Modifier
        .clickable { onSelect() }
        .padding(8.dp)
        .width(180.dp)
        .height(65.dp),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = null
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
}
