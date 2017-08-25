package io.atleastonce.wallet.manager.services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{Wallet, WalletDTO}
import io.atleastonce.wallet.manager.repositories.WalletRepo

import scala.util.{Failure, Success, Try}

@Singleton
class WalletService @Inject()(walletRepo: WalletRepo) {
  def getWallet(id: String, userId: String): Either[Wallet, Throwable] = {
    walletRepo.getWallet(id, userId) match {
      case Some(w) => Left(w)
      case None => Right(new Error("Não foi possível encontrar a carteira pesquisada"))
    }
  }

  def getWalletsByUser(userId: String): Either[List[Wallet], Throwable] = {
    walletRepo.getWalletsByUser(userId) match {
      case wallets: List[Wallet] if wallets.isEmpty =>
        Right(new Error("Não foi possível encontrar carteiras para este usuário"))
      case wallets: List[Wallet] => Left(wallets)
      case _ =>
        Right(new Error("Não foi possível encontrar carteiras para este usuário"))
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
    this.getWallet(id, userId) match {
      case Left(w) =>
        Try(w.copy(credit = credit)) match {
          case Success(wallet) => walletRepo.updateWallet(WalletDTO(wallet.id, credit, userId))
          case Failure(err) => Right(new Error(err.getMessage, err))
        }
      case Right(r) => Right(r)
    }
  }

//  def purchase(id: String, userId: String, value: Float): Either[Wallet, Throwable] = {
//    this.getWallet(id, userId) match {
//      case Left(w) =>
//        w.purchase(value) match {
//          case Left(transactions) =>
//            // TODO: CreditCard transactions
//            //transactions.foreach(t => t)
//          case Right(err) => Right(new Error(err.getMessage, err))
//        }
//      case Right(r) => Right(r)
//    }
//  }
}
