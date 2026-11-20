package com.example.navigationlab.ui.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> =
        combine(
            favoriteRepository.observeFavorites(),
            preferencesRepository.sortAscending
        ) { favorites, ascending ->
            val sorted = if (ascending) {
                favorites.sortedBy { it.firstName.lowercase() }
            } else {
                favorites.sortedByDescending { it.firstName.lowercase() }
            }
            FavoritesUiState(favorites = sorted, sortAscending = ascending)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState()
        )

    fun toggleSort() {
        viewModelScope.launch {
            preferencesRepository.saveSortAscending(!uiState.value.sortAscending)
        }
    }

    companion object {
        fun factory(
            favoriteRepository: FavoriteRepository,
            preferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { FavoritesViewModel(favoriteRepository, preferencesRepository) }
            }
    }
}