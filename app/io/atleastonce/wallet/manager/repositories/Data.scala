package io.atleastonce.wallet.manager.repositories

import io.atleastonce.wallet.manager.domain.User

object Data {
  // Essa classe tem problemas de concorrência não tratados
  private var users: List[User] = List.empty

  def getUser(id: String): Option[User] = {
    users.find(_.id == id)
  }

  def addUser(user: User): User = {
    users = users :+ user
    user
  }

  def getAllUsers: List[User] = {
    users
  }

  def removeUser(user: User) = {
    this.users = users diff List(user)
  }

  def updateUser(oldUser: User, user: User) = {
    this.removeUser(oldUser)
    this.addUser(user)
  }
}
