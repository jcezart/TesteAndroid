// MainActivity.kt
package com.example.testeandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.testeandroid.core.navigation.App
import com.example.testeandroid.core.presentation.ui.theme.TesteAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TesteAndroidTheme {
                // Surface para aplicar o tema
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    App()
                }
            }
        }
    }
}


