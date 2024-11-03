// RegisterScreen.kt
package com.example.testeandroid.presentation.register

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
import com.example.testeandroid.data.models.UserRegistrationRequest

@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel = viewModel()) {
    // Obter o estado atual do resultado do registro
    val registrationResult by userViewModel.registrationResult.observeAsState()

    // Estados locais para controlar a UI
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Estados para os campos de entrada
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Efeito colateral para reagir às mudanças no resultado
    LaunchedEffect(registrationResult) {
        registrationResult?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                val response = result.getOrNull()
                message = "Registro bem-sucedido!\nID: ${response?.id}\nNome: ${response?.name}\nEmail: ${response?.email}"
                // Opcional: Navegar de volta para a tela de login após o registro
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
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
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de entrada para o Nome
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para o Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
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
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para Confirmar Senha
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botão para realizar o registro com validações
        Button(
            onClick = {
                // Validações básicas
                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    message = "Por favor, preencha todos os campos."
                    return@Button
                }
                if (password.length < 8) {
                    message = "A senha deve ter pelo menos 8 caracteres."
                    return@Button
                }
                if (password != confirmPassword) {
                    message = "As senhas não coincidem."
                    return@Button
                }

                // Iniciar o processo de registro
                isLoading = true
                val userRequest = UserRegistrationRequest(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )
                userViewModel.registerUser(userRequest)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
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

        // Botão para navegar de volta para a tela de login
        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Já tem uma conta? Faça Login")
        }
    }
}
