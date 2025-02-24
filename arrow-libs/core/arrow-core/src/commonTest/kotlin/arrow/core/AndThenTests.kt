package arrow.core

import arrow.core.test.generators.functionAToB
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list

class AndThenTests : FreeSpec() {

  init {
    val count = 500000

    "AndThen0" - {
      "compose a chain of functions with andThen should be same with AndThen" {
        checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
          val result = fs.fold({ i }) { acc, f ->
            { f(acc()) }
          }.invoke()

          val expect = fs.fold({ i }) { acc, f ->
            acc.andThen(f)
          }.invoke()

          result shouldBe expect
        }
      }

      "compose a chain of function with compose should be same with AndThen" {
        checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
          val result = fs.fold({ x: Int -> x }) { acc, f ->
            { x: Int -> acc(f(x)) }
          }.invoke(i)

          val expect = fs.fold({ x: Int -> x }) { acc, b ->
            acc.compose(b)
          }.invoke(i)

          result shouldBe expect
        }
      }

      val count = 500000

      "andThen is stack safe" {
        val result = (0 until count).fold({ 0 }) { acc, _ ->
          acc.andThen { it + 1 }
        }.invoke()

        result shouldBe count
      }

      "toString is stack safe" {
        (0 until count).fold({ x: Int -> x }) { acc, _ ->
          acc.compose { it + 1 }
        }.toString() shouldBe "AndThen.Concat(...)"
      }
    }

    "AndThen1" - {
      "compose a chain of functions with andThen should be same with AndThen" {
        checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
          val result = fs.fold({ x: Int -> x }) { acc, f ->
            { x: Int -> f(acc(x)) }
          }.invoke(i)

          val expect = fs.fold({ x: Int -> x }) { acc, f ->
            acc.andThen(f)
          }.invoke(i)

          result shouldBe expect
        }
      }

      "compose a chain of function with compose should be same with AndThen" {
        checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
          val result = fs.fold({ x: Int -> x }) { acc, f ->
            { x: Int -> acc(f(x)) }
          }.invoke(i)

          val expect = fs.fold({ x: Int -> x }) { acc, b ->
            acc.compose(b)
          }.invoke(i)

          result shouldBe expect
        }
      }

      "andThen is stack safe" {
        val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
          acc.andThen { it + 1 }
        }.invoke(0)

        result shouldBe count
      }

      "compose is stack safe" {
        val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
          acc.compose { it + 1 }
        }.invoke(0)

        result shouldBe count
      }

      "toString is stack safe" {
        (0 until count).fold({ x: Int -> x }) { acc, _ ->
          acc.compose { it + 1 }
        }.toString() shouldBe "AndThen.Concat(...)"
      }
    }

    "AndThen2" - {
      "compose a chain of functions with andThen should be same with AndThen" {
        checkAll(Arb.int(), Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, j, fs ->
          val result = fs.fold({ x: Int, y: Int -> x + y }) { acc, f ->
            { x: Int, y: Int -> f(acc(x, y)) }
          }.invoke(i, j)

          val expect = fs.fold({ x: Int, y: Int -> x + y }) { acc, f ->
            acc.andThen(f)
          }.invoke(i, j)

          result shouldBe expect
        }
      }

      "andThen is stack safe" {
        val result = (0 until count).fold({ x: Int, y: Int -> x + y }) { acc, _ ->
          acc.andThen { it + 1 }
        }.invoke(0, 0)

        result shouldBe count
      }

      "toString is stack safe" {
        (0 until count).fold({ x: Int -> x }) { acc, _ ->
          acc.compose { it + 1 }
        }.toString() shouldBe "AndThen.Concat(...)"
      }
    }
  }
}
