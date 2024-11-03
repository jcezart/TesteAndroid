// CreateBookScreen.kt
package com.example.testeandroid.presentation.home

import android.content.Context
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
import com.example.testeandroid.data.models.Category
import com.example.testeandroid.data.models.CreateBookRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.testeandroid.core.data.networking.RetrofitClient
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

// Função auxiliar para converter Uri em File
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".tmp", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    // Removido imageBase64, pois não será mais usado
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // Apenas atualize o imageUri para exibir a imagem selecionada
                imageUri = it
            }
        }
    )

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

                // Botão para selecionar a imagem
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Selecionar Imagem")
                }

                // Exibir a imagem selecionada
                imageUri?.let { uri ->
                    Image(
                        painter = rememberImagePainter(data = uri),
                        contentDescription = "Imagem selecionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                    )
                }

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
                                text = { Text(text = category.title) },
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

                        if (imageUri == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Selecione uma imagem.")
                            }
                            return@Button
                        }

                        if (selectedCategory == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Selecione uma categoria.")
                            }
                            return@Button
                        }

                        coroutineScope.launch {
                            // Fazer o upload da imagem
                            try {
                                val imageFile = getFileFromUri(context, imageUri!!)
                                if (imageFile != null) {
                                    val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                                    val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                                    // Chamar o método uploadFile
                                    val uploadResponse = RetrofitClient.apiService.uploadFile(multipartBody)
                                    val uploadedImageUrl = uploadResponse.url

                                    if (uploadedImageUrl != null) {
                                        // Criar o objeto de requisição com a URL da imagem
                                        val createBookRequest = CreateBookRequest(
                                            title = title,
                                            summary = if (summary.isNotBlank()) summary else null,
                                            author = author,
                                            imageUrl = uploadedImageUrl,
                                            categoryId = selectedCategory!!.id
                                        )

                                        // Chamar a função de criação no ViewModel
                                        homeViewModel.createBook(createBookRequest)
                                    } else {
                                        snackbarHostState.showSnackbar("Erro ao obter a URL da imagem.")
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Erro ao processar a imagem selecionada.")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Erro ao fazer upload da imagem: ${e.message}")
                            }
                        }
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
