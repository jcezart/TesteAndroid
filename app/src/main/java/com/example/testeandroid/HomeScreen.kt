// HomeScreen.kt
package com.example.testeandroid

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, homeViewModel: HomeViewModel = viewModel()) {
    // Obter o contexto uma vez no início da função Composable
    val context = LocalContext.current

    // Estados para categorias e livros
    val categoriesResult by homeViewModel.categoriesResult.observeAsState()
    val booksResult by homeViewModel.booksResult.observeAsState()

    // Estados de UI
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var filteredBooks by remember { mutableStateOf<List<Book>>(emptyList()) }

    // Buscar categorias e livros ao entrar na tela
    LaunchedEffect(Unit) {
        homeViewModel.fetchCategories()
        homeViewModel.fetchBooks()
    }

    // Filtrar livros com base na categoria selecionada
    LaunchedEffect(categoriesResult, booksResult, selectedCategoryId) {
        if (selectedCategoryId == null) {
            filteredBooks = booksResult?.getOrNull() ?: emptyList()
        } else {
            filteredBooks = booksResult?.getOrNull()?.filter { it.category.id == selectedCategoryId }
                ?: emptyList()
        }
    }

    // UI da tela
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    // Botão de Navegação para Criar Livro
                    IconButton(onClick = {
                        navController.navigate("createBook")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Livro"
                        )
                    }

                    // Botão de Logout Existente
                    IconButton(onClick = {
                        // Implementar logout
                        SecureStorage.clearToken(context)
                        RetrofitClient.setAuthToken("") // Limpar o token no RetrofitClient
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Categorias",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Exibir categorias
            when {
                categoriesResult == null -> {
                    CircularProgressIndicator()
                }
                categoriesResult!!.isSuccess -> {
                    val categories = categoriesResult!!.getOrNull() ?: emptyList()
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                isSelected = category.id == selectedCategoryId,
                                onClick = {
                                    selectedCategoryId =
                                        if (selectedCategoryId == category.id) null else category.id
                                }
                            )
                        }
                    }
                }
                categoriesResult!!.isFailure -> {
                    val exception = categoriesResult!!.exceptionOrNull()
                    Text(
                        text = exception?.message ?: "Erro ao carregar categorias.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Livros",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Exibir livros filtrados
            when {
                booksResult == null -> {
                    CircularProgressIndicator()
                }
                booksResult!!.isSuccess -> {
                    val books = filteredBooks
                    if (books.isEmpty()) {
                        Text(text = "Nenhum livro encontrado.")
                    } else {
                        LazyColumn {
                            items(books) { book ->
                                BookItem(book = book, onClick = {
                                    // Implementar ação ao clicar no livro, como detalhar
                                })
                            }
                        }
                    }
                }
                booksResult!!.isFailure -> {
                    val exception = booksResult!!.exceptionOrNull()
                    Text(
                        text = exception?.message ?: "Erro ao carregar livros.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = category.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = book.imageUrl),
                contentDescription = "Imagem do livro",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Autor: ${book.author}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Categoria: ${book.category.title}",
                    style = MaterialTheme.typography.bodySmall
                )
                book.summary?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
