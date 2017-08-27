package io.atleastonce.wallet.manager.domain

import java.time.LocalDateTime

import org.scalatestplus.play.PlaySpec

class WalletSpec extends PlaySpec {
  "Wallet creation" should {
    "throws an Error if id is null" in {
      a[IllegalArgumentException] must be thrownBy {
        Wallet(null, 0F)
      }
    }

    "throws an Error if id is empty" in {
      a[IllegalArgumentException] must be thrownBy {
        Wallet("", 0F)
      }
    }

    "throws an Error if id is not an UUID" in {
      a[IllegalArgumentException] must be thrownBy {
        Wallet("asdqwe", 0F)
      }
    }

    "throws an Error if credit is less than 0" in {
      a[IllegalArgumentException] must be thrownBy {
        Wallet("38e16da6-fee6-4053-9633-a499182ddc43", -0.00001F)
      }
    }

    "throws an error if credit is greater than credit cards sum limit (no credit cards added)" in {
      a[AssertionError] must be thrownBy {
        Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1F)
      }
    }

    "throws an error if credit is greater than credit cards sum limit" in {
      a[AssertionError] must be thrownBy {
        Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 100.001F, List(CreditCard(
          "5febb7b0-4082-4138-aced-7189e1cc464a",
          "1234123412341234",
          "123",
          15,
          LocalDateTime.now().plusYears(5L),
          100F
        )))
      }
    }

    "must be created correctly if all parameters are ok and credit is equals 0" in {
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 0F)

      wallet.id mustBe "38e16da6-fee6-4053-9633-a499182ddc43"
      wallet.credit mustBe 0F
    }

    "must be created correctly if all parameters are ok and credit greater than 0" in {
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1000F,  List(CreditCard(
        "5febb7b0-4082-4138-aced-7189e1cc464a",
        "1234123412341234",
        "123",
        15,
        LocalDateTime.now().plusYears(5L),
        1001F
      )))

      wallet.id mustBe "38e16da6-fee6-4053-9633-a499182ddc43"
      wallet.credit mustBe 1000F
    }
  }

  "A wallet" should {
    "be created with one credit card" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 1001F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1000F, List(cc1))

      wallet.cards.length mustBe 1
      wallet.cards.head.id mustBe "5febb7b0-4082-4138-aced-7189e1cc464a"
      wallet.cards.head.credit mustBe 1001F
    }

    "be created without credit card and then add one" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 1001F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 0F)
      wallet.addCard(cc1) match {
        case Left(newWallet) =>
          newWallet.cards.length mustBe 1
          newWallet.cards.head.id mustBe "5febb7b0-4082-4138-aced-7189e1cc464a"
          newWallet.cards.head.credit mustBe 1001F
        case _ =>
      }
    }

    "have the maximum limit as result of all credit cards available" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 0F, List(cc1, cc2))

      wallet.getMaximumLimit mustBe 1000F
    }

    "return an error if a new limit set above maximum limit of the wallet" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1000F, List(cc1, cc2))

      wallet.updateLimit(1500F) match {
        case Right(error) =>
          error.getMessage mustBe "O novo valor ultrapassa o valor máximo de crédito disponível. 1500.0 > 1000.0"
        case _ =>
      }
    }

    "works properly if the new limit is bellow maximum limit" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1000F, List(cc1, cc2))

      wallet.updateLimit(900F) match {
        case Left(newWallet) =>
          newWallet.credit mustBe 900F
        case _ =>
      }
    }

    "return an error if the credit card does not exist" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 400F, List(cc1, cc2))


      wallet.removeCard("9876987698769876") match {
        case Right(error) =>
          error.getMessage mustBe "Cartão de Crédito não encontrado"
        case _ =>
      }
    }

    "return an error if the limit of credit is greater than maximum limit" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1000F, List(cc1, cc2))

      wallet.removeCard("4321432143214321") match {
        case Right(error) =>
          error.getMessage mustBe "Não é possível remover o cartão no momento." +
            " Primeiro reduza o limite de crédito da carteira"
        case _ =>
      }
    }

    "works properly if the card exists" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 400F, List(cc1, cc2))

      wallet.removeCard("4321432143214321") match {
        case Left(newWallet) =>
          newWallet.cards.length mustBe 1
        case _ =>
      }
    }

    "have the right limit when transactions are defined" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(100F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(200F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 400F, List(cc1, cc2))

      wallet.getAvailableCredit mustBe 800F
    }

    "could not select credit cards for buy if the ammount is bigger than all available limit" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(100F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(200F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 400F, List(cc1, cc2))

      wallet.selectBestCreditCard(801) match {
        case Right(error) =>
          error.getMessage mustBe "O valor da compra é maior que o crédito disponível na carteira"
        case _ =>
      }
    }

    "select the card with lower limit and farthest due date" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1500F, List(cc1, cc2, cc3))

      wallet.selectBestCreditCard(500F) match {
        case Left(cc) =>
          cc.head.credit mustBe 700F
          cc.head.number mustBe "1234123412341234"
        case _ =>
      }
    }

    "select 2 credit cards for the payment with lower limit and farthest due date" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1500F, List(cc1, cc2, cc3))

      wallet.selectBestCreditCard(1001F) match {
        case Left(ccs) =>
          ccs.head.credit mustBe 700F
          ccs.head.number mustBe "1234123412341234"

          ccs(1).credit mustBe 1000F
          ccs(1).number mustBe "7654765476547654"
        case _ =>
      }
    }

    "select 3 credit cards for the payment with lower limit and farthest due date" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 2200F, List(cc1, cc2, cc3))

      wallet.selectBestCreditCard(2000F) match {
        case Left(ccs) =>
          ccs.head.credit mustBe 700F
          ccs.head.number mustBe "1234123412341234"

          ccs(1).credit mustBe 1000F
          ccs(1).number mustBe "7654765476547654"

          ccs(2).credit mustBe 500F
          ccs(2).number mustBe "4321432143214321"
        case _ =>
      }
    }

    "select 3 credit cards for the payment lower limit and farthest due date" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F,
        transactions = List(PaymentTransaction(50F, LocalDateTime.now)))
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(PaymentTransaction(100F, LocalDateTime.now)))
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 2200F, List(cc1, cc2, cc3))

      wallet.selectBestCreditCard(2000F) match {
        case Left(ccs) =>
          ccs.head.credit mustBe 700F
          ccs.head.number mustBe "1234123412341234"

          ccs(1).credit mustBe 1000F
          ccs(1).number mustBe "7654765476547654"

          ccs(2).credit mustBe 500F
          ccs(2).number mustBe "4321432143214321"
        case _ =>
      }
    }

    "could not generate transactions if the ammount is bigger than all available limit" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(100F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        15, LocalDateTime.now().plusYears(5L), 500F,
        transactions = List(DebitTransaction(200F, LocalDateTime.now), PaymentTransaction(50F, LocalDateTime.now)))
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 400F, List(cc1, cc2))

      wallet.purchase(801) match {
        case Right(error) =>
          error.getMessage mustBe "O valor da compra é maior que o crédito disponível na carteira"
        case _ =>
      }
    }

    "select one card with lower limit and farthest due date" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1500F, List(cc1, cc2, cc3))

      wallet.purchase(500F) match {
        case Left(cc) =>
          cc.head._1.credit mustBe 700F
          cc.head._1.number mustBe "1234123412341234"
          cc.head._2.value mustBe 500F
        case _ =>
      }
    }

    "select 2 credit cards for the payment with 700 in one and 301 in another transaction" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 1500F, List(cc1, cc2, cc3))

      wallet.purchase(1001F) match {
        case Left(ccs) =>
          ccs.head._1.credit mustBe 700F
          ccs.head._1.number mustBe "1234123412341234"
          ccs.head._2.value mustBe 700F

          ccs(1)._1.credit mustBe 1000F
          ccs(1)._1.number mustBe "7654765476547654"
          ccs(1)._2.value mustBe 301F
        case _ =>
      }
    }

    "select 3 credit cards for the payment with transactions of 700, 1000 and 300" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F)
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F)
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 2200F, List(cc1, cc2, cc3))

      wallet.purchase(2000F) match {
        case Left(ccs) =>
          ccs.head._1.credit mustBe 700F
          ccs.head._1.number mustBe "1234123412341234"
          ccs.head._2.value mustBe 700F

          ccs(1)._1.credit mustBe 1000F
          ccs(1)._1.number mustBe "7654765476547654"
          ccs(1)._2.value mustBe 1000F

          ccs(2)._1.credit mustBe 500F
          ccs(2)._1.number mustBe "4321432143214321"
          ccs(2)._2.value mustBe 300F
        case _ =>
      }
    }

    "select 3 credit cards for the payment with transactions of 650, 900 and 450" in {
      val cc1 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "1234123412341234", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 700F,
        transactions = List(DebitTransaction(50F, LocalDateTime.now)))
      val cc2 = CreditCard("ad4e0751-6fc4-4e28-b79a-74e59f23d8e6", "4321432143214321", "123",
        ValidDate.datePlusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 500F)
      val cc3 = CreditCard("5febb7b0-4082-4138-aced-7189e1cc464a", "7654765476547654", "123",
        ValidDate.dateMinusTwoDays.getDayOfMonth, LocalDateTime.now().plusYears(5L), 1000F,
        transactions = List(DebitTransaction(100F, LocalDateTime.now)))
      val wallet = Wallet("38e16da6-fee6-4053-9633-a499182ddc43", 2200F, List(cc1, cc2, cc3))

      wallet.purchase(2000F) match {
        case Left(ccs) =>
          ccs.head._1.credit mustBe 700F
          ccs.head._1.number mustBe "1234123412341234"
          ccs.head._2.value mustBe 650F

          ccs(1)._1.credit mustBe 1000F
          ccs(1)._1.number mustBe "7654765476547654"
          ccs(1)._2.value mustBe 900F

          ccs(2)._1.credit mustBe 500F
          ccs(2)._1.number mustBe "4321432143214321"
          ccs(2)._2.value mustBe 450F
        case _ =>
      }
    }
  }
}

object ValidDate {
  def datePlusTwoDays: LocalDateTime = {
    val result = LocalDateTime.now.plusDays(2L)
    if (result.getDayOfMonth > 28) {
      val nextMonth = result.plusMonths(1L)
      LocalDateTime.of(nextMonth.getYear, nextMonth.getMonth, 1, 0, 0, 0, 0)
    } else {
      result
    }
  }

  def dateMinusTwoDays: LocalDateTime = {
    val result = LocalDateTime.now.minusDays(2L)
    if (result.getDayOfMonth > 28) {
      LocalDateTime.of(result.getYear, result.getMonth, 28, 0, 0, 0, 0)
    } else {
      result
    }
  }
}