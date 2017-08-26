package io.atleastonce.wallet.manager.services

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{CreditCard, CreditCardDTO, DebitTransaction, Transaction}
import io.atleastonce.wallet.manager.repositories.CreditCardRepo

import scala.util.{Failure, Success, Try}

@Singleton
class CreditCardService @Inject()(creditCardRepo: CreditCardRepo,
                                  transactionService: TransactionService) {

  def getCreditCard(id: String, walletId: String): Either[CreditCard, Throwable] = {
    creditCardRepo.getCreditCard(id, walletId) match {
      case Some(w) => Left(w)
      case None => Right(new Error("Não foi possível encontrar a carteira pesquisada"))
    }
  }

  def getCreditCardsByWallet(walletId: String): Either[List[CreditCard], Throwable] = {
    creditCardRepo.getCreditCardsByWallet(walletId) match {
      case creditCards: List[CreditCard] if creditCards.isEmpty =>
        Right(new Error("Não foi possível encontrar cartões de crédito para esta carteira"))
      case creditCards: List[CreditCard] => Left(creditCards)
      case _ =>
        Right(new Error("Não foi possível encontrar cartões de crédito para esta carteira"))
    }
  }

  def loadFull(walletId: String): List[CreditCard] = {
    creditCardRepo.getCreditCardsByWallet(walletId)
      .map(cc => cc.copy(transactions = transactionService.loadFull(cc.id)))
  }

  def save(walletId: String, number: String, cvv: String, dueDate: Int,
           expirationDate:LocalDateTime, credit: Float): Either[CreditCard, Throwable] = {
    val newCreditCard = CreditCardDTO(UUID.randomUUID.toString, number, cvv, dueDate,
      expirationDate, credit, removed = false, walletId)

    Try(newCreditCard.toCreditCard) match {
      case Success(_) => creditCardRepo.addCreditCard(newCreditCard)
      case Failure(err) => Right(new Error(err.getMessage, err))
    }
  }

  def update(id: String, walletId: String, number: String, cvv: String, dueDate: Int,
             expirationDate:LocalDateTime, credit: Float, removed: Boolean): Either[CreditCard, Throwable] = {
    this.getCreditCard(id, walletId) match {
      case Left(cc) =>
        Try(cc.copy(number = number, cvv = cvv, dueDate = dueDate, expirationDate = expirationDate,
          credit = credit, removed = removed)) match {
          case Success(_) => creditCardRepo.updateCreditCard(CreditCardDTO(id, number,
            cvv, dueDate, expirationDate, credit, removed, walletId))
          case Failure(err) => Right(new Error(err.getMessage, err))
        }
      case Right(r) => Right(r)
    }
  }

  def transact(id: String, walletId: String, transaction: Transaction): Either[CreditCard, Throwable] = {
    this.getCreditCard(id, walletId) match {
      case Left(cc) =>
        transactionService.save(cc.id, transaction.operation, transaction.value) match {
          case Left(t) => Left(cc.purchase(DebitTransaction(t.value, t.date)))
          case Right(err) => Right(err)
        }
      case Right(err) => Right(err)
    }
  }
}
