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
      openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
    }
}
