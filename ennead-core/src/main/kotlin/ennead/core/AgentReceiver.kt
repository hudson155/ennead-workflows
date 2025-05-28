package ennead.core

/**
 * The invocation-time receiver for an [Agent], providing access to [state] and transitions.
 */
public class AgentReceiver<State> internal constructor(
  internal val context: NetworkContext<State>,
) {
  public var state: State = context.state
  internal var transition: (MutableList<String>.(nextAgentNames: List<String>) -> Unit) = { addAll(it) }

  internal fun nextAgentNames(nextAgentNames: List<String>): List<String> =
    buildList { transition(nextAgentNames) }
}

/**
 * DSL for custom [AgentImplementation]s.
 */
public fun <State> AgentBuilder<State>.custom(block: AgentImplementation<State>) {
  implementation = block
}

/**
 * DSL for handoff-style transitions.
 * Control will be passed to the [Agent] with the given [agentName].
 */
public fun <State> AgentReceiver<State>.handoff(agentName: String) {
  transition = { nextAgentNames ->
    add(agentName)
    addAll(nextAgentNames)
  }
}

/**
 * DSL for handoff-style transitions.
 * Control will be passed to the [Agent] with the given [agentName],
 * and then passed back to the current [Agent].
 */
public fun <State> AgentReceiver<State>.consult(agentName: String) {
  transition = {
    add(agentName)
    add(context.currentAgentName)
    addAll(context.nextAgentNames)
  }
}
