package ennead.ai

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import ennead.core.AgentBuilder
import ennead.core.custom

public class LlmAgentReceiver internal constructor() {
  public var model: ChatModel? = null
  public var instructions: String? = null
  internal val tools: MutableMap<String, LlmTool<*, *>> = mutableMapOf()

  public fun tool(tool: LlmTool<*, *>) {
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
    val request = ChatRequest.builder().apply {
      messages(state.messages)
      if (receiver.tools.isNotEmpty()) {
        toolSpecifications(receiver.tools.map { it.value.toolSpecification })
      }
    }.build()
    val response = model.chat(request)
    val aiMessage = response.aiMessage()
    state = state.copy(messages = state.messages + aiMessage)
    if (aiMessage.hasToolExecutionRequests()) {
      aiMessage.toolExecutionRequests().forEach { execution ->
        val tool = receiver.tools[execution.name()]!!
        val output = tool(execution.arguments())
        val executionMessage = ToolExecutionResultMessage(execution.id(), execution.name(), output)
        state = state.copy(messages = state.messages + executionMessage)
      }
    }
  }
}
