package io.atleastonce.wallet.manager.controllers

import java.time._
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.PaymentTransaction
import io.atleastonce.wallet.manager.services.CreditCardService
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write
import play.api.libs.json.JsValue
import play.api.mvc._
import rapture.json._
import rapture.json.jsonBackends.jawn._

@Singleton
class CreditCardController @Inject()(cc: ControllerComponents,
                                     creditCardService: CreditCardService) extends AbstractController(cc) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def createCreditCard(userId: String, walletId: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val body = request.body.toString
    JsonSchemaValidator.validate(SchemaResources.createCreditCardSchema, body) match {
      case Right(err: SchemaValidationException) =>
        PreconditionFailed(s"""{"errors":"$err"}""").as(JSON)
      case Left(_) =>
        val data = Json.parse(body)
        val localDate = LocalDate.parse(data.expirationDate.toBareString.replaceAll("\"", ""))

        creditCardService.save(walletId,
          data.number.toBareString.replaceAll("\"", ""),
          data.cvv.toBareString.replaceAll("\"", ""),
          data.dueDate.toBareString.toInt,
          LocalDateTime.of(localDate, LocalTime.now),
          data.credit.toBareString.toFloat) match {
          case Left(result) => Ok(write(result)).as(JSON)
          case Right(err) => InternalServerError(s"""{"message":"${err.getMessage}"}""").as(JSON)
        }
    }
  }

  def updateCreditCard(userId: String, walletId: String, id: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val body = request.body.toString
    JsonSchemaValidator.validate(SchemaResources.updateCreditCardSchema, body) match {
      case Right(err: SchemaValidationException) =>
        PreconditionFailed(s"""{"errors":"$err"}""").as(JSON)
      case Left(_) =>
        val data = Json.parse(body)
        val localDate = LocalDate.parse(data.expirationDate.toBareString.replaceAll("\"", ""))

        creditCardService.update(id,
          walletId,
          data.number.toBareString.replaceAll("\"", ""),
          data.cvv.toBareString.replaceAll("\"", ""),
          data.dueDate.toBareString.toInt,
          LocalDateTime.of(localDate, LocalTime.now),
          data.credit.toBareString.toFloat,
          false) match {
          case Left(result) => Created(write(result)).as(JSON)
          case Right(e) => NotFound(s"""{"message":"${e.getMessage}"}""").as(JSON)
        }
    }
  }

  def findCreditCard(userId: String, walletId: String, id: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val cc = creditCardService.getCreditCard(id, walletId)

    cc match {
      case Left(w) => Ok(write(w)).as(JSON)
      case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""").as(JSON)
    }
  }

  def findCreditCards(userId: String, walletId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val wallet = creditCardService.getCreditCardsByWallet(walletId)

    wallet match {
      case Left(w) => Ok(write(w)).as(JSON)
      case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""").as(JSON)
    }
  }

  def payment(userId: String, walletId: String, id: String): Action[JsValue] = Action(parse.json) { implicit request =>
    val body = request.body.toString
    JsonSchemaValidator.validate(SchemaResources.paymentSchema, body) match {
      case Right(err: SchemaValidationException) =>
        PreconditionFailed(s"""{"errors":"$err"}""").as(JSON)
      case Left(_) =>
        val data = Json.parse(body)
        val wallet = creditCardService.payment(id, walletId,
          PaymentTransaction(data.value.toBareString.toFloat))

        wallet match {
          case Left(w) => Ok(write(w)).as(JSON)
          case Right(m) => NotFound(s"""{"message":"${m.getMessage}"}""").as(JSON)
        }
    }
  }
}