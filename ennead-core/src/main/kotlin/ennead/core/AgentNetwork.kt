package ennead.core

/**
 * An [AgentNetwork] represents a connected group of [Agent]s, and can perform a run of those [Agent]s.
 */
public class AgentNetwork<State> internal constructor(
  private val agents: Map<String, Agent<State>>,
  private val safetyCriteria: AgentNetworkSafetyCriteria,
) {
  /**
   * Performs a run of the [Agent]s in the [AgentNetwork], starting with the [Agent] that matches [initialAgentName].
   * Returns the final state.
   */
  public suspend fun run(initialState: State, initialAgentName: String): State {
    var context = initialContext(initialState, initialAgentName)
    while (context.nextAgentNames.isNotEmpty()) {
      val currentAgent = getAgent(context.nextAgentNames.first())
      context = context.copy(
        invocations = context.invocations + 1,
        currentAgentName = currentAgent.name,
        nextAgentNames = context.nextAgentNames.drop(1),
      )
      safetyCriteria.check(context)
      context = currentAgent(context)
    }
    return context.state
  }

  private fun initialContext(initialState: State, initialAgentName: String): NetworkContext<State> =
    NetworkContext(
      state = initialState,
      invocations = 0,
      currentAgentName = initialAgentName,
      nextAgentNames = listOf(initialAgentName),
    )

  private fun getAgent(agentName: String): Agent<State> =
    requireNotNull(agents[agentName]) { "No agent with name: $agentName." }
}

public class AgentNetworkBuilder<State> internal constructor() {
  private val agents: MutableMap<String, Agent<State>> = mutableMapOf()

  public var safetyCriteria: AgentNetworkSafetyCriteriaBuilder =
    AgentNetworkSafetyCriteriaBuilder()

  public fun agent(agent: Agent<State>) {
    require(agents[agent.name] == null) { "Duplicate agent with name: ${agent.name}." }
    agents[agent.name] = agent
  }

  public fun safetyCriteria(block: AgentNetworkSafetyCriteriaBuilder.() -> Unit) {
    safetyCriteria.apply(block)
  }

  internal fun build(): AgentNetwork<State> =
    AgentNetwork(
      agents = agents,
      safetyCriteria = safetyCriteria.build(),
    )
}

public fun <State> network(block: AgentNetworkBuilder<State>.() -> Unit): AgentNetwork<State> =
  AgentNetworkBuilder<State>().apply(block).build()
