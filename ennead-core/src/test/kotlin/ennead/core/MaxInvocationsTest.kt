package ennead.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Tests [AgentNetworkSafetyCriteria.maxInvocations].
 */
internal class MaxInvocationsTest {
  private val first: Agent<Int> =
    agent("first") {
      custom {
        state--
        if (state > 0) handoff("second")
      }
    }

  private val second: Agent<Int> =
    agent("second") {
      custom {
        state--
        if (state > 0) handoff("first")
      }
    }

  private val network: AgentNetwork<Int> =
    network {
      agent(first)
      agent(second)
    }

  @Test
  fun `max invocations ok`(): Unit = runTest {
    shouldNotThrowAny {
      network.run(initialState = 99, initialAgentName = "first")
    }
  }

  @Test
  fun `max invocations exceeded`(): Unit = runTest {
    shouldThrow<IllegalStateException> {
      network.run(initialState = 100, initialAgentName = "first")
    }.shouldHaveMessage("Maximum invocations (100) exceeded.")
  }
}
