package ennead.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Trivial usage test for a single-agent scenario.
 */
internal class SingleAgentTest {
  private val agent: Agent<String?> =
    agent("trivial") {
      custom {
        state = "I'm a trivial agent!"
      }
    }

  private val network: AgentNetwork<String?> =
    network {
      agent(agent)
    }

  @Test
  fun test(): Unit = runTest {
    val result = network.run(initialState = null, initialAgentName = "trivial")
    result.shouldBe("I'm a trivial agent!")
  }
}
