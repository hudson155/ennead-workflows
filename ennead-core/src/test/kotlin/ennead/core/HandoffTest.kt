package ennead.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class HandoffTest {
  private data class State(
    val name: String,
    val result: List<String>,
    val logs: List<String>, // Just for testing.
  ) {
    constructor(name: String) : this(
      name = name,
      result = emptyList(),
      logs = emptyList(),
    )
  }

  private val greetingAgent: Agent<State> =
    agent("greeting") {
      custom {
        state = state.copy(
          result = state.result + "Hi ${state.name}!",
          logs = state.logs + "greeting start",
        )
        handoff("pleasantry")
        state = state.copy(logs = state.logs + "greeting end")
      }
    }

  private val pleasantryAgent: Agent<State> =
    agent("pleasantry") {
      custom {
        state = state.copy(
          result = state.result + "I hope you're doing well today.",
          logs = state.logs + "pleasantry start",
        )
        handoff("question")
        state = state.copy(logs = state.logs + "pleasantry end")
      }
    }

  private val questionAgent: Agent<State> =
    agent("question") {
      custom {
        state = state.copy(
          result = state.result + "Can you help me lift this heavy object?",
          logs = state.logs + "question start",
        )
        state = state.copy(logs = state.logs + "question end")
      }
    }

  private val runner: AgentRunner<State> =
    runner {
      agent(greetingAgent)
      agent(pleasantryAgent)
      agent(questionAgent)
    }

  @Test
  fun `starts with Greeting agent`(): Unit = runTest {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "greeting")
    val expected = State(
      name = "Jeff",
      result = listOf("Hi Jeff!", "I hope you're doing well today.", "Can you help me lift this heavy object?"),
      logs = listOf(
        "greeting start",
        "greeting end",
        "pleasantry start",
        "pleasantry end",
        "question start",
        "question end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Pleasantry agent`(): Unit = runTest {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "pleasantry")
    val expected = State(
      name = "Jeff",
      result = listOf("I hope you're doing well today.", "Can you help me lift this heavy object?"),
      logs = listOf(
        "pleasantry start",
        "pleasantry end",
        "question start",
        "question end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Question agent`(): Unit = runTest {
    val result = runner.run(initialState = State(name = "Jeff"), initialAgentName = "question")
    val expected = State(
      name = "Jeff",
      result = listOf("Can you help me lift this heavy object?"),
      logs = listOf(
        "question start",
        "question end",
      ),
    )
    result.shouldBe(expected)
  }
}
