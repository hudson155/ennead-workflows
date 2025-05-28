package ennead.ai

import dev.langchain4j.data.message.UserMessage
import ennead.core.AgentNetwork

public typealias LlmAgentNetwork = AgentNetwork<LlmState>

public suspend fun LlmAgentNetwork.run(userMessage: String, initialAgentName: String): LlmState {
  val initialState = LlmState(
    messages = listOf(UserMessage(userMessage)),
  )
  return run(initialState, initialAgentName)
}
