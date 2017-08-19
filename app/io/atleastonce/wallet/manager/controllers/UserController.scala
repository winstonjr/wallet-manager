
package io.atleastonce.wallet.manager.controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.User
import io.atleastonce.wallet.manager.services.UserService
import org.json4s._
import org.json4s.native.Serialization.write
import play.api.libs.json.JsValue
import play.api.mvc._
import rapture.json._
import rapture.json.jsonBackends.jawn._

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService) extends AbstractController(cc) {
  implicit val formats = DefaultFormats

  def getAll: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(write(userService.getAllUsers)).as(JSON)
  }

  def createUser: Action[JsValue] = Action(parse.json) { implicit request =>
    val data = Json.parse(request.body.toString)
    val result = userService.save(data.name.toBareString)
    Created(write(result)).as(JSON)
  }

  def updateUser(id: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val data = Json.parse(request.body.toString)
    userService.update(id, data.name.toBareString) match {
      case Left(result) => Created(write(result)).as(JSON)
      case Right(e) => NotFound(s"""{"message":"${e.getMessage}"}""")
    }
  }

  def findById(id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val user = userService.load(id)

    user match {
      case Left(u) => Ok(write(u)).as(JSON)
      case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""")
    }
  }
}