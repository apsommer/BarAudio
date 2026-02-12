package com.sommerengineering.baraudio.messages

sealed class MindfulnessQuoteState {
    object Idle : MindfulnessQuoteState()
    object Loading : MindfulnessQuoteState()
    data class Error(val message: String?) : MindfulnessQuoteState()
    data class Success(val mindfulnessQuote: MindfulnessQuote) : MindfulnessQuoteState()
}

data class MindfulnessQuote(
    val quote: String,
    val category: String
)