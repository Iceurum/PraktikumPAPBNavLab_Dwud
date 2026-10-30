package com.example.navigationlab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigationlab.data.repository.DefaultStudentRepository
import com.example.navigationlab.data.source.StudentLocalDataSource
import com.example.navigationlab.ui.ui.detail.DetailViewModel
import com.example.navigationlab.ui.ui.home.HomeViewModel
import com.example.navigationlab.ui.ui.screen.AboutScreen
import com.example.navigationlab.ui.ui.screen.DetailScreen
import com.example.navigationlab.ui.ui.screen.HomeScreen
import com.example.navigationlab.ui.ui.screen.ProfileScreen

@Composable
fun NavigationLabApp() {
    val dataSource = remember { StudentLocalDataSource() }
    val repository = remember { DefaultStudentRepository(dataSource) }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(repository)
            )

            LaunchedEffect(Unit) {
                viewModel.loadStudents()
            }

            HomeScreen(
                uiState = viewModel.uiState,
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1
            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository)
            )

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            DetailScreen(
                uiState = viewModel.uiState,
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}