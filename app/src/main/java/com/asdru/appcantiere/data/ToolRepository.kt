package com.asdru.appcantiere.data

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

data class ToolCategory(
  val name: String,
  val tools: List<Tool>
)

class ToolRepository(private val context: Context) {

  private val categories: List<ToolCategory> by lazy {
    loadToolsFromAssets()
  }

  private fun loadToolsFromAssets(): List<ToolCategory> {
    val jsonString = try {
      val inputStream = context.assets.open("tools_data.json")
      val reader = BufferedReader(InputStreamReader(inputStream))
      reader.readText()
    } catch (e: Exception) {
      e.printStackTrace()
      return emptyList()
    }

    val categoriesList = mutableListOf<ToolCategory>()
    try {
      val jsonObject = JSONObject(jsonString)
      val jsonArray = jsonObject.getJSONArray("glossario_cantiere")

      for (i in 0 until jsonArray.length()) {
        val categoryObject = jsonArray.getJSONObject(i)
        val rawCategoryName = categoryObject.getString("categoria")
        val normalizedCategoryName = normalizeId(rawCategoryName)
        val categoryNameResId = context.resources.getIdentifier(
          normalizedCategoryName,
          "string",
          context.packageName
        )
        val categoryName = if (categoryNameResId != 0) {
          context.getString(categoryNameResId)
        } else {
          rawCategoryName
        }
        val elementsArray = categoryObject.getJSONArray("elementi")

        val toolsList = mutableListOf<Tool>()
        for (j in 0 until elementsArray.length()) {
          val rawId = elementsArray.getString(j)
          val normalizedId = normalizeId(rawId)

          val nameResId =
            context.resources.getIdentifier(
              normalizedId,
              "string",
              context.packageName
            )
          val imageResId =
            context.resources.getIdentifier(
              normalizedId,
              "drawable",
              context.packageName
            )

          val name = if (nameResId != 0) context.getString(nameResId) else rawId
          val imageRes = if (imageResId != 0) imageResId else null

          toolsList.add(
            Tool(
              id = normalizedId,
              name = name,
              imageRes = imageRes
            )
          )
        }
        categoriesList.add(ToolCategory(categoryName, toolsList))
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return categoriesList
  }

  private fun normalizeId(id: String): String {
    return id.lowercase().replace(" ", "_").replace("'", "_")
  }

  fun getSections(): List<ToolCategory> {
    return categories
  }

  fun getToolsForSection(sectionIndex: Int): List<Tool> {
    if (sectionIndex < 0 || sectionIndex >= categories.size) {
      return emptyList()
    }
    return categories[sectionIndex].tools
  }

  fun getRandomTools(count: Int, excludeId: String): List<Tool> {
    val allTools = categories.flatMap { it.tools }
    return allTools.filter { it.id != excludeId }.shuffled().take(count)
  }
}
