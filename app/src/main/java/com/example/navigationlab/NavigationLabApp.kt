package com.example.navigationlab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigationlab.data.dto.remote.RetrofitClient
import com.example.navigationlab.data.local.DatabaseProvider
import com.example.navigationlab.data.location.DefaultLocationRepository
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.remote.StudentRemoteDataSource
import com.example.navigationlab.data.repository.DefaultStudentRepository
import com.example.navigationlab.data.repository.FavoriteRepository
import com.example.navigationlab.ui.ui.detail.DetailViewModel
import com.example.navigationlab.ui.ui.favorites.FavoritesViewModel
import com.example.navigationlab.ui.ui.home.HomeViewModel
import com.example.navigationlab.ui.ui.location.LocationScreen
import com.example.navigationlab.ui.ui.location.LocationViewModel
import com.example.navigationlab.ui.ui.screen.AboutScreen
import com.example.navigationlab.ui.ui.screen.DetailScreen
import com.example.navigationlab.ui.ui.screen.FavoritesScreen
import com.example.navigationlab.ui.ui.screen.HomeScreen
import com.example.navigationlab.ui.ui.screen.ProfileScreen
import com.google.android.gms.location.LocationServices

@Composable
fun NavigationLabApp() {
    val remoteDataSource = remember {
        StudentRemoteDataSource(RetrofitClient.apiService)
    }
    val repository = remember {
        DefaultStudentRepository(remoteDataSource)
    }

    val context = LocalContext.current
    val preferencesRepository = remember {
        UserPreferencesRepository(context.applicationContext)
    }
    val favoriteRepository = remember {
        FavoriteRepository(
            DatabaseProvider.getDatabase(context).favoriteStudentDao()
        )
    }

    val locationRepository = remember {
        DefaultLocationRepository(
            context = context.applicationContext,
            client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        )
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(
                    repository,
                    preferencesRepository,
                    favoriteRepository
                )
            )
            val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = homeUiState,
                searchQuery = searchQuery,
                favoriteIds = favoriteIds,
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenAbout = { navController.navigate(Routes.ABOUT) },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                onOpenLocation = { navController.navigate(Routes.LOCATION) },
                onRetry = viewModel::retry,
                onRefresh = viewModel::refresh,
                onSearchQueryChange = viewModel::onSearchQueryChange
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
                factory = DetailViewModel.factory(repository, favoriteRepository)
            )
            val detailUiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            DetailScreen(
                uiState = detailUiState,
                onToggleFavorite = viewModel::toggleFavorite,
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onBack = { navController.popBackStack() },
                onRetry = { viewModel.loadStudent(studentId) }
            )
        }

        composable(Routes.FAVORITES) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.factory(favoriteRepository, preferencesRepository)
            )
            val favoritesUiState by viewModel.uiState.collectAsStateWithLifecycle()

            FavoritesScreen(
                uiState = favoritesUiState,
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                onToggleSort = viewModel::toggleSort,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOCATION) {
            val viewModel: LocationViewModel = viewModel(
                factory = LocationViewModel.factory(locationRepository)
            )
            LocationScreen(
                viewModel = viewModel,
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