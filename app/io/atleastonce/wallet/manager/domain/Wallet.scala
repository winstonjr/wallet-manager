package io.atleastonce.wallet.manager.domain

case class Wallet(id: String,
                  user: User,
                  credit: Float,
                  cards: List[CreditCard] = List.empty) {
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
    val totalExpenses = cards.map(_.getAvailableCredit).sum
    this.credit - totalExpenses
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
  def selectBestCreditCard(): Boolean = {
    // TODO: Tudo =D

    true
  }

  private def removalAllowed(card: CreditCard): Either[Wallet, Throwable] = {
    val newWallet = removeCardFromList(card)
    if (this.credit < newWallet.getMaximumLimit) {
      Right(new Error("Não é possível remover o cartão no momento." +
        " Primeiro reduza o limite de crédito da carteira"))
    } else {
      // TODO: Remover o cartão do banco de dados.
      Left(newWallet)
    }
  }

  private def removeCardFromList(card: CreditCard): Wallet = {
    val newCards = cards diff List(card)
    this.copy(cards = newCards)
  }
}