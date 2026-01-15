package com.asdru.appcantiere.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.asdru.appcantiere.data.ToolRepository
import com.asdru.appcantiere.ui.screens.LearningScreen
import com.asdru.appcantiere.ui.screens.QuizScreen
import com.asdru.appcantiere.ui.screens.SectionListScreen

@Composable
fun ToolApp(repository: ToolRepository) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = Routes.HOME) {
    composable(Routes.HOME) {
      SectionListScreen(
        repository = repository,
        onSectionClick = { index ->
          navController.navigate(Routes.learning(index))
        }
      )
    }

    composable(
      route = Routes.LEARNING,
      arguments = listOf(navArgument("sectionIndex") { type = NavType.IntType })
    ) { backStackEntry ->
      val sectionIndex = backStackEntry.arguments?.getInt("sectionIndex") ?: 0
      LearningScreen(
        sectionIndex = sectionIndex,
        repository = repository,
        onStartQuiz = {
          navController.navigate(Routes.quiz(sectionIndex))
        }
      )
    }

    composable(
      route = Routes.QUIZ,
      arguments = listOf(navArgument("sectionIndex") { type = NavType.IntType })
    ) { backStackEntry ->
      val sectionIndex = backStackEntry.arguments?.getInt("sectionIndex") ?: 0
      QuizScreen(
        sectionIndex = sectionIndex,
        repository = repository,
        onFinish = {
          navController.popBackStack(Routes.HOME, inclusive = false)
        }
      )
    }
  }
}
