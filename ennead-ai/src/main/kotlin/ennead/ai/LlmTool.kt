package ennead.ai

import dev.langchain4j.agent.tool.ToolSpecification
import kairo.reflect.KairoType
import kairo.serialization.typeReference
import osiris.core.osirisMapper
import osiris.core.responseConverter.osirisSchema

public abstract class LlmTool<Input : Any, Output : Any>(
  public val name: String,
) {
  private val inputType: KairoType<Input> = KairoType.from(LlmTool::class, 0, this::class)

  public open val description: String? = null

  internal val toolSpecification: ToolSpecification =
    ToolSpecification.builder().apply {
      name(name)
      if (description != null) description(description)
      parameters(osirisSchema(inputType.kotlinClass))
    }.build()

  internal suspend operator fun invoke(string: String): String {
    val input = osirisMapper.readValue(string, inputType.typeReference)
    val output = invoke(input)
    return osirisMapper.writeValueAsString(output)
  }

  public abstract suspend operator fun invoke(input: Input): Output
}
