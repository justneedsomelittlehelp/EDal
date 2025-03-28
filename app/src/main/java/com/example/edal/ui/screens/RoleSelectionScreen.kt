package com.example.edal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Choose your role", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onRoleSelected("renter") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I am a Renter")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onRoleSelected("rentee") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I am a Rentee")
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
