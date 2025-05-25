package ennead.core

public fun interface AgentImplementation<State> {
  public suspend fun execute(context: AgentContext<State>): AgentContext<State>
}
