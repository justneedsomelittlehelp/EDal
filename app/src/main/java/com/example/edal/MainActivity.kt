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
import com.example.edal.ui.screens.HomeScreen
import com.example.edal.ui.screens.RenteeProfileScreen
import com.example.edal.ui.screens.RenteeMoveInScreen
import com.jakewharton.threetenabp.AndroidThreeTen



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        AndroidThreeTen.init(this)

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
                            val profileCreated = snapshot.getBoolean("profileCreated") ?: false

                            screenState = when {
                                role.isNullOrEmpty() -> "role"
                                !profileCreated -> "profile" // ✅ Send to profile screen
                                else -> "home"
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
                                .addOnSuccessListener { screenState = "loading" }
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            screenState = "login"
                        }
                    )


                    "profile" -> RenteeProfileScreen(
                        onProfileSaved = {
                            screenState = "movein"
                        },
                        onLogout = {
                            auth.signOut()
                            currentUser = null
                            screenState = "login"
                        }
                    )

                    "movein" -> RenteeMoveInScreen(onNext = {
                        screenState = "home"
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


