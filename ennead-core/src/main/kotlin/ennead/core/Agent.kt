package ennead.core

/**
 * An [Agent] within an [AgentNetwork] just has a [name] and an [implementation].
 */
public class Agent<State> internal constructor(
  public val name: String,
  private val implementation: AgentImplementation<State>,
) {
  /**
   * When executed, the agent transforms the [NetworkContext].
   */
  internal suspend operator fun invoke(context: NetworkContext<State>): NetworkContext<State> {
    val receiver = AgentReceiver(context)
    receiver.implementation()
    return context.transform(receiver)
  }

  private fun NetworkContext<State>.transform(receiver: AgentReceiver<State>): NetworkContext<State> =
    copy(
      state = receiver.state,
      nextAgentNames = receiver.nextAgentNames(nextAgentNames),
    )
}

public class AgentBuilder<State> internal constructor(
  private val name: String,
) {
  public var implementation: AgentImplementation<State>? = null

  internal fun build(): Agent<State> {
    val implementation = requireNotNull(implementation) { "Implementation must be provided." }
    return Agent(
      name = name,
      implementation = implementation,
    )
  }
}

public fun <State> agent(name: String, block: AgentBuilder<State>.() -> Unit): Agent<State> =
  AgentBuilder<State>(name).apply(block).build()
