package com.example.testeandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testeandroid.core.data.networking.RetrofitClient
import com.example.testeandroid.core.data.local.SecureStorage
import com.example.testeandroid.presentation.home.BookDetailScreen
import com.example.testeandroid.presentation.home.HomeScreen
import com.example.testeandroid.presentation.home.CreateBookScreen
import com.example.testeandroid.presentation.login.LoginScreen
import com.example.testeandroid.presentation.register.RegisterScreen


    @Composable
    fun App() {
        val navController = rememberNavController()
        val context = LocalContext.current
        var startDestination by remember { mutableStateOf("login") }

        LaunchedEffect(Unit) {
            val token = SecureStorage.getToken(context)
            if (!token.isNullOrEmpty()) {
                RetrofitClient.setAuthToken(token) // Atualiza o token no RetrofitClient
                startDestination = "home"
            }
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("createBook") {
                CreateBookScreen(navController = navController)
            }
            composable(
                route = "bookDetail/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId")
                if (bookId != null) {
                    BookDetailScreen(navController = navController, bookId = bookId)
                } else {
                    // Tratar erro ou navegar de volta
                    navController.popBackStack()
                }
            }

        }
    }
