package com.example.edal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.edal.ui.screens.LoginScreen
import com.example.edal.ui.screens.RoleSelectionScreen
import com.example.edal.ui.theme.EDalTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.edal.ui.screens.ProfileScreen
import com.example.edal.ui.screens.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        setContent {
            var currentUser by remember { mutableStateOf(auth.currentUser) }
            var screenState by remember { mutableStateOf("loading") }
            val context = LocalContext.current

            LaunchedEffect(screenState) {
                if (screenState == "loading") {
                    val user = auth.currentUser
                    currentUser = user
                    if (user == null) {
                        screenState = "login"
                    } else {
                        try {
                            val snapshot = firestore.collection("users").document(user.uid).get().await()
                            val role = snapshot.getString("role")

                            screenState = if (role.isNullOrEmpty()) {
                                "role"
                            } else {
                                "home"
                            }

                        } catch (e: Exception) {
                            screenState = "login"
                        }
                    }
                }
            }

            EDalTheme {
                when (screenState) {
                    "login" -> LoginScreen(onLoginSuccess = {
                        currentUser = auth.currentUser
                        screenState = "loading"
                    })

                    "role" -> RoleSelectionScreen(
                        onRoleSelected = { role ->
                            val uid = auth.currentUser!!.uid
                            firestore.collection("users").document(uid)
                                .set(mapOf("role" to role))
                                .addOnSuccessListener { screenState = "home" }
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            screenState = "login"
                        }
                    )


                    "profile" -> ProfileScreen(onLogout = {
                        auth.signOut()
                        currentUser = null
                        screenState = "login"
                    })

                    "home" -> HomeScreen(onLogout = {
                        auth.signOut()
                        currentUser = null
                        screenState = "login"
                    })

                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}


