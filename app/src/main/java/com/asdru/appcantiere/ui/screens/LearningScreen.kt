package com.asdru.appcantiere.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.appcantiere.R
import com.asdru.appcantiere.data.Tool
import com.asdru.appcantiere.data.ToolRepository
import kotlinx.coroutines.launch

@Composable
fun LearningScreen(
  sectionIndex: Int,
  repository: ToolRepository,
  onStartQuiz: () -> Unit
) {
  val tools = repository.getToolsForSection(sectionIndex)

  val pagerState = rememberPagerState(pageCount = { tools.size + 1 })
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  var tts: TextToSpeech? by remember { mutableStateOf(null) }

  DisposableEffect(context) {
    tts = TextToSpeech(context) { status ->
      if (status != TextToSpeech.SUCCESS) {
        Log.e("ERROR", "TTS Shutdown failure")
      }
    }
    onDispose {
      tts?.stop()
      tts?.shutdown()
    }
  }

  HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxSize()
  ) { page ->
    if (page < tools.size) {
      ToolDetailView(
        tool = tools[page],
        onAudioClick = {
          tts?.speak(
            tools[page].name,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
          )
        },
        onNext = {
          scope.launch {
            pagerState.animateScrollToPage(page + 1)
          }
        }
      )
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = stringResource(R.string.ui_sezione_completata),
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
          onClick = onStartQuiz,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
        ) {
          Text(stringResource(R.string.ui_inizia_quiz), fontSize = 18.sp)
        }
      }
    }
  }
}

@Composable
fun ToolDetailView(
  tool: Tool,
  onAudioClick: () -> Unit,
  onNext: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Object Name (Large)
    Text(
      text = tool.name,
      style = MaterialTheme.typography.displayMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(top = 32.dp, bottom = 48.dp)
    )

    // Image
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      if (tool.imageRes != null) {
        Image(
          painter = painterResource(id = tool.imageRes),
          contentDescription = tool.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Fit
        )
      } else {
        // Placeholder
        Icon(
          painter = painterResource(id = android.R.drawable.ic_menu_gallery),
          contentDescription = null,
          modifier = Modifier.size(64.dp),
          tint = Color.Gray
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Audio Button
    IconButton(
      onClick = onAudioClick,
      modifier = Modifier
        .size(72.dp)
        .background(
          MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(50)
        )
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Default.VolumeUp,
        contentDescription = "Play Name",
        modifier = Modifier.size(32.dp),
        tint = MaterialTheme.colorScheme.onPrimaryContainer
      )
    }

    Text(
      text = stringResource(R.string.ui_ascolta),
      style = MaterialTheme.typography.labelLarge,
      modifier = Modifier.padding(top = 8.dp)
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(onClick = onNext) {
      Text(stringResource(R.string.ui_prossimo))
    }
    Spacer(modifier = Modifier.height(16.dp))
  }
}
