package com.sommerengineering.baraudio.login

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val account: String) : LoginState()
    data class Error(val message: String) : LoginState()
}