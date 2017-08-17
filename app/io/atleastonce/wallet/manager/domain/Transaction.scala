package io.atleastonce.wallet.manager.domain

import java.time.LocalDateTime

class Transaction(operation: String,
                       value: Float,
                       date: LocalDateTime) {
  def getValueWithSignal: Float = {
    operation match {
      case "payment" => value * (-1)
      case _ => value
    }
  }
}

case class PaymentTransaction(value: Float,
                              date: LocalDateTime) extends Transaction("payment", value, date)

case class DebitTransaction(value: Float,
                            date: LocalDateTime) extends Transaction("debit", value, date)