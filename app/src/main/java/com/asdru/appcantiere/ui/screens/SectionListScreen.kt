package com.asdru.appcantiere.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.asdru.appcantiere.R
import com.asdru.appcantiere.data.ToolRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionListScreen(
  repository: ToolRepository,
  onSectionClick: (Int) -> Unit
) {
  val sections = repository.getSections()

  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.ui_app_desc)) })
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(sections.size) { index ->
        val category = sections[index]
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSectionClick(index) },
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          )
        ) {
          Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(24.dp)
          )
        }
      }
    }
  }
}
