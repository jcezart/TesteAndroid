// LoginScreen.kt
package com.example.testeandroid

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    // Obter o estado atual do resultado do login
    val loginResult by loginViewModel.loginResult.observeAsState()

    // Estados locais para controlar a UI
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Estados para os campos de entrada
    var credential by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Efeito colateral para reagir às mudanças no resultado
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                val response = result.getOrNull()
                message = "Login bem-sucedido! Bem-vindo, ${response?.user?.name}."
                // Navegar para a tela Home
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                val exception = result.exceptionOrNull()
                message = exception?.message ?: "Erro desconhecido."
            }
        }
    }

    // UI da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de entrada para o Credential (Email)
        OutlinedTextField(
            value = credential,
            onValueChange = { credential = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para a Senha
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botão para realizar o login com validações
        Button(
            onClick = {
                // Validações básicas
                if (credential.isBlank() || password.isBlank()) {
                    message = "Por favor, preencha todos os campos."
                    return@Button
                }

                // Iniciar o processo de login
                isLoading = true
                val loginRequest = UserLoginRequest(
                    credential = credential,
                    password = password
                )
                loginViewModel.loginUser(loginRequest, context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de carregamento
        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exibir mensagem de sucesso ou erro
        if (message.isNotEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.startsWith("Erro")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para navegar para a tela de registro
        OutlinedButton(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Não tem uma conta? Registre-se")
        }
    }
}
