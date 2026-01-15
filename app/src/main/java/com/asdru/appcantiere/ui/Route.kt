package com.asdru.appcantiere.ui

object Routes {
  const val HOME = "home"
  const val LEARNING = "learning/{sectionIndex}"
  const val QUIZ = "quiz/{sectionIndex}"

  fun learning(sectionIndex: Int) = "learning/$sectionIndex"
  fun quiz(sectionIndex: Int) = "quiz/$sectionIndex"
}
