package com.sommerengineering.baraudio.messages

sealed class MindfullnessQuoteState {
    object Loading : MindfullnessQuoteState()
    data class Error(val message: String?) : MindfullnessQuoteState()
    data class Success(val mindfullnessQuote: MindfullnessQuote) : MindfullnessQuoteState()
}

data class MindfullnessQuote(
    val quote: String,
    val category: String
)