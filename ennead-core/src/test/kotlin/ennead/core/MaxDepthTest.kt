package ennead.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Tests [AgentNetworkSafetyCriteria.maxDepth].
 */
internal class MaxDepthTest {
  private val first: Agent<Int> =
    agent("first") {
      custom {
        state--
        if (state > 0) consult("second")
      }
    }

  private val second: Agent<Int> =
    agent("second") {
      custom {
        state--
        if (state > 0) consult("first")
      }
    }

  private val network: AgentNetwork<Int> =
    network {
      agent(first)
      agent(second)
    }

  @Test
  fun `max depth ok`(): Unit = runTest {
    shouldNotThrowAny {
      network.run(initialState = 10, initialAgentName = "first")
    }
  }

  @Test
  fun `max depth exceeded`(): Unit = runTest {
    shouldThrow<IllegalStateException> {
      network.run(initialState = 11, initialAgentName = "first")
    }.shouldHaveMessage("Maximum depth (10) exceeded.")
  }
}
