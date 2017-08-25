package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{Transaction, TransactionDTO}

import scala.util.{Failure, Success, Try}

@Singleton
class TransactionRepo @Inject()() {
  val logger = play.api.Logger(this.getClass)
  lazy val ctx = DataUtil.ctx
  import ctx._
  implicit val queryMeta: SchemaMeta[TransactionDTO] = schemaMeta[TransactionDTO](
    "wallet.transactions"
  )

  def getTransactions(id: String, walletId: String): Option[Transaction] = {
    run(quote {
      query[TransactionDTO].filter(t => t.id == lift(id) && t.creditCardId == lift(walletId))
    }).headOption.map(_.toTransaction)
  }

  def getTransactionsByCreditCard(walletId: String): List[Transaction] = {
    run(quote {
      query[TransactionDTO].filter(t => t.creditCardId == lift(walletId))
    }).map(_.toTransaction)
  }

  def addTransaction(transaction: TransactionDTO): Either[Transaction, Throwable] = {
    Try {
      run(quote {
        query[TransactionDTO].insert(lift(transaction))
      })
    } match {
      case Success(_) =>
        Left(transaction.toTransaction)
      case Failure(error) =>
        this.logger.error(s"Erro ao inserir a transação #${transaction.id}", error)
        Right(error)
    }
  }

  def updateTransaction(transaction: TransactionDTO): Either[Transaction, Throwable] = {
    Try {
      run(quote {
        query[TransactionDTO].filter(_.id == lift(transaction.id)).update(lift(transaction))
      })
    } match {
      case Success(result) if result == 0 =>
        Right(new Error(s"Transação com id #${transaction.id} não existe"))
      case Success(_) =>
        Left(transaction.toTransaction)
      case Failure(error) =>
        this.logger.error(s"Erro ao atualizar transação #${transaction.id}", error)
        Right(new Error(s"Erro ao atualizar transação #${transaction.id}", error))
    }
  }
}
