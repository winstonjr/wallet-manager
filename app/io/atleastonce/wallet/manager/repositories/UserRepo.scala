package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.{User, UserDTO}
import io.getquill.{PostgresJdbcContext, SnakeCase}
import play.api.Configuration

import scala.util.{Failure, Success, Try}

@Singleton
class UserRepo @Inject()(configuration: Configuration) {
  val logger = play.api.Logger(this.getClass)
  lazy val ctx = new PostgresJdbcContext[SnakeCase](configuration.getConfig("ctx").get.underlying)
  import ctx._
  implicit val queryMeta: SchemaMeta[UserDTO] = schemaMeta[UserDTO](
    "wallet.users"
  )

  def getAllUsers: List[User] = {
    run(quote {
      query[UserDTO]
    }).map(_.getUser)
  }

  def getUser(id: String): Option[User] = {
    run(quote {
      query[UserDTO].filter(user => user.id == lift(id))
    }).headOption.map(_.getUser)
  }

  def addUser(user: User): Either[User, Throwable] = {
   Try {
      run(quote {
        query[UserDTO].insert(lift(user.getUserDTO))
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
        query[UserDTO].filter(_.id == lift(user.id)).update(lift(user.getUserDTO))
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
