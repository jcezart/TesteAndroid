// LoginViewModel.kt
package com.example.testeandroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import android.util.Log
import android.content.Context

class LoginViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    // LiveData para observar o resultado do login
    val loginResult = MutableLiveData<Result<UserLoginResponse>>()

    fun loginUser(loginRequest: UserLoginRequest, context: Context) {
        viewModelScope.launch {
            try {
                // Chamada à API
                val response = apiService.loginUser(loginRequest)
                Log.d("LoginViewModel", "Login Response: $response")
                // Salvar o token de forma segura
                SecureStorage.saveToken(context, response.token)
                // Atualizar o RetrofitClient com o token
                RetrofitClient.setAuthToken(response.token)
                // Postar o resultado de sucesso
                loginResult.postValue(Result.success(response))
            } catch (e: IOException) {
                // Erro de rede ou I/O
                Log.e("LoginViewModel", "Network Error: ${e.localizedMessage}")
                loginResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                // Erro HTTP
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("LoginViewModel", "HTTP Error Body: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message } ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("LoginViewModel", "HTTP Error: $errorMessage")
                loginResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                // Outros erros
                Log.e("LoginViewModel", "Unexpected Error: ${e.localizedMessage}")
                loginResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }
}
