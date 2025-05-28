package ennead.core

/**
 * Safety criteria help avoid runaways (such as infinite loops) within a single [AgentNetwork] run.
 * If one of these criteria is exceeded, an exception is thrown. This is called "safety".
 * You can implement your own criteria by extending this class.
 */
public open class AgentNetworkSafetyCriteria(
  /**
   * The maximum depth for [consult].
   * This is called "depth" since consultations return control to their parent.
   * The initial depth is 0.
   */
  private val maxDepth: Int,
  /**
   * The maximum total number of [Agent]s invoked.
   * If one [Agent] is invoked multiple times, each invocation counts towards this total.
   * The initial invocation is 1.
   */
  private val maxInvocations: Int,
) {
  /**
   * Checks safety, throwing an [IllegalStateException] if one of the criteria is exceeded.
   */
  public open fun check(context: NetworkContext<*>) {
    check(context.invocationDepth < maxDepth) { "Maximum depth ($maxDepth) exceeded." }
    check(context.invocations < maxInvocations) { "Maximum invocations ($maxInvocations) exceeded." }
  }
}

public open class AgentNetworkSafetyCriteriaBuilder {
  public var maxDepth: Int = 10
  public var maxInvocations: Int = 100

  public open fun build(): AgentNetworkSafetyCriteria =
    AgentNetworkSafetyCriteria(
      maxDepth = maxDepth,
      maxInvocations = maxInvocations,
    )
}
