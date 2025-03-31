package com.example.edal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RenteeProfileScreen(
    viewModel: RenteeProfileViewModel = viewModel(),
    onProfileSaved: () -> Unit,
    onLogout: () -> Unit
) {
    val age by viewModel.age.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val error by viewModel.error.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Rentee Profile Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = viewModel::onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = viewModel::onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = age,
            onValueChange = viewModel::onAgeChange,
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Gender")
        Row {
            listOf("Male", "Female", "Other").forEach { option ->
                Row(modifier = Modifier.padding(end = 12.dp)) {
                    RadioButton(
                        selected = gender == option,
                        onClick = { viewModel.onGenderChange(option) }
                    )
                    Text(option, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.submitProfile(onProfileSaved) },
            enabled = firstName.isNotBlank() && lastName.isNotBlank() && age.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }
    }
}
