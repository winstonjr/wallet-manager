package io.atleastonce.wallet.manager.domain

import java.time.{LocalDateTime, ZoneOffset}

case class CreditCard(id: String,
                      number: String,
                      cvv: Int,
                      dueDate: Int,
                      expirationDate: LocalDateTime,
                      credit: Float,
                      removed: Option[Boolean] = Some(false),
                      transactions: List[Transaction] = List.empty) {
  /**
    * Método responsável por calcular o crédito consolidado disponível no cartão
    * - - The user must be able to access all the information of his/her wallet at any time (limit set
    * by the user, maximum limit and available credit)
    *
    * @return valor disponível para uso em determinado momento
    */
  def getAvailableCredit: Float = {
    // TODO: Buscar informações do banco de dados
    val accumulatedValue = transactions.map(_.getValueWithSignal).sum

    credit - accumulatedValue
  }

  /**
    * Método responsável por receber uma compra de um cartão.
    * - Have the necessary properties to execute a purchase (number, due date,
    * expiration date, cvv and limit).
    *
    * @param transaction transação que está sendo realizada
    * @return O cartão de crédito com a nova transação ou uma mensagem informando que não é possível
    *         realizar a transação.
    */
  def Purchase(transaction: PaymentTransaction): Either[CreditCard, Throwable] = {
    if (this.expirationDate.isAfter(transaction.date)) {
      if (isCreditAvailableForTransaction(transaction.value)) {
        val newTransactionList = transactions :+ transaction
        // TODO: Atualizar o banco de dados com a informação
        Left(this.copy(transactions = newTransactionList))
      } else {
        Right(new Error("Não existe crédito suficiente para realizar a transação"))
      }
    } else {
      Right(new Error("O cartão já está expirado. Favor remover da carteira."))
    }
  }

  /**
    * Método responsável por receber o pagamento de um cartão.
    * - If capable of releasing credit (pay a certain amount on the cards account)
    *
    * @param transaction transação que está sendo realizada
    * @return Nova carteira com o pagamento adicionado
    */
  def AddCredits(transaction: DebitTransaction): CreditCard = {
    val newTransactionList = transactions :+ transaction
    this.copy(transactions = newTransactionList)
  }

  def getDueDate: LocalDateTime = {
    val now = LocalDateTime.now

    if (this.dueDate < now.getDayOfMonth) {
      LocalDateTime.of(now.getYear, now.getMonthValue + 1, this.dueDate, 0, 0, 0)
    } else {
      LocalDateTime.of(now.getYear, now.getMonthValue, this.dueDate, 0, 0, 0)
    }
  }

  def getDueDateMillis: Long = {
    this.getDueDate.toInstant(ZoneOffset.UTC).toEpochMilli
  }

  private def isCreditAvailableForTransaction(value: Float): Boolean = {
    // TODO: Buscar informações atualizadas no banco
    value <= this.getAvailableCredit
  }
}