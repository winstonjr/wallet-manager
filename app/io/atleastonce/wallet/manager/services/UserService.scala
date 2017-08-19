package io.atleastonce.wallet.manager.services

import java.util.UUID
import javax.inject.Singleton

import io.atleastonce.wallet.manager.domain.User
import io.atleastonce.wallet.manager.repositories.Data

@Singleton
class UserService {

  def getAllUsers: List[User] = {
    Data.getAllUsers
  }

  def load(id: String): Either[User, Throwable] = {
    Data.getUser(id) match {
      case Some(u) => Left(u)
      case None => Right(new Error("Não foi possível encontrar o usuário pesquisado"))
    }
  }

  def save(name: String): User = {
    val newUser = User(UUID.randomUUID.toString, name, UUID.randomUUID.toString, User.generateSecretKey)
    Data.addUser(newUser)
  }

  def update(id: String, name: String): Either[User, Throwable] = {
    Data.getUser(id) match {
      case Some(u) =>
        val newUser = u.copy(name = name)
        Data.updateUser(u, newUser)
        Left(newUser)
      case None => Right(new Error("O usuário não foi encontrado. Não é possível atualizar o mesmo"))
    }
  }
}
