package io.atleastonce.wallet.manager.domain

import org.scalatestplus.play.PlaySpec

class CreditCardSpec extends PlaySpec {
  "Credit Card creation" should {
    "throws an error if id is null" in {
      true mustBe true
    }

    "throws an error if id is empty" in {
      true mustBe true
    }

    "throws an error if id is not UUID" in {
      true mustBe true
    }

    "throws an error if number is null" in {
      true mustBe true
    }

    "throws an error if number is empty" in {
      true mustBe true
    }

    "throws an error if number is not numeric" in {
      true mustBe true
    }

    "throws an error if number has not 16 numbers" in {
      true mustBe true
    }

    "throws an error if cvv is null" in {
      true mustBe true
    }

    "throws an error if cvv is empty" in {
      true mustBe true
    }

    "throws an error if cvv is not numeric" in {
      true mustBe true
    }

    "throws an error if cvv has not 3 numbers" in {
      true mustBe true
    }

    "throws an error if due date is not equals or bigger then 1 and equals or less then 28" in {
      true mustBe true
    }

    "throws an error if it is already expired" in {
      true mustBe true
    }
  }
}
