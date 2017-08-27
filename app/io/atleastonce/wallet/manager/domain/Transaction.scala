package io.atleastonce.wallet.manager.domain

import java.time.LocalDateTime

abstract class Transaction(op: String,
                       v: Float,
                       d: LocalDateTime) {
  val operation: String = op
  val value: Float = v
  val date: LocalDateTime = d

  def getValueWithSignal: Float
}

case class PaymentTransaction(v: Float,
                              d: LocalDateTime = LocalDateTime.now) extends Transaction("payment", v, d) {
  override def getValueWithSignal: Float = value * (-1)
}

case class DebitTransaction(v: Float,
                            d: LocalDateTime = LocalDateTime.now) extends Transaction("debit", v, d) {
  override def getValueWithSignal: Float = value
}

case class TransactionDTO(id: String,
                          operation: String,
                          value: Float,
                          date: LocalDateTime,
                          creditCardId: String) {
  def toTransaction: Transaction = {
    operation match {
      case "payment" => PaymentTransaction(value, date)
      case "debit" => DebitTransaction(value, date)
    }
  }
}