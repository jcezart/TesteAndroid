// CreateBookScreen.kt
package com.example.testeandroid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    // Criar um SnackbarHostState separado
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Observando o resultado da criação do livro
    val createBookResult by homeViewModel.createBookResult.observeAsState()

    // Observando o resultado das categorias
    val categoriesResult by homeViewModel.categoriesResult.observeAsState()

    // Estado dos campos do formulário
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    // Estado para o DropdownMenu de categorias
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val categories = categoriesResult?.getOrNull() ?: emptyList()

    // Buscando categorias ao entrar na tela
    LaunchedEffect(Unit) {
        homeViewModel.fetchCategories()
    }

    Scaffold(
        // Passar o SnackbarHost para o Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Criar Livro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                // Resumo (Opcional)
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Resumo (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 4
                )

                // Autor
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                // URL da Imagem
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL da Imagem") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )

                // Categoria
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.title ?: "",
                        onValueChange = {},
                        label = { Text("Categoria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = true },
                        enabled = false,
                        trailingIcon = {
                            IconButton(onClick = { isDropdownExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown, // Ícone apropriado
                                    contentDescription = "Abrir categorias"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {Text(text = category.title)},
                                onClick = {
                                    selectedCategory = category
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Botão para criar o livro
                Button(
                    onClick = {
                        // Validar campos
                        if (title.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("O título é obrigatório.")
                            }
                            return@Button
                        }

                        if (author.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("O autor é obrigatório.")
                            }
                            return@Button
                        }

                        if (imageUrl.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("A URL da imagem é obrigatória.")
                            }
                            return@Button
                        }

                        if (selectedCategory == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Selecione uma categoria.")
                            }
                            return@Button
                        }

                        // Criar o objeto de requisição
                        val createBookRequest = CreateBookRequest(
                            title = title,
                            summary = if (summary.isNotBlank()) summary else null,
                            author = author,
                            imageUrl = imageUrl,
                            categoryId = selectedCategory!!.id
                        )

                        // Chamar a função de criação no ViewModel
                        homeViewModel.createBook(createBookRequest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Criar Livro")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )

    // Tratar o resultado da criação do livro
    createBookResult?.let { result ->
        when {
            result.isSuccess -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar("Livro criado com sucesso!")
                    navController.popBackStack() // Navega de volta para a tela anterior
                }
            }
            result.isFailure -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Erro ao criar livro.")
                }
            }
        }
    }

    // Tratar o resultado das categorias
    categoriesResult?.let { result ->
        when {
            result.isFailure -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(result.exceptionOrNull()?.message ?: "Erro ao carregar categorias.")
                }
            }
            // Caso de sucesso já está tratado no DropdownMenu
        }
    }
}
