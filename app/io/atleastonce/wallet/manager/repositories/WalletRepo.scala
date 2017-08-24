package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{Wallet, WalletDTO}
import play.api.Configuration

import scala.util.{Failure, Success, Try}

@Singleton
class WalletRepo @Inject()(configuration: Configuration) {
  val logger = play.api.Logger(this.getClass)
  lazy val ctx = DataUtil.ctx
  import ctx._
  implicit val queryMeta: SchemaMeta[WalletDTO] = schemaMeta[WalletDTO](
    "wallet.wallets"
  )

  def getWallet(id: String, userId: String): Option[Wallet] = {
    run(quote {
      query[WalletDTO].filter(w => w.id == lift(id) && w.userId == lift(userId))
    }).headOption.map(_.toWallet)
  }

  def getWalletsByUser(userId: String): List[Wallet] = {
    run(quote {
      query[WalletDTO].filter(w => w.userId == lift(userId))
    }).map(_.toWallet)
  }

  def addWallet(wallet: WalletDTO): Either[Wallet, Throwable] = {
    Try {
      run(quote {
        query[WalletDTO].insert(lift(wallet))
      })
    } match {
      case Success(_) =>
        Left(wallet.toWallet)
      case Failure(error) =>
        this.logger.error(s"Erro ao inserir a carteira #${wallet.id}", error)
        Right(error)
    }
  }

  def updateUser(wallet: WalletDTO): Either[Wallet, Throwable] = {
    Try {
      run(quote {
        query[WalletDTO].filter(_.id == lift(wallet.id)).update(lift(wallet))
      })
    } match {
      case Success(result) if result == 0 =>
        Right(new Error(s"Carteira com id #${wallet.id} não existe"))
      case Success(_) =>
        Left(wallet.toWallet)
      case Failure(error) =>
        this.logger.error(s"Erro ao atualizar usuário #${wallet.id}", error)
        Right(new Error(s"Erro ao atualizar usuário #${wallet.id}", error))
    }
  }
}
