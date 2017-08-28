package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{CreditCard, CreditCardDTO}

import scala.util.{Failure, Success, Try}

@Singleton
class CreditCardRepo @Inject()() {
  val logger = play.api.Logger(this.getClass)
  lazy val ctx = DataUtil.ctx
  import ctx._
  implicit val queryMeta: SchemaMeta[CreditCardDTO] = schemaMeta[CreditCardDTO](
    "wallet.credit_cards"
  )

  def getCreditCard(id: String, walletId: String): Option[CreditCardDTO] = {
    run(quote {
      query[CreditCardDTO].filter(cc => cc.id == lift(id) && cc.walletId == lift(walletId))
    }).headOption
  }

  def getCreditCardsByWallet(walletId: String): List[CreditCardDTO] = {
    run(quote {
      query[CreditCardDTO].filter(cc => cc.walletId == lift(walletId))
    })
  }

  def addCreditCard(creditCard: CreditCardDTO): Either[CreditCard, Throwable] = {
    Try {
      run(quote {
        query[CreditCardDTO].insert(lift(creditCard))
      })
    } match {
      case Success(_) =>
        Left(creditCard.toCreditCard)
      case Failure(error) =>
        this.logger.error(s"Erro ao inserir o cartão de crédito #${creditCard.id}", error)
        Right(error)
    }
  }

  def updateCreditCard(creditCard: CreditCardDTO): Either[CreditCardDTO, Throwable] = {
    Try {
      run(quote {
        query[CreditCardDTO].filter(_.id == lift(creditCard.id)).update(lift(creditCard))
      })
    } match {
      case Success(result) if result == 0 =>
        Right(new Error(s"Cartão de Crédito com id #${creditCard.id} não existe"))
      case Success(_) =>
        Left(creditCard)
      case Failure(error) =>
        this.logger.error(s"Erro ao atualizar Cartão de Crédito #${creditCard.id}", error)
        Right(new Error(s"Erro ao atualizar Cartão de Crédito #${creditCard.id}", error))
    }
  }
}
