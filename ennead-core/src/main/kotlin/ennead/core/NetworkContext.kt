package ennead.core

/**
 * Shared context for the [AgentNetwork].
 */
public data class NetworkContext<State>(
  /**
   * The current [State] for each [Agent] to operate on.
   */
  val state: State,
  /**
   * The total number of [Agent]s invoked.
   * If one [Agent] is invoked multiple times, each invocation counts towards this total.
   * The initial invocation is 1.
   */
  val invocations: Int,
  /**
   * The [Agent.name] of the [Agent] for the current invocation.
   */
  val currentAgentName: String,
  /**
   * The [Agent.name]s of the [Agent]s in the [AgentNetwork] to be invoked next, in order.
   */
  val nextAgentNames: List<String>,
) {
  /**
   * The depth for [consult].
   * This is called "depth" since consultations return control to their parent.
   * The initial depth is 0.
   */
  public val invocationDepth: Int = nextAgentNames.size
}
