package io.atleastonce.wallet.manager.domain

import java.time.{LocalDateTime, ZoneOffset}

case class CreditCard(id: String,
                      number: String,
                      cvv: String,
                      dueDate: Int,
                      expirationDate: LocalDateTime,
                      credit: Float,
                      removed: Boolean = false,
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
  require(cvvReg.findFirstIn(cvv).isDefined, "cvv do cartão de crédito deve possuir 3 caracteres numéricos")
  require(dueDate > 0 && dueDate < 29, "Vencimento do cartão deve ser entre os dias 1 e 28 do mês")
  require(this.getExpiration.isAfter(CreditCard.getExpiration(date)), "Não é possível adicionar um cartão expirado")
  require(credit > 0F, "o crédito do cartão deve ser maior que 0")

  /**
    * Método responsável por calcular o crédito consolidado disponível no cartão
    * - - The user must be able to access all the information of his/her wallet at any time (limit set
    * by the user, maximum limit and available credit)
    *
    * @return valor disponível para uso em determinado momento
    */
  def getAvailableCredit: Float = {
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
  def pay(transaction: PaymentTransaction): CreditCard = {
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
      LocalDateTime.of(now.getYear, now.plusMonths(1L).getMonthValue, this.dueDate, 0, 0, 0)
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

  /**
    * Método responsável por adicionar uma transação de débito a um cartão
    *
    * @param transaction transação a ser adicionada
    * @return cartão de crédito com transações atualizadas ou erro de transação não autorizada
    */
  def purchase(transaction: DebitTransaction): Either[CreditCard, Throwable] = {
    if (this.isCreditAvailableForTransaction(transaction.value)) {
      val newTransactionList = transactions :+ transaction
      Left(this.copy(transactions = newTransactionList))
    } else {
      Right(new Error("Não existe crédito disponível para a compra"))
    }
  }

  private def isCreditAvailableForTransaction(value: Float): Boolean = {
    value <= this.getAvailableCredit
  }

  private def getExpiration: LocalDateTime = {
    CreditCard.getExpiration(this.expirationDate)
  }
}

object CreditCard {
  def getExpiration(date: LocalDateTime): LocalDateTime = {
    LocalDateTime.of(date.getYear, date.getMonthValue, 1, 0, 0, 0, 0)
  }
}

case class CreditCardDTO(id: String,
                         number: String,
                         cvv: String,
                         dueDate: Int,
                         expirationDate: LocalDateTime,
                         credit: Float,
                         removed: Boolean,
                         walletId: String) {
  def toCreditCard(transactions: List[Transaction]): CreditCard = {
    CreditCard(id,
      number,
      cvv,
      dueDate,
      expirationDate,
      credit,
      removed,
      transactions)
  }

  def toCreditCard: CreditCard = {
    CreditCard(id,
      number,
      cvv,
      dueDate,
      expirationDate,
      credit,
      removed)
  }}