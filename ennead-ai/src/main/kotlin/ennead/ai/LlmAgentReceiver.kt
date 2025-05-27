package ennead.ai

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import ennead.core.AgentBuilder
import ennead.core.custom

@Suppress("UseDataClass")
public class LlmAgentReceiver internal constructor() {
  public var model: ChatModel? = null
    set(value) {
      requireNotNull(value) { "Cannot set model to null." }
      require(field == null) { "Model has already been set." }
      field = value
    }

  public var instructions: String? = null
    set(value) {
      requireNotNull(value) { "Cannot set instructions to null." }
      require(field == null) { "Instructions have already been set." }
      field = value
    }
}

public fun AgentBuilder<LlmState>.llm(block: LlmAgentReceiver.() -> Unit) {
  custom {
    val receiver = LlmAgentReceiver().apply(block)
    val model = requireNotNull(receiver.model) { "Model must be provided." }
    val instructions = requireNotNull(receiver.instructions) { "Instructions must be provided." }
    state = state.copy(
      messages = buildList {
        add(SystemMessage(instructions))
        addAll(state.messages.filterNot { it is SystemMessage })
      },
    )
    val request = ChatRequest.builder()
      .messages(state.messages)
      .build()
    val response = model.chat(request)
    state = state.copy(messages = state.messages + response.aiMessage())
  }
}
