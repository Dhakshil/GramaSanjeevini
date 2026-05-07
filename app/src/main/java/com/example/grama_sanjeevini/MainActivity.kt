package com.example.grama_sanjeevini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.grama_sanjeevini.constants.theme.GramaSanjeeviniTheme
import com.example.grama_sanjeevini.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            // null = follow system, true = force dark, false = force light
            var darkModeOverride by remember { mutableStateOf<Boolean?>(null) }
            val isDark = darkModeOverride ?: systemDark

            GramaSanjeeviniTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    isDark = isDark,
                    onToggleDarkMode = { darkModeOverride = if (isDark) false else true }
                )
            }
        }
    }
}
