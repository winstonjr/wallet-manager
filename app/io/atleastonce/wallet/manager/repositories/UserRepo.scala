package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{User, UserDTO}

import scala.util.{Failure, Success, Try}

@Singleton
class UserRepo @Inject()() {
  val logger = play.api.Logger(this.getClass)
  lazy val ctx = DataUtil.ctx
  import ctx._
  implicit val queryMeta: SchemaMeta[UserDTO] = schemaMeta[UserDTO](
    "wallet.users"
  )

  def getAllUsers: List[User] = {
    run(quote {
      query[UserDTO]
    }).map(_.toUser)
  }

  def getUser(id: String): Option[User] = {
    run(quote {
      query[UserDTO].filter(u => u.id == lift(id))
    }).headOption.map(_.toUser)
  }

  def addUser(user: User): Either[User, Throwable] = {
   Try {
      run(quote {
        query[UserDTO].insert(lift(user.toUserDTO))
      })
    } match {
      case Success(_) =>
        Left(user)
      case Failure(error) =>
        this.logger.error(s"Erro ao inserir usuário #${user.name}", error)
        Right(error)
    }
  }

  def updateUser(user: User): Either[User, Throwable] = {
    Try {
      run(quote {
        query[UserDTO].filter(_.id == lift(user.id)).update(lift(user.toUserDTO))
      })
    } match {
      case Success(result) if result == 0 =>
        Right(new Error(s"Usuário com id #${user.id} não existe"))
      case Success(_) =>
        Left(user)
      case Failure(error) =>
        this.logger.error(s"Erro ao atualizar usuário #${user.id}", error)
        Right(new Error(s"Erro ao atualizar usuário #${user.id}", error))
    }
  }
}