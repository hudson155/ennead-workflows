package ennead.ai

import dev.langchain4j.data.message.ChatMessage

public data class LlmState(
  val messages: List<ChatMessage>,
)
