package com.example.profile.di

import com.example.profile.notifications.ActivityProvider
import com.example.profile.notifications.DefaultActivityProvider
import com.example.profile.repository.ProfileRepository
import com.example.profile.viewmodels.ProfileEditViewModel
import com.example.profile.viewmodels.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single { AndroidContextProvider(androidApplication()) }

    single<ActivityProvider> { DefaultActivityProvider() }

    single { ProfileRepository(get()) }

    viewModel { ProfileViewModel(get()) }
    viewModel { ProfileEditViewModel(get()) }
}