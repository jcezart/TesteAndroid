// BookDetailScreen.kt
package com.example.testeandroid.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import androidx.compose.runtime.livedata.observeAsState
import com.example.testeandroid.data.models.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: Int,
    homeViewModel: HomeViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val bookDetailResult by homeViewModel.bookDetailResult.observeAsState()

    // Buscar detalhes do livro ao entrar na tela
    LaunchedEffect(bookId) {
        homeViewModel.fetchBookById(bookId)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Livro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val result = bookDetailResult) {
            null -> {
                // Estado de carregamento
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                if (result.isSuccess) {
                    val book = result.getOrNull()
                    if (book != null) {
                        // Exibir detalhes do livro
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            // Imagem do Livro
                            Image(
                                painter = rememberImagePainter(data = book.imageUrl),
                                contentDescription = "Imagem do livro",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Título do Livro
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Autor do Livro
                            Text(
                                text = "Autor: ${book.author}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Categoria do Livro
                            Text(
                                text = "Categoria: ${book.category.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Resumo do Livro
                            book.summary?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        // Livro não encontrado
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Livro não encontrado.")
                        }
                    }
                } else {
                    // Estado de erro
                    val exception = result.exceptionOrNull()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = exception?.message ?: "Erro ao carregar detalhes do livro.")
                    }
                }
            }
        }
    }
}
