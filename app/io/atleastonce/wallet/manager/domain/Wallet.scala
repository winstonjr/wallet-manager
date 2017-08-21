package io.atleastonce.wallet.manager.domain

import java.time.ZoneOffset

case class Wallet(id: String,
                  credit: Float,
                  cards: List[CreditCard] = List.empty) {

  private val uuidReg = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$".r
  require(null != id && id.nonEmpty, "id é obrigatório")
  require(uuidReg.findFirstIn(id).isDefined, "id deve ser um UUID")
  require(credit >= 0F, "O valor mínimo da carteira é 0")
  assume(credit <= this.getMaximumLimit,
    "O limite máximo de um cartão de uma carteira não pode ultrapassar a soma dos limites dos cartões")

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
    */
  def getAvailableCredit: Float = {
    cards.map(_.getAvailableCredit).sum
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
      // cartões que possuem crédito para a compra single shot
      var f1 = this.cards.filter(_.getAvailableCredit >= value)
        .sortWith((cc1, cc2) => getFarthestDueDateCard(cc1.getDueDateMillis, cc2.getDueDateMillis)
          && lowerCredit(cc1.credit, cc2.credit))

      if (f1.nonEmpty) {
        Left(List(f1.head))
      } else {
        // será necessário mais de um cartão para realizar a compra
        Right(new Error("deu ruim"))
      }
    }
  }

  private def lowerCredit(credit1: Float, credit2: Float): Boolean = {
    credit1 < credit2
  }

  private def getFarthestDueDateCard(dueDate1: Long, dueDate2: Long): Boolean = {
    dueDate1 > dueDate2
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