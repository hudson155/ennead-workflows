package ennead.ai.testing

import com.fasterxml.jackson.module.kotlin.readValue
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.ToolExecutionResultMessage
import dev.langchain4j.data.message.UserMessage
import ennead.ai.LlmState
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import osiris.core.osirisMapper

public class MessageVerifier {
  public val verifiers: MutableList<(message: ChatMessage) -> Unit> = mutableListOf()
}

public fun LlmState.verifyMessages(block: MessageVerifier.() -> Unit) {
  val verifier = MessageVerifier()
  verifier.block()
  verifier.verifiers.zip(messages).forEach { (verify, message) ->
    verify(message)
  }
  if (messages.size != verifier.verifiers.size) {
    fail(
      buildList {
        add("Expected ${verifier.verifiers.size} messages, but got ${messages.size}.")
        add("All previous messages matched.")
        if (messages.size > verifier.verifiers.size) {
          add("The following messages were not verified:")
          addAll(messages.subList(verifier.verifiers.size, messages.size))
        }
      }.joinToString(" "),
    )
  }
}

public fun MessageVerifier.verifySystemMessage(exactly: String) {
  verifiers += { message ->
    message.shouldBeInstanceOf<SystemMessage>()
    message.text().shouldBe(exactly)
  }
}

public fun MessageVerifier.verifyUserMessage(exactly: String) {
  verifiers += { message ->
    message.shouldBeInstanceOf<UserMessage>()
    message.singleText().shouldBe(exactly)
  }
}

public fun MessageVerifier.verifyAiMessage(exactly: String) {
  verifiers += { message ->
    message.shouldBeInstanceOf<AiMessage>()
    message.text().shouldNotBeNull()
    message.hasToolExecutionRequests().shouldBeFalse()
    message.text().shouldBe(exactly)
  }
}

public fun MessageVerifier.verifyAiMessage(block: ToolCallReceiver.() -> Unit) {
  val receiver = ToolCallReceiver()
  receiver.block()
  verifiers += { message ->
    message.shouldBeInstanceOf<AiMessage>()
    message.text().shouldBeNull()
    message.hasToolExecutionRequests().shouldBeTrue()
    message.toolExecutionRequests().forEach { toolExecutionRequest ->
      withClue("Tool execution request $toolExecutionRequest was not expected.") {
        receiver.toolCalls.shouldForOne { (name, arguments) ->
          toolExecutionRequest.id().shouldNotBeNull() // Not verified.
          toolExecutionRequest.name().shouldBe(name)
          arguments(toolExecutionRequest.arguments())
        }
      }
    }
  }
}

public inline fun <reified Output : Any> MessageVerifier.verifyToolMessage(toolName: String, output: Output) {
  verifiers += { message ->
    message.shouldBeInstanceOf<ToolExecutionResultMessage>()
    message.id().shouldNotBeNull() // Not verified.
    message.toolName().shouldBe(toolName)
    osirisMapper.readValue<Output>(message.text()).shouldBe(output)
  }
}
