package ennead.ai

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import ennead.core.agent
import ennead.core.runner
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi

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

  private val runner: LlmAgentRunner =
    runner {
      agent(agent)
    }

  @Test
  fun test(): Unit = runTest {
    val result = runner.run(userMessage = "What's 2+2?", initialAgentName = "simple")
    val expected = LlmState(
      messages = listOf(
        SystemMessage("Do the math. Return only the answer (nothing else)."),
        UserMessage("What's 2+2?"),
        AiMessage("4"),
      ),
    )
    result.shouldBe(expected)
  }
}
