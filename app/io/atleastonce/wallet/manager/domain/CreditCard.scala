package io.atleastonce.wallet.manager.domain

import java.time.{LocalDateTime, ZoneOffset}

case class CreditCard(id: String,
                      number: String,
                      cvv: String,
                      dueDate: Int,
                      expirationDate: LocalDateTime,
                      credit: Float,
                      removed: Option[Boolean] = Some(false),
                      transactions: List[Transaction] = List.empty) {
  private val uuidReg = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$".r
  require(null != id && id.nonEmpty, "id é obrigatório")
  require(uuidReg.findFirstIn(id).isDefined, "id deve ser um UUID")
  require(null != number && number.nonEmpty, "number é obrigatório")
  require(16.equals(number.length))


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
    * Método responsável por receber o pagamento de um cartão.
    * - If capable of releasing credit (pay a certain amount on the cards account)
    *
    * @param transaction transação que está sendo realizada
    * @return Nova carteira com o pagamento adicionado
    */
  def AddCredits(transaction: PaymentTransaction): CreditCard = {
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

  def isValid: Boolean = {
    this.expirationDate.isAfter(LocalDateTime.now)
  }

  private def isCreditAvailableForTransaction(value: Float): Boolean = {
    // TODO: Buscar informações atualizadas no banco
    value <= this.getAvailableCredit
  }
}