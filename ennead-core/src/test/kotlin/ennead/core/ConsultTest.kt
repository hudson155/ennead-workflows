package ennead.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ConsultTest {
  private data class State(
    val inputs: List<Int>,
    val sum: Int?,
    val result: String?,
    val logs: List<String>, // Just for testing.
  ) {
    constructor(inputs: List<Int>) : this(
      inputs = inputs,
      sum = null,
      result = null,
      logs = emptyList(),
    )
  }

  private val exampleAgent: Agent<State> =
    agent("example") {
      custom {
        state = state.copy(
          logs = state.logs + "example start",
        )
        if (state.sum == null) {
          consult("math")
        }
        state = state.copy(
          result = state.sum?.let { "The sum is $it." } ?: state.result,
          logs = state.logs + "example end",
        )
      }
    }

  private val mathAgent: Agent<State> =
    agent("math") {
      custom {
        state = state.copy(
          logs = state.logs + "math start",
        )
        if (state.sum == null) {
          consult("sum")
        }
        state = state.copy(
          logs = state.logs + "math end",
        )
      }
    }

  private val sumAgent: Agent<State> =
    agent("sum") {
      custom {
        state = state.copy(
          logs = state.logs + "sum start",
        )
        state = state.copy(
          sum = state.inputs.sum(),
          logs = state.logs + "sum end",
        )
      }
    }

  private val runner: AgentRunner<State> =
    runner {
      agent(exampleAgent)
      agent(mathAgent)
      agent(sumAgent)
    }

  @Test
  fun `starts with Example agent`(): Unit = runTest {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "example")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = "The sum is 6.",
      logs = listOf(
        "example start",
        "example end",
        "math start",
        "math end",
        "sum start",
        "sum end",
        "math start",
        "math end",
        "example start",
        "example end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Math agent`(): Unit = runTest {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "math")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = null,
      logs = listOf(
        "math start",
        "math end",
        "sum start",
        "sum end",
        "math start",
        "math end",
      ),
    )
    result.shouldBe(expected)
  }

  @Test
  fun `starts with Sum agent`(): Unit = runTest {
    val result = runner.run(initialState = State(inputs = listOf(1, 2, 3)), initialAgentName = "sum")
    val expected = State(
      inputs = listOf(1, 2, 3),
      sum = 6,
      result = null,
      logs = listOf(
        "sum start",
        "sum end",
      ),
    )
    result.shouldBe(expected)
  }
}
