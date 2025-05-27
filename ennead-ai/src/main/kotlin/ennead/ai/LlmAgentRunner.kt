package ennead.ai

import dev.langchain4j.data.message.UserMessage
import ennead.core.AgentRunner

public typealias LlmAgentRunner = AgentRunner<LlmState>

public suspend fun LlmAgentRunner.run(userMessage: String, initialAgentName: String): LlmState {
  val initialState = LlmState(
    messages = listOf(UserMessage(userMessage)),
  )
  return run(initialState, initialAgentName)
}
