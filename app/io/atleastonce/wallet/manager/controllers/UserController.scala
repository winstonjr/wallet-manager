package io.atleastonce.wallet.manager.controllers

import javax.inject.{Inject, Singleton}
import io.atleastonce.wallet.manager.services.UserService
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService) extends AbstractController(cc) {
//  implicit val formats = DefaultFormats

  def getAll() = Action { implicit request: Request[AnyContent] =>
    NotImplemented
  }

  def createUser() = Action(parse.json) { implicit request =>
//    val data: User = read[User](request.body.toString)
//    val updatedData: User = service.updateUser(data)
//
//    Ok(write(updatedData)).as(JSON)
    NotImplemented
  }

  def updateUser() = Action(parse.json) { implicit request =>
    NotImplemented
  }

  def findById(id: String) = Action { implicit request: Request[AnyContent] =>
    NotImplemented
  }
}