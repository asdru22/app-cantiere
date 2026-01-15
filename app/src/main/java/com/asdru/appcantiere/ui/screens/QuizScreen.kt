package com.asdru.appcantiere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  var questions by remember { mutableStateOf<List<Pair<Tool, List<Tool>>>>(emptyList()) }
  var currentQuestionIndex by remember { mutableStateOf(0) }
  var score by remember { mutableStateOf(0) }
  var isFinished by remember { mutableStateOf(false) }

  // Feedback state
  var selectedAnswer by remember { mutableStateOf<Tool?>(null) }
  var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

  LaunchedEffect(Unit) {
    val tools = repository.getToolsForSection(sectionIndex)
    // Pick 5 random tools for the quiz
    val quizTools = tools.shuffled().take(5)

    // Prepare questions: Each question has 1 correct tool and 1 wrong tool
    questions = quizTools.map { correct ->
      val wrong = repository.getRandomTools(1, correct.id).first()
      val options = listOf(correct, wrong).shuffled()
      correct to options
    }
  }

  if (questions.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Caricamento Quiz...")
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
      Text("Quiz Completato!", style = MaterialTheme.typography.headlineMedium)
      Spacer(modifier = Modifier.height(16.dp))
      Text("Punteggio: $score / ${questions.size}", style = MaterialTheme.typography.displaySmall)
      Spacer(modifier = Modifier.height(32.dp))
      Button(onClick = onFinish) {
        Text("Torna alla Home")
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
      Text(
        "Domanda ${currentQuestionIndex + 1} / ${questions.size}",
        style = MaterialTheme.typography.titleMedium
      )
      Spacer(modifier = Modifier.height(32.dp))

      // Initial Request said: "randomly choosing from previous videos... show image... button corresponding to tool used"
      // Interpreting: Play sound/video -> User guesses tool.
      // But spec said: "section where name (audio), image... end of section quiz where 5 videos ... click button corresponding to tool"
      // Since we don't have video/audio really working, I will show the VIDEO/IMAGE frame and ask "Which tool is this?"
      // OR play audio and ask "Which tool is this?"
      // Given "click button corresponding to tool used", I will assume we show the media (Image representing the video) and user clicks the name.

      Box(
        modifier = Modifier
          .size(200.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.LightGray),
        contentAlignment = Alignment.Center
      ) {
        if (correctTool.imageRes != null) {
          Icon(
            painter = painterResource(id = correctTool.imageRes),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
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
      Text("Qual è questo strumento?", modifier = Modifier.padding(top = 16.dp))

      Spacer(modifier = Modifier.weight(1f))

      options.forEach { option ->
        val isSelected = selectedAnswer == option
        val isCorrect = option == correctTool

        // Color logic:
        // If answer selected:
        //   If this option is satisfied correct -> Green
        //   If this option is selected & wrong -> Red
        val containerColor = if (selectedAnswer != null) {
          when {
            isCorrect -> Color.Green.copy(alpha = 0.7f)
            isSelected -> Color.Red.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.primaryContainer
          }
        } else {
          MaterialTheme.colorScheme.primaryContainer
        }

        Button(
          onClick = {
            if (selectedAnswer == null) {
              selectedAnswer = option
              isAnswerCorrect = (option == correctTool)
              if (option == correctTool) score++
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
      }

      Spacer(modifier = Modifier.height(32.dp))

      if (selectedAnswer != null) {
        // Auto advance or button to advance
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
