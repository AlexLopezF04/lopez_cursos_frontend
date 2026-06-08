package com.alexlopez.cursosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.presentation.navigation.NavGraph
import com.alexlopez.cursosapp.presentation.theme.CursosAppTheme
import com.alexlopez.cursosapp.presentation.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursosAppTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                NavGraph(authViewModel = authViewModel)
            }
        }
    }
}
