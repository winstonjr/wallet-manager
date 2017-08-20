package io.atleastonce.wallet.manager.domain

import java.time.LocalDateTime

abstract class Transaction(operation: String,
                       value: Float,
                       date: LocalDateTime) {
  def getValueWithSignal: Float
}

case class PaymentTransaction(value: Float,
                              date: LocalDateTime) extends Transaction("payment", value, date) {
  override def getValueWithSignal: Float = value * (-1)
}

case class DebitTransaction(value: Float,
                            date: LocalDateTime) extends Transaction("debit", value, date) {
  override def getValueWithSignal: Float = value
}