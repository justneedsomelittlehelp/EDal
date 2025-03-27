package com.example.edal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.edal.ui.screens.LoginScreen
import com.example.edal.ui.theme.EDalTheme
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EDalTheme {
                val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
                var isLoggedIn by remember { mutableStateOf(isUserLoggedIn) }

                if (isLoggedIn) {
                    HomeScreen(onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        isLoggedIn = false
                    })
                } else {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Surface {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Welcome to EDal 🎉",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}

