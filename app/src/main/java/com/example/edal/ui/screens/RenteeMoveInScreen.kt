package com.example.edal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.threeten.bp.LocalDate


@Composable
fun RenteeMoveInScreen(
    viewModel: RenteeMoveInViewModel = viewModel(),
    onNext: () -> Unit
) {
    val moveInDate by viewModel.moveInDate.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var stayLength by remember { mutableStateOf(0) }


    val today = remember { LocalDate.now() }
    val next6Months = remember {
        (0L..5L).map { offset ->
            today.plusMonths(offset).withDayOfMonth(1)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Move-in Date", style = MaterialTheme.typography.headlineSmall)

            next6Months.forEach { date ->
                Button(
                    onClick = { viewModel.setMoveInDate(date) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(date.toString())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Expected Stay (months): $stayLength")
            Slider(
                value = stayLength.toFloat(),
                onValueChange = { stayLength = it.toInt() },
                valueRange = 1f..12f,
                steps = 11
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // You can save data here or pass it to Firestore later
                    println("Move-in Date: $moveInDate")
                    println("Stay Length: $stayLength months")
                    onNext() // Navigate to next screen
                }
                ,
                enabled = moveInDate != null && stayLength > 0 && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save & Continue")
            }


        }
    }
}
