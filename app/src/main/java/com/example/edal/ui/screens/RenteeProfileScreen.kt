package com.example.edal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenteeProfileScreen(
    viewModel: RenteeProfileViewModel = viewModel(),
    onProfileSaved: () -> Unit,
    onLogout: () -> Unit
) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val age by viewModel.age.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val minBudget by viewModel.minBudget.collectAsState()
    val maxBudget by viewModel.maxBudget.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val stateOptions = listOf("California", "Minnesota", "New York")
    val cityOptions = when (selectedState) {
        "California" -> listOf("Los Angeles", "San Diego", "San Francisco")
        "Minnesota" -> listOf("Minneapolis", "St. Paul")
        "New York" -> listOf("New York City", "Buffalo")
        else -> emptyList()
    }

    val genderOptions = listOf("Male", "Female", "Other")

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Your Information", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = firstName,
                onValueChange = { viewModel.firstName.value = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { viewModel.lastName.value = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { viewModel.age.value = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuBox(
                label = "Gender",
                options = genderOptions,
                selected = gender,
                onSelectedChange = { viewModel.gender.value = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Preferred Location", style = MaterialTheme.typography.titleMedium)

            DropdownMenuBox(
                label = "State",
                options = stateOptions,
                selected = selectedState,
                onSelectedChange = { viewModel.selectedState.value = it }
            )

            DropdownMenuBox(
                label = "City",
                options = cityOptions,
                selected = selectedCity,
                onSelectedChange = { viewModel.selectedCity.value = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Monthly Budget Range: \$${minBudget.toInt()} - \$${maxBudget.toInt()}")

            RangeSlider(
                value = minBudget..maxBudget,
                onValueChange = { range ->
                    viewModel.minBudget.value = range.start
                    viewModel.maxBudget.value = range.endInclusive
                },
                valueRange = 0f..5000f // ✅ Changed from 500f to 0f
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.saveProfile(onProfileSaved) },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor() // ✅ Required for dropdown to position properly
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
