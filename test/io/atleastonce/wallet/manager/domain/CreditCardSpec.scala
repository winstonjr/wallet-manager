package io.atleastonce.wallet.manager.domain

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

import org.scalatestplus.play.PlaySpec

class CreditCardSpec extends PlaySpec {
  "Credit Card creation" should {
    "throws an error if id is null" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(null, "1234123412341234", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if id is empty" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard("", "1234123412341234", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if id is not UUID" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard("asdasd123123qwqasd", "1234123412341234", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if number is null" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, null, "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if number is empty" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if number is not numeric" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "lalala", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if number has not 16 numbers" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "123412341234123", "123", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if cvv is null" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", null, 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if cvv is empty" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "", 15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if cvv is not numeric" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "asd", 15,
          LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if cvv has not 3 numbers" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "1234",
          15, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if due date is not bigger than 28" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "123",
          29, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if due date is less than 1" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "123",
          0, LocalDateTime.now().plusMonths(2L), 1F)
      }
    }

    "throws an error if it is already expired this month" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "123",
          5, LocalDateTime.now(), 1F)
      }
    }

    "throws an error if it is already expired older than this month" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "123",
          5, LocalDateTime.now().minusMonths(2L), 1F)
      }
    }

    "throws an error if credit is less than or equals 0" in {
      a[IllegalArgumentException] must be thrownBy {
        CreditCard(UUID.randomUUID().toString, "1234123412341234", "123",
          5, LocalDateTime.now().plusMonths(2L), 0F)
      }
    }

    "be created if all rules apply" in {
      val id = UUID.randomUUID().toString
      val ed = LocalDateTime.now().plusMonths(2L)
      val cc = CreditCard(id, "1234123412341234", "123", 5, ed, 1F)

      cc.id mustBe id
      cc.number mustBe "1234123412341234"
      cc.cvv mustBe "123"
      cc.dueDate mustBe 5
      cc.expirationDate mustBe ed
      cc.credit mustBe 1
      cc.removed mustBe false
      cc.transactions.isEmpty mustBe true
    }

    "be created even if it is removed" in {
      val id = UUID.randomUUID().toString
      val ed = LocalDateTime.now().plusMonths(2L)
      val cc = CreditCard(id, "1234123412341234", "123", 5, ed, 1F, true)

      cc.id mustBe id
      cc.number mustBe "1234123412341234"
      cc.cvv mustBe "123"
      cc.dueDate mustBe 5
      cc.expirationDate mustBe ed
      cc.credit mustBe 1
      cc.removed mustBe true
      cc.transactions.isEmpty mustBe true
    }

    "be created with transactions inside" in {
      val id = UUID.randomUUID().toString
      val ed = LocalDateTime.now().plusMonths(2L)
      val cc = CreditCard(id, "1234123412341234", "123", 5, ed, 1F, true,
        List(DebitTransaction(50F, LocalDateTime.now)))

      cc.id mustBe id
      cc.number mustBe "1234123412341234"
      cc.cvv mustBe "123"
      cc.dueDate mustBe 5
      cc.expirationDate mustBe ed
      cc.credit mustBe 1
      cc.removed mustBe true
      cc.transactions.isEmpty mustBe false
      cc.transactions.head.getValueWithSignal mustBe (50F)
    }
  }

  "A credit card" should {
    "calculate available credit of the card" in {
      val cc = CreditCard(UUID.randomUUID().toString, "1234123412341234", "123", 5,
        LocalDateTime.now().plusMonths(2L), 1F)

      cc.getAvailableCredit mustBe 1F
    }

    "calculate available credit considering purchase transactions" in {
      val cc = CreditCard(UUID.randomUUID().toString, "1234123412341234", "123", 5,
        LocalDateTime.now().plusMonths(2L), 100F, transactions = List(DebitTransaction(50F)))

      cc.getAvailableCredit mustBe 50F
    }

    "calculate available credit considering payment transactions" in {
      val cc = CreditCard(UUID.randomUUID().toString, "1234123412341234", "123", 5,
        LocalDateTime.now().plusMonths(2L), 100F, transactions = List(PaymentTransaction(50F)))

      cc.getAvailableCredit mustBe 150F
    }

    "calculate available credit considering purchase & payment transactions" in {
      val cc = CreditCard(UUID.randomUUID().toString, "1234123412341234", "123", 5,
        LocalDateTime.now().plusMonths(2L), 100F, transactions = List(DebitTransaction(70F),
          PaymentTransaction(60F)))

      cc.getAvailableCredit mustBe 90F
    }

    "have an option to receive payments" in {
      val cc = CreditCard(UUID.randomUUID().toString, "1234123412341234", "123", 5,
        LocalDateTime.now().plusMonths(2L), 1F)
      val newCC = cc.pay(PaymentTransaction(50L))

      newCC.getAvailableCredit mustBe 51L
    }

    "generate the payment date on this month if date is today" in {
      val now = LocalDateTime.now
      val cc = CreditCard(UUID.randomUUID().toString, "7654765476547654", "123",
        now.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val dueDate = LocalDateTime.of(now.getYear, now.getMonthValue, now.getDayOfMonth, 0, 0, 0)

      cc.getDueDate mustBe dueDate
    }

    "generate the payment date on the next month if date is before today" in {
      val cc = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F,
        transactions = List(DebitTransaction(100F, LocalDateTime.now)))
      val now = LocalDateTime.now
      val dueDate = LocalDateTime.of(now.getYear, now.plusMonths(1L).getMonthValue, cc.dueDate, 0, 0, 0)

      cc.getDueDate mustBe dueDate
    }

    "generate payment date in milliseconds to make easy to do comparisons" in {
      val cc = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val now = LocalDateTime.now
      val dueDate = LocalDateTime.of(now.getYear, now.plusMonths(1L).getMonthValue, cc.dueDate, 0, 0, 0)

      cc.getDueDateMillis mustBe dueDate.toInstant(ZoneOffset.UTC).toEpochMilli
    }

    "must be valid if expiration date is after today" in {
      val cc = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)

      cc.isValid mustBe true
    }

    "must deny a purchase if there is no available credit" in {
      val cc = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)

      cc.purchase(DebitTransaction(1500F)) match {
        case Right(err) =>
          err.getMessage mustBe "Não existe crédito disponível para a compra"
        case _ =>
      }
    }

    "must allow a purchase if there are available credit" in {
      val cc = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)

      cc.purchase(DebitTransaction(1000F)) match {
        case Left(cc) =>
          cc.transactions.length mustBe 1
          cc.transactions.head.value mustBe 1000F
          cc.transactions.head.operation mustBe "debit"
        case Right(_) =>
      }
    }
  }
}
