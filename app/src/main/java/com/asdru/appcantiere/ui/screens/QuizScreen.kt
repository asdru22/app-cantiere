package com.asdru.appcantiere.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asdru.appcantiere.R
import com.asdru.appcantiere.data.Tool
import com.asdru.appcantiere.data.ToolRepository
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
  sectionIndex: Int,
  repository: ToolRepository,
  onFinish: () -> Unit
) {
  // Game State
  var questions by remember {
    mutableStateOf<List<Pair<Tool, List<Tool>>>>(emptyList())
  }
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }
  var isFinished by remember { mutableStateOf(false) }

  // Feedback state
  var selectedAnswer by remember { mutableStateOf<Tool?>(null) }
  var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

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

  LaunchedEffect(Unit) {
    val tools = repository.getToolsForSection(sectionIndex)

    // UPDATE 1: Set quiz size to half the number of tools available
    // We use coerceAtLeast(1) to ensure the quiz runs even if the category is small
    val numberOfQuestions = (tools.size / 2).coerceAtLeast(1)
    val quizTools = tools.shuffled().take(numberOfQuestions)

    questions = quizTools.map { correct ->
      // UPDATE 2: Get 2 wrong answers instead of 1
      // This creates a total of 3 options (1 correct + 2 wrong)
      val wrong = repository.getRandomTools(2, correct.id)

      // Combine the list of wrong answers with the correct answer
      val options = (wrong + correct).shuffled()
      correct to options
    }
  }

  if (questions.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(stringResource(R.string.ui_caricamento_quiz))
    }
    return
  }

  if (isFinished) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        stringResource(R.string.ui_quiz_completato),
        style = MaterialTheme.typography.headlineMedium
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        stringResource(R.string.ui_punteggio, score, questions.size),
        style = MaterialTheme.typography.displaySmall
      )
      Spacer(modifier = Modifier.height(32.dp))
      Button(onClick = onFinish) {
        Text(stringResource(R.string.ui_home))
      }
    }
  } else {
    val (correctTool, options) = questions[currentQuestionIndex]

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        stringResource(
          R.string.ui_domanda,
          currentQuestionIndex + 1,
          questions.size
        ),
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.height(20.dp))
      Box(
        modifier = Modifier
          .size(250.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.LightGray),
        contentAlignment = Alignment.Center
      ) {
        if (correctTool.imageRes != null) {
          Icon(
            painter = painterResource(id = correctTool.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Color.Unspecified
          )
        } else {
          Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
          )
        }
      }
      Text(
        stringResource(R.string.ui_cosa_rappresenta_immagine),
        modifier = Modifier.padding(top = 16.dp)
      )

      Spacer(modifier = Modifier.weight(1f))

      options.forEach { option ->
        val isSelected = selectedAnswer == option
        val isCorrect = option == correctTool

        val containerColor = if (selectedAnswer != null) {
          when {
            isCorrect -> Color.Green.copy(alpha = 0.7f)
            isSelected -> Color.Red.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.primaryContainer
          }
        } else {
          MaterialTheme.colorScheme.primaryContainer
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = {
              if (selectedAnswer == null) {
                selectedAnswer = option
                isAnswerCorrect = (option == correctTool)
                if (option == correctTool) score++
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = containerColor),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = option.name,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          Spacer(modifier = Modifier.size(8.dp))

          // Audio Button
          IconButton(
            onClick = {
              tts?.speak(
                option.name,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
              )
            },
            modifier = Modifier
              .size(64.dp)
              .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
              )
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Play Name",
              modifier = Modifier.size(24.dp),
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      if (selectedAnswer != null) {
        LaunchedEffect(Unit) {
          delay(1500)
          if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            selectedAnswer = null
            isAnswerCorrect = null
          } else {
            isFinished = true
          }
        }
      }
    }
  }
}