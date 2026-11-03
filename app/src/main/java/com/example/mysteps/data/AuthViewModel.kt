package com.example.mysteps.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var isLoginMode = mutableStateOf(true)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun clearFields() {
        email.value = ""
        password.value = ""
        _errorMessage.value = null
        _isSuccess.value = false
    }

    fun authenticate() {
        if (email.value.isBlank() || password.value.isBlank()){
            _errorMessage.value = "Заполните все поля!"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            if (isLoginMode.value) {
                val result = repository.signInWithEmail(email.value.trim(), password.value)
                _isLoading.value = false

                result.onSuccess {
                    _isSuccess.value = true
                }
                result.onFailure { error ->
                    _errorMessage.value = error.localizedMessage ?: "Ошибка при входе"
                }
            } else {
                val authResult = repository.signUpWithEmail(email.value.trim(), password.value)

                authResult.onSuccess {
                    val userId = repository.getCurrentUserOd()

                    if (userId != null) {
                        val newUserTableData = UserDto(
                            users_id = userId,
                            email = email.value.trim(),
                            password = password.value,
                        )
                        val dbResult = repository.saveUserToTable(newUserTableData)

                        _isLoading.value = false
                        dbResult.onSuccess {
                            _isSuccess.value = true
                        }
                        dbResult.onFailure { error ->
                            _errorMessage.value = "Аккаунт создан, но не удалось записать в таблицу: ${error.localizedMessage}"
                        }
                    } else {
                        _isLoading.value = false
                        _isSuccess.value = true
                    }
                }

                authResult.onFailure { error ->
                    _isLoading.value = false
                    _errorMessage.value = error.localizedMessage ?: "Ошибка при регистрации"
                }
            }
        }
    }
}