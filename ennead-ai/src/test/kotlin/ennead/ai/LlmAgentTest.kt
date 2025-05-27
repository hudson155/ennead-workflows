package ennead.ai

import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import osiris.core.ModelFactory
import osiris.core.modelFactory
import osiris.openAi.openAiApiKey

internal abstract class LlmAgentTest {
  @Suppress("UnnecessaryLet")
  @OptIn(ProtectedString.Access::class)
  protected val modelFactory: ModelFactory =
    modelFactory {
      openAiApiKey =
        requireNotNull(DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]) {
          "OPEN_AI_API_KEY environment variable must be set."
        }.let { ProtectedString(it) }
    }
}
