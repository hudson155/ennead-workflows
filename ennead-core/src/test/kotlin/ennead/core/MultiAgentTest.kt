package ennead.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class MultiAgentTest {
  private val cowAgent: Agent<String?> =
    agent("cow") {
      custom {
        state = "Moo!"
      }
    }

  private val duckAgent: Agent<String?> =
    agent("duck") {
      custom {
        state = "Quack!"
      }
    }

  private val runner: AgentRunner<String?> =
    runner {
      agent(cowAgent)
      agent(duckAgent)
    }

  @Test
  fun `starts with Cow agent`(): Unit = runTest {
    val result = runner.run(initialState = null, initialAgentName = "cow")
    result.shouldBe("Moo!")
  }

  @Test
  fun `starts with Duck agent`(): Unit = runTest {
    val result = runner.run(initialState = null, initialAgentName = "duck")
    result.shouldBe("Quack!")
  }
}
