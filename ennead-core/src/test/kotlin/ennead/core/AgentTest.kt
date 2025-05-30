package ennead.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Tests the basics of [Agent], but doesn't test its usage.
 * Usage is covered by other tests.
 */
internal class AgentTest {
  @Test
  fun `build agent, trivial case`(): Unit = runTest {
    shouldThrow<IllegalArgumentException> {
      agent<Nothing>("example") { }
    }.shouldHaveMessage("Implementation must be provided.")
  }

  @Test
  fun `build agent, happy path`(): Unit = runTest {
    shouldNotThrowAny {
      agent<Nothing>("first") {
        custom { }
      }
    }
  }
}
