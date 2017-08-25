package io.atleastonce.wallet.manager.services

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{Transaction, TransactionDTO}
import io.atleastonce.wallet.manager.repositories.TransactionRepo

import scala.util.{Failure, Success, Try}

@Singleton
class TransactionService @Inject()(transactionRepo: TransactionRepo) {
  def getWallet(id: String, creditCardId: String): Either[Transaction, Throwable] = {
    transactionRepo.getTransactions(id, creditCardId) match {
      case Some(t) => Left(t)
      case None => Right(new Error("Não foi possível encontrar a transação pesquisada"))
    }
  }

  def getTransactionsByCreditCard(creditCardId: String): Either[List[Transaction], Throwable] = {
    transactionRepo.getTransactionsByCreditCard(creditCardId) match {
      case transactions: List[Transaction] if transactions.isEmpty =>
        Right(new Error("Não foi possível encontrar transações para este cartão de crédito"))
      case transactions: List[Transaction] => Left(transactions)
      case _ =>
        Right(new Error("Não foi possível encontrar carteiras para este cartão de crédito"))
    }
  }

  def save(creditCardId: String, operation: String, value: Float): Either[Transaction, Throwable] = {
    val newTransaction = TransactionDTO(UUID.randomUUID.toString, operation, value, LocalDateTime.now, creditCardId)

    Try(newTransaction.toTransaction) match {
      case Success(_) => transactionRepo.addTransaction(newTransaction)
      case Failure(err) => Right(new Error(err.getMessage, err))
    }
  }
}
