package io.atleastonce.wallet.manager.domain

import java.time.{LocalDateTime, ZoneOffset}

case class Wallet(id: String,
                  credit: Float,
                  cards: List[CreditCard] = List.empty) {

  private val uuidReg = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$".r
  require(null != id && id.nonEmpty, "id é obrigatório")
  require(uuidReg.findFirstIn(id).isDefined, "id deve ser um UUID")
  require(credit >= 0F, "O valor mínimo da carteira é 0")
  assume(credit <= this.getMaximumLimit,
    "O limite máximo de uma carteira não pode ultrapassar a soma dos limites dos cartões")

  private implicit val ord = new Ordering[CreditCard] {
    def compare(self: CreditCard, that: CreditCard): Int = {
      that.getDueDateMillis compare self.getDueDateMillis match {
        case 0 => self.credit compare that.credit
        case c => c
      }
    }
  }

  /**
    * Método responsável por calcular o limite máximo de uma carteira
    * - The maximum limit of a wallet must be the sum of all its cards
    *
    * @return O limite máximo de um cartão de crédito
    */
  def getMaximumLimit: Float = {
    cards.map(_.credit).sum
  }

  /**
    * Método responsável por atualizar o limite de crédito da carteira quando possível.
    * - The user can set the real limit of a wallet, but can’t surpass the maximum limit
    *
    * @param value novo valor da carteira
    * @return Uma nova carteira atualizada em caso de sucesso ou uma mensagem explicativa em caso de erro.
    */
  def updateLimit(value: Float): Either[Wallet, Throwable] = {
    val maximumLimit = this.getMaximumLimit
    if (value > maximumLimit) {
      Right(new Error(s"O novo valor ultrapassa o valor máximo de crédito disponível. $value > $maximumLimit"))
    } else {
      // TODO: Atualizar o banco de dados quando isso ocorrer
      Left(this.copy(credit = value))
    }
  }

  /**
    * Método responsável por remover um cartão de crédito de uma carteira.
    * - Having the capacity of adding or removing a card at any time
    *
    * @param cardNumber Número do cartão a ser removido da carteira
    * @return Uma nova carteira atualizada em caso de sucesso ou uma mensagem explicativa caso contrário
    */
  def removeCard(cardNumber: String): Either[Wallet, Throwable] = {
    val card = cards.find(_.number == cardNumber)
    card match {
      case Some(c) => this.removalAllowed(c)
      case None => Right(new Error("Cartão de Crédito não encontrado"))
    }
  }

  /**
    * Método responsável por atualizar a carteira adicionando um novo cartão
    * - Having the capacity of adding or removing a card at any time
    *
    * @param card Novo cartão a ser adicionado
    * @return Uma nova carteira atualizada em caso de sucesso ou uma mensagem explicativa caso contrário
    */
  def addCard(card: CreditCard): Either[Wallet, Throwable] = {
    // TODO: Salvar essa informação no banco de dados
    val newCardList = this.cards :+ card
    Left(this.copy(cards = newCardList))
  }

  /**
    * Método responsável por retornar o limite disponível na carteira
    * - The user must be able to access all the information of his/her wallet at any time (limit set
    * by the user, maximum limit and available credit)
    *
    * @return valor de créditos diminuindo as transações já realizada
    */
  def getAvailableCredit: Float = {
    this.credit - cards.filter(_.isValid).map(_.getTransactions).sum
  }

  /**
    * Método responsável por gerar a lista de transações e cartões a serem descontados
    *
    * @param value Valor total da transação
    * @return Lista de transaçõe e cartões a serem atualizados.
    */
  def purchase(value: Float): Either[List[(CreditCard, DebitTransaction)], Throwable] = {
    selectBestCreditCard(value) match {
      case Right(err) => Right(err)
      case Left(ccards) => Left(this.generateTransactions(ccards, value))
    }
  }

  private def generateTransactions(ccards: List[CreditCard], value: Float): List[(CreditCard, DebitTransaction)] = {
    var moneyNeeded = value

    ccards.map(card =>
      if (moneyNeeded > card.getAvailableCredit) {
        moneyNeeded = moneyNeeded - card.getAvailableCredit
        (card, DebitTransaction(card.getAvailableCredit, LocalDateTime.now))
      } else {
        (card, DebitTransaction(moneyNeeded, LocalDateTime.now))
      }
    )
  }

  /**
    * Método responsável por escolher o/os melhores cartões para uma compra
    *
    * - Choose the credit card that has the farthest due date
    * - Use the credit card with lowest limit
    * - Purchase on a single card
    * - When it is not possible to purchase on a single card, purchase into more cards
    * - If the cards expire on the same day, you pay first with the one of lowest
    *   limit and "complete" with the next one which has more limit.
    *
    * @return Melhor cartão de crédito para a compra
    */
  def selectBestCreditCard(value: Float): Either[List[CreditCard], Throwable] = {
    // Não possui crédito para a compra
    if (value > this.getAvailableCredit) {
      Right(new Error("O valor da compra é maior que o crédito disponível na carteira"))
    } else {
      tryGetCreditCardForOneShotPayment(value) match {
        case Left(card) => Left(card)
        case Right(_) => Left(this.getMoreThanOneCardForPayment(value))
      }
    }
  }

  private def tryGetCreditCardForOneShotPayment(value: Float): Either[List[CreditCard], Throwable] = {
    val f1 = this.cards.filter(cc => cc.isValid && cc.getAvailableCredit >= value).sorted

    if (f1.nonEmpty) {
      Left(List(f1.head))
    } else {
      Right(new Error("Não foi possível utilizar apenas um cartão para pagamento"))
    }
  }

  private def getMoreThanOneCardForPayment(value: Float): List[CreditCard] = {
    var takeUntilBiggerThanZero = value

    this.cards.filter(_.isValid).sorted.takeWhile(cc => {
        val needCredit = takeUntilBiggerThanZero > 0
        takeUntilBiggerThanZero = takeUntilBiggerThanZero - cc.getAvailableCredit
        needCredit
      })
  }

  private def removalAllowed(card: CreditCard): Either[Wallet, Throwable] = {
    if (this.credit > (this.getMaximumLimit - card.credit)) {
      Right(new Error("Não é possível remover o cartão no momento." +
        " Primeiro reduza o limite de crédito da carteira"))
    } else {
      // TODO: Remover o cartão do banco de dados.
      val newWallet = removeCardFromList(card)
      Left(newWallet)
    }
  }

  private def removeCardFromList(card: CreditCard): Wallet = {
    val newCards = cards diff List(card)
    this.copy(cards = newCards)
  }
}

case class WalletDTO(id: String,
                     credit: Float,
                     userId: String) {
  def toWallet: Wallet = {
    Wallet(id,
      credit)
  }

  def toWallet(cards: List[CreditCard]): Wallet = {
    Wallet(id,
      credit,
      cards)
  }
}