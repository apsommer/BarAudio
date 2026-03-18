package com.sommerengineering.baraudio.settings.setup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun SetupNavigation(
    onClose: () -> Unit) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "copy") {

    }
}