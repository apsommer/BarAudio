package com.sommerengineering.baraudio.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen() {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)


}