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
  private val ccReg = "^[0-9]{16}$".r
  private val cvvReg = "^[0-9]{3}$".r
  private val date = LocalDateTime.now()
  require(null != id && id.nonEmpty, "id é obrigatório")
  require(uuidReg.findFirstIn(id).isDefined, "id deve ser um UUID")
  require(null != number && number.nonEmpty, "number é obrigatório")
  require(ccReg.findFirstIn(number).isDefined, "número do cartão de crédito deve possuir 16 caracteres numéricos")
  require(null != cvv && cvv.nonEmpty, "cvv é obrigatório")
  require(ccReg.findFirstIn(number).isDefined, "cvv do cartão de crédito deve possuir 3 caracteres numéricos")
  require(dueDate > 0 && dueDate < 29, "Vencimento do cartão deve ser entre os dias 1 e 28 do mês")
  require(expirationDate.isAfter(
    LocalDateTime.of(date.getYear, date.getMonthValue, date.getDayOfMonth + 1, 0, 0, 0)),
    "A data de expiração deve ser maior ou igual a amanhã")
  require(credit > 0F, "o crédito do cartão deve ser maior que 0")

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

  /**
    * Método responsável por retornar a informação da data de pagamento
    *
    * @return Data de pagamento do cartão
    */
  def getDueDate: LocalDateTime = {
    val now = LocalDateTime.now

    if (this.dueDate < now.getDayOfMonth) {
      LocalDateTime.of(now.getYear, now.getMonthValue + 1, this.dueDate, 0, 0, 0)
    } else {
      LocalDateTime.of(now.getYear, now.getMonthValue, this.dueDate, 0, 0, 0)
    }
  }

  /**
    * Método responsável por retornar a informação de data de pagamento em milisegundos
    * para facilitar comparações em outras classes
    *
    * @return
    */
  def getDueDateMillis: Long = {
    this.getDueDate.toInstant(ZoneOffset.UTC).toEpochMilli
  }

  /**
    * Método responsável por retornar se o cartão está válido para uso ou não
    *
    * @return True caso o cartão esteja válido False caso contrário
    */
  def isValid: Boolean = {
    this.expirationDate.isAfter(LocalDateTime.now)
  }

  private def isCreditAvailableForTransaction(value: Float): Boolean = {
    // TODO: Buscar informações atualizadas no banco
    value <= this.getAvailableCredit
  }
}