package ennead.ai

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import ennead.core.AgentBuilder
import ennead.core.custom

public class LlmAgentReceiver internal constructor() {
  public var model: ChatModel? = null
  public var instructions: String? = null
}

public fun AgentBuilder<LlmState>.llm(block: LlmAgentReceiver.() -> Unit) {
  custom {
    val receiver = LlmAgentReceiver()
    receiver.block()
    val model = requireNotNull(receiver.model) { "Model must be provided." }
    val instructions = receiver.instructions
    state = state.copy(
      messages = buildList {
        instructions?.let { add(SystemMessage(it)) }
        addAll(state.messages)
      },
    )
    val request = ChatRequest.builder()
      .messages(state.messages)
      .build()
    val response = model.chat(request)
    state = state.copy(messages = state.messages + response.aiMessage())
  }
}
