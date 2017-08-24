package io.atleastonce.wallet.manager.controllers

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.services.WalletService
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write
import play.api.libs.json.JsValue
import play.api.mvc._
import rapture.json._
import rapture.json.jsonBackends.jawn._

@Singleton
class WalletController  @Inject()(cc: ControllerComponents,
                                  walletService: WalletService) extends AbstractController(cc) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def createWallet(userId: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val data = Json.parse(request.body.toString)
    walletService.save(userId, data.credit.toBareString.toFloat) match {
      case Left(result) => Created(write(result)).as(JSON)
      case Right(err) => InternalServerError(s"""{"message":"${err.getMessage}"}""").as(JSON)
    }
  }

  def updateWallet(userId: String, id: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val data = Json.parse(request.body.toString)
    walletService.update(id, userId, data.credit.toBareString.toFloat) match {
      case Left(result) => Created(write(result)).as(JSON)
      case Right(e) => NotFound(s"""{"message":"${e.getMessage}"}""").as(JSON)
    }
  }

  def findWallet(userId: String, id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val wallet = walletService.getWallet(id, userId)

    wallet match {
      case Left(w) => Ok(write(w)).as(JSON)
      case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""").as(JSON)
    }
  }

  def findWallets(userId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val wallet = walletService.getWalletsByUser(userId)

    wallet match {
      case Left(w) => Ok(write(w)).as(JSON)
      case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""").as(JSON)
    }
  }
}