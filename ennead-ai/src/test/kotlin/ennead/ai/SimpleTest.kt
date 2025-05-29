package ennead.ai

import ennead.ai.testing.verifyAiMessage
import ennead.ai.testing.verifyMessages
import ennead.ai.testing.verifySystemMessage
import ennead.ai.testing.verifyUserMessage
import ennead.core.agent
import ennead.core.network
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi

/**
 * Tests an [LlmAgentNetwork] with a single [LlmAgent].
 */
internal class SimpleTest : LlmAgentTest() {
  private val agent: LlmAgent =
    agent("simple") {
      llm {
        model = modelFactory.openAi("gpt-4.1-nano") {
          temperature(0.20)
        }
        instructions = "Do the math. Return only the answer (nothing else)."
      }
    }

  private val network: LlmAgentNetwork =
    network {
      agent(agent)
    }

  @Test
  fun test(): Unit = runTest {
    val result = network.run(userMessage = "What's 2+2?", initialAgentName = "simple")
    result.verifyMessages {
      verifySystemMessage("Do the math. Return only the answer (nothing else).")
      verifyUserMessage("What's 2+2?")
      verifyAiMessage("4")
    }
  }
}
