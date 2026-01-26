package com.sommerengineering.baraudio.messages

sealed class QuoteState {
    object Loading : QuoteState()
    data class Error(val message: String?) : QuoteState()
    data class Success(val quote: Quote) : QuoteState()
}

data class Quote(
    val quote: String,
    val category: String
)