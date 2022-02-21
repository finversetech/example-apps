package com.example.demo_app_android

import android.os.Bundle
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demo_app_android.screen.Greetings
import com.example.demo_app_android.screen.LinkScreen
import com.example.demo_app_android.screen.LoginScreen
import com.example.demo_app_android.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val userState by viewModels<UserStateViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(UserState provides userState) { AppTheme { AuthSwitcher() } }
        }
    }
}

@Composable
private fun AuthSwitcher() {
    val state = UserState.current
    if (state.isLoggedIn) {
        AppSwitcher()
    } else {
        LoginScreen()
    }
}

@Composable
private fun AppSwitcher() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "linking") {
        composable("linking") { LinkScreen(navController) }
        composable("demo") { Greetings() }
    }
}
