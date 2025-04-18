package com.example.edal.ui.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    // Show message for verification/resets
    var showMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(showMessage) {
        if (showMessage != null) {
            kotlinx.coroutines.delay(3000)
            showMessage = null
        }
    }

    // Google Sign-In config
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("819719363043-b90jgnba6pfv4m4d56o1a709ivjn55s7.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            Log.d("GOOGLE_SIGNIN", "Google account: ${account.email}")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                Log.d("GOOGLE_SIGNIN", "Firebase login successful")
                onLoginSuccess()
            }
        } else {
            viewModel.onEmailChange("")
            viewModel.onPasswordChange("")
            Log.e("GOOGLE_SIGNIN", "Google sign-in failed: ${task.exception}")
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val isInputValid = email.isNotBlank() && password.isNotBlank()

            Button(
                onClick = { viewModel.login(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && isInputValid
            ) {
                Text("Login with Email")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && isInputValid
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Sign in with Google")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                if (email.isNotBlank()) {
                    viewModel.sendPasswordResetEmail(
                        email,
                        onSent = { showMessage = "Password reset email sent." },
                        onError = { showMessage = it }
                    )
                } else {
                    showMessage = "Please enter your email first."
                }
            }) {
                Text("Forgot password?")
            }

            TextButton(onClick = {
                viewModel.resendVerificationEmail(
                    onSent = { showMessage = "Verification email sent again." },
                    onError = { showMessage = it }
                )
            }) {
                Text("Resend verification email")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (error != null) {
                Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            }

            showMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

