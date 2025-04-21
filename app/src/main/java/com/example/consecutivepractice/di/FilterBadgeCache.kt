package com.example.consecutivepractice.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object FilterBadgeCache {
    private val _shouldShowBadge = MutableStateFlow(false)
    val shouldShowBadge: StateFlow<Boolean> = _shouldShowBadge.asStateFlow()

    fun updateBadgeVisibility(hasCustomFilters: Boolean) {
        _shouldShowBadge.value = hasCustomFilters
    }

}