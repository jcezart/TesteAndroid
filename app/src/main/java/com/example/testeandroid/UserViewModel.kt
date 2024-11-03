// UserViewModel.kt
package com.example.testeandroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    // LiveData para observar o resultado do registro
    val registrationResult = MutableLiveData<Result<UserRegistrationResponse>>()

    fun registerUser(userRequest: UserRegistrationRequest) {
        viewModelScope.launch {
            try {
                // Chamada à API
                val response = apiService.registerUser(userRequest)
                // Postar o resultado de sucesso
                registrationResult.postValue(Result.success(response))
            } catch (e: IOException) {
                // Erro de rede ou I/O
                registrationResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                // Erro HTTP
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error ?: "Erro HTTP ${e.code()}: ${e.message()}"
                val errorMessageString = errorMessage.toString()
                registrationResult.postValue(Result.failure(Exception(errorMessageString)))
                //registrationResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                // Outros erros
                registrationResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }
}
