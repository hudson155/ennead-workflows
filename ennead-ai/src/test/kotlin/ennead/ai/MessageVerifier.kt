package ennead.ai

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

internal class MessageVerifier {
  val verifiers: MutableList<(message: ChatMessage) -> Unit> = mutableListOf()

  fun verifySystemMessage(exactly: String) {
    verifiers += { it.shouldBe(SystemMessage(exactly)) }
  }

  fun verifyUserMessage(exactly: String) {
    verifiers += { it.shouldBe(UserMessage(exactly)) }
  }

  fun verifyAiMessage(exactly: String) {
    verifiers += { it.shouldBe(AiMessage(exactly)) }
  }
}

internal fun LlmState.verifyMessages(block: MessageVerifier.() -> Unit) {
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
      }.joinToString(" ")
    )
  }
}
