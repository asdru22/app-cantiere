package com.asdru.appcantiere.data

data class Tool(
  val id: String,
  val name: String,
  val imageRes: Int? = null, // For local drawable resources
  val imageUrl: String? = null, // For network or file paths
  val audioRes: Int? = null // For local raw audio resources
)
