package io.atleastonce.wallet.manager.services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{Wallet, WalletDTO}
import io.atleastonce.wallet.manager.repositories.WalletRepo

import scala.util.{Failure, Success, Try}

@Singleton
class WalletService @Inject()(walletRepo: WalletRepo,
                              creditCardService: CreditCardService) {
  def getWallet(id: String, userId: String): Either[Wallet, Throwable] = {
    walletRepo.getWallet(id, userId) match {
      case Some(w) => Left(w.toWallet(cards = creditCardService.loadFull(w.id)))
      case None => Right(new Error("Não foi possível encontrar a carteira pesquisada"))
    }
  }

  def getFullWallet(id: String, userId: String): Either[Wallet, Throwable] = {
    walletRepo.getWallet(id, userId) match {
      case Some(w) => Left(w.toWallet(cards = creditCardService.loadFull(w.id)))
      case None => Right(new Error("Não foi possível encontrar a carteira pesquisada"))
    }
  }

  def getWalletsByUser(userId: String): Either[List[Wallet], Throwable] = {
    walletRepo.getWalletsByUser(userId) match {
      case wallets: List[WalletDTO] if wallets.isEmpty =>
        Right(new Error("Não foi possível encontrar carteiras para este usuário"))
      case wallets: List[WalletDTO] => Left(wallets.map(wdto =>
        wdto.toWallet(creditCardService.loadFull(wdto.id))))
      case _ =>
        Right(new Error("Não foi possível encontrar carteiras para este usuário"))
    }
  }

  def loadFull(userId: String): List[Wallet] = {
    walletRepo.getWalletsByUser(userId).map(w => w.toWallet(cards = creditCardService.loadFull(w.id)))
  }

  def loadFullById(id: String, userId: String): Either[Wallet, Throwable] = {
    walletRepo.getWallet(id, userId).map(w => w.toWallet(cards = creditCardService.loadFull(w.id))) match {
      case Some(ws) => Left(ws)
      case None => Right(new Error(s"Não foi possível encontrar a carteira procurada. Id: $id ::: userId: $userId"))
    }
  }

  def save(userId: String, credit: Float): Either[Wallet, Throwable] = {
    val newWallet = WalletDTO(UUID.randomUUID.toString, credit, userId)

    Try(newWallet.toWallet) match {
      case Success(_) => walletRepo.addWallet(newWallet)
      case Failure(err) => Right(new Error(err.getMessage, err))
    }
  }

  def update(id: String, userId: String, credit: Float): Either[Wallet, Throwable] = {
    this.getFullWallet(id, userId) match {
      case Left(w) =>
        Try(w.copy(credit = credit)) match {
          case Success(wallet) => walletRepo.updateWallet(WalletDTO(wallet.id, credit, userId)) match {
            case Left(wdto) => this.loadFullById(id, userId)
            case Right(err) => Right(err)
          }
          case Failure(err) => Right(new Error(err.getMessage, err))
        }
      case Right(r) => Right(r)
    }
  }

  def purchase(id: String, userId: String, value: Float): Either[Wallet, Throwable] = {
    this.loadFullById(id, userId) match {
      case Left(w) =>
        w.purchase(value) match {
          case Left(ts) =>
            ts.foreach(t => creditCardService.purchase(t._1.id, w.id, t._2))
            this.loadFullById(id, userId)
          case Right(err) => Right(new Error(err.getMessage, err))
        }
      case Right(r) => Right(r)
    }
  }

  def remove(id: String, walletId: String, userId: String): Either[Boolean, Throwable] = {
    this.getWallet(walletId, userId) match {
      case Left(w) => w.cards.find(_.id == id) match {
        case Some(cc) => w.removeCard(cc.number) match {
          case Left(_) => creditCardService.update(cc.id, walletId, cc.number,
            cc.cvv, cc.dueDate, cc.expirationDate, cc.credit, removed = true) match {
            case Left(_) => Left(true)
            case Right(err) => Right(err)
          }
          case Right(err) => Right(err)
        }
        case None => Right(new Error("Não foi possível encontrar o cartão a ser removido"))
      }
      case Right(err) => Right(err)
    }
  }
}
