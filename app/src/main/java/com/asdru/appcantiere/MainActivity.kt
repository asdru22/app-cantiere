package com.asdru.appcantiere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.asdru.appcantiere.data.ToolRepository
import com.asdru.appcantiere.ui.ToolApp
import com.asdru.appcantiere.ui.theme.AppCantiereTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = ToolRepository(applicationContext)

    setContent {
      AppCantiereTheme {
        ToolApp(repository = repository)
      }
    }
  }
}