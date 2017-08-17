package io.atleastonce.wallet.manager.services

import javax.inject.Singleton

import io.atleastonce.wallet.manager.domain.User
import io.atleastonce.wallet.manager.repositories.Data

@Singleton
class UserService {

  def loadUser(id: String): Option[User] = {
    Data.getUser(id)
  }
}
