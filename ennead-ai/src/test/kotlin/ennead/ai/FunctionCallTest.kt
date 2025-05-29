package ennead.ai

import ennead.ai.testing.toolCall
import ennead.ai.testing.verifyAiMessage
import ennead.ai.testing.verifyMessages
import ennead.ai.testing.verifyToolMessage
import ennead.ai.testing.verifyUserMessage
import ennead.core.agent
import ennead.core.network
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.core.schema.OsirisSchema
import osiris.openAi.openAi

internal class FunctionCallTest : LlmAgentTest() {
  internal object WeatherTool : LlmTool<WeatherTool.Input, WeatherTool.Output>("weather") {
    data class Input(
      @OsirisSchema.Type("string")
      @OsirisSchema.Description("The city to get the weather for. Only the city name.")
      val location: String,
    )

    data class Output(
      @OsirisSchema.Type("string")
      val temperature: String,
      @OsirisSchema.Type("string")
      val conditions: String,
    )

    override suspend fun invoke(input: Input): Output {
      input.shouldBe(Input("Edmonton"))
      return Output(
        temperature = "-30 degrees Celsius",
        conditions = "Snowing",
      )
    }
  }

  private val weatherService: LlmAgent =
    agent("weather_service") {
      llm {
        model = modelFactory.openAi("gpt-4.1-nano") {
          temperature(0.20)
        }
        tool(WeatherTool)
      }
    }

  private val network: LlmAgentNetwork =
    network {
      agent(weatherService)
    }

  @Test
  fun test(): Unit = runTest {
    val result = network.run(userMessage = "What's the weather in Edmonton?", initialAgentName = "weather_service")
    result.verifyMessages {
      verifyUserMessage("What's the weather in Edmonton?")
      verifyAiMessage {
        toolCall("weather", WeatherTool.Input("Edmonton"))
      }
      verifyToolMessage("weather", WeatherTool.Output(temperature = "-30 degrees Celsius", conditions = "Snowing"))
    }
  }
}
