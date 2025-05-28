package ennead.core

/**
 * The implementation for an [Agent]. This typealias makes DSL easier.
 */
public typealias AgentImplementation<State> = suspend AgentReceiver<State>.() -> Unit
