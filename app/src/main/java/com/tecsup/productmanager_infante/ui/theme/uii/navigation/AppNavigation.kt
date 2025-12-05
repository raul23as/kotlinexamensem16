package com.tecsup.productmanager_infante.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.productmanager_infante.data.model.Product
import com.tecsup.productmanager_infante.ui.screens.LoginScreen
import com.tecsup.productmanager_infante.ui.screens.ProductFormScreen
import com.tecsup.productmanager_infante.ui.screens.ProductListScreen
import com.tecsup.productmanager_infante.ui.screens.RegisterScreen
import com.tecsup.productmanager_infante.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ProductList : Screen("product_list")
    object ProductForm : Screen("product_form")
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val startDestination = if (isLoggedIn) {
        Screen.ProductList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                onNavigateToForm = { product ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("product", product)
                    navController.navigate(Screen.ProductForm.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProductForm.route) {
            val product = navController.previousBackStackEntry
                ?.savedStateHandle?.get<Product>("product")

            ProductFormScreen(
                product = product,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}