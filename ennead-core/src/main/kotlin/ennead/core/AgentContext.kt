package ennead.core

public data class AgentContext<State>(
  val state: State,
  val currentAgentName: String,
  val nextAgentNames: List<String>,
)
