package com.example.consecutivepractice.di

import com.example.consecutivepractice.Network.RetrofitInstance
import com.example.consecutivepractice.data.AppDatabaseProvider
import com.example.consecutivepractice.data.FavoritesRepository
import com.example.consecutivepractice.domain.GameFilterUseCase
import com.example.consecutivepractice.repositories.FilterRepository
import com.example.consecutivepractice.repositories.GameRepository
import com.example.consecutivepractice.repositories.ProfileRepository
import com.example.consecutivepractice.viewmodels.FavoritesViewModel
import com.example.consecutivepractice.viewmodels.GameFilterViewModel
import com.example.consecutivepractice.viewmodels.GameViewModel
import com.example.consecutivepractice.viewmodels.ProfileEditViewModel
import com.example.consecutivepractice.viewmodels.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    // Providers
    single { AndroidContextProvider(androidApplication()) }
    single { AppDatabaseProvider(androidContext()) }
    single { RetrofitInstance.getGamesApi(androidContext()) }

    // Repositories
    single { GameRepository(get()) }
    single { FilterRepository(get()) }
    single { ProfileRepository(get()) }
    single { FavoritesRepository(get()) }

    // Use Cases
    single { GameFilterUseCase(get()) }

    // ViewModels
    viewModel { GameViewModel(get(), get(), get()) }
    viewModel { GameFilterViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { ProfileEditViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}
