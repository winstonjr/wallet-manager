package io.atleastonce.wallet.manager.services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.User
import io.atleastonce.wallet.manager.repositories.{UserRepo, WalletRepo}

@Singleton
class UserService @Inject()(userRepo: UserRepo,
                            walletService: WalletService) {

  def getAllUsers: List[User] = {
    userRepo.getAllUsers
  }

  def load(id: String): Either[User, Throwable] = {
    userRepo.getUser(id) match {
      case Some(u) => Left(u)
      case None => Right(new Error("Não foi possível encontrar o usuário pesquisado"))
    }
  }

  def loadFull(id: String): Either[User, Throwable] = {
    userRepo.getUser(id) match {
      case Some(u) => Left(u.copy(wallets = walletService.loadFull(id)))
      case None => Right(new Error("Não foi possível encontrar o usuário pesquisado"))
    }
  }

  def save(name: String): Either[User, Throwable] = {
    val newUser = User(UUID.randomUUID.toString, name, UUID.randomUUID.toString, User.generateSecretKey)
    userRepo.addUser(newUser)
  }

  def update(id: String, name: String): Either[User, Throwable] = {
    userRepo.getUser(id) match {
      case Some(u) =>
        val newUser = u.copy(name = name)
        userRepo.updateUser(newUser)
      case None => Right(new Error("O usuário não foi encontrado. Não é possível atualizar o mesmo"))
    }
  }
}
