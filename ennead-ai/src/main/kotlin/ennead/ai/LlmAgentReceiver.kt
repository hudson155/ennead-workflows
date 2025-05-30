package ennead.ai

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import ennead.core.AgentBuilder
import ennead.core.custom
import osiris.core.OsirisEvent
import osiris.core.OsirisTool
import osiris.core.osiris

public class LlmAgentReceiver internal constructor() {
  public var model: ChatModel? = null
  public var instructions: String? = null
  internal val tools: MutableMap<String, OsirisTool<*, *>> = mutableMapOf()

  public fun tool(tool: OsirisTool<*, *>) {
    require(tool.name !in tools) { "Duplicate tool with name: ${tool.name}." }
    tools[tool.name] = tool
  }
}

public fun AgentBuilder<LlmState>.llm(block: LlmAgentReceiver.() -> Unit) {
  custom {
    val receiver = LlmAgentReceiver()
    receiver.block()
    val model = requireNotNull(receiver.model) { "Model must be provided." }
    val instructions = receiver.instructions
    state = state.copy(
      messages = buildList {
        addAll(state.messages)
        if (instructions != null) add(SystemMessage(instructions))
      },
    )
    val response = osiris(
      model = model,
      messages = state.messages,
      tools = receiver.tools,
    )
    response.collect { event ->
      when (event) {
        is OsirisEvent.Message -> {
          state = state.copy(messages = state.messages + event.message)
        }
      }
    }
  }
}
