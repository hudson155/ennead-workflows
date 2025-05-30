package ennead.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Tests the basics of [AgentNetwork], but doesn't test its usage.
 * Usage is covered by other tests.
 */
internal class AgentNetworkTest {
  @Test
  fun `build network, trivial case`(): Unit = runTest {
    shouldNotThrowAny {
      network<Nothing> { }
    }
  }

  @Test
  fun `build network, happy path`(): Unit = runTest {
    val first = agent<Nothing>("first") {
      custom { }
    }
    val second = agent<Nothing>("second") {
      custom { }
    }
    shouldNotThrowAny {
      network {
        agent(first)
        agent(second)
      }
    }
  }

  @Test
  fun `build network, duplicate name`(): Unit = runTest {
    val first = agent<Nothing>("same_name") {
      custom { }
    }
    val second = agent<Nothing>("same_name") {
      custom { }
    }
    shouldThrow<IllegalArgumentException> {
      network {
        agent(first)
        agent(second)
      }
    }.shouldHaveMessage("Duplicate agent with name: same_name.")
  }

  @Test
  fun `run network, no agent`(): Unit = runTest {
    val first = agent<Nothing?>("first") {
      custom { }
    }
    val network = network {
      agent(first)
    }
    shouldThrow<IllegalArgumentException> {
      network.run(initialState = null, initialAgentName = "second")
    }.shouldHaveMessage("No agent with name: second.")
  }

  @Test
  fun `run network, happy path`(): Unit = runTest {
    val first = agent<Nothing?>("first") {
      custom { }
    }
    val network = network {
      agent(first)
    }
    shouldNotThrowAny {
      network.run(initialState = null, initialAgentName = "first")
    }
  }
}
