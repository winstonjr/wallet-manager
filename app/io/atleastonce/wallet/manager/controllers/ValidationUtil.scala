package io.atleastonce.wallet.manager.controllers

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.util.{Failure, Success, Try}

object JsonSchemaValidator {

  def validate(jsonSchema: String,
               json: String,
               schemaValidator: SchemaValidator = SchemaValidator()): Either[String, SchemaValidationException] = {
    Try {
      (Json.parse(jsonSchema), Json.parse(json))
    } match {
      case Success((schema, instance)) =>
        schema.validate[SchemaType] match {
          case JsSuccess(validSchema, _) =>
            schemaValidator.validate(validSchema)(instance) match {
              case JsSuccess(validInstance, _) =>
                Left(validInstance.toString)
              case JsError(errors) =>
                val listErrors: List[String] = errors
                  .map(
                    e =>
                      e._2
                        .map(x => s"${if (e._1.path.nonEmpty) s"${e._1.path.mkString}: " else ""}${x.message}")
                        .head
                  )
                  .toList
                Right(SchemaValidationException(listErrors.mkString(",")))
            }
          case JsError(invalidSchema) =>
            Right(SchemaValidationException(s"Invalid JSON schema. $invalidSchema"))
        }
      case Failure(ex) =>
        Logger.error(s"Error while trying to parse request body or json schema", ex)
        Right(SchemaValidationException(ex.getMessage))
    }
  }

}

case class SchemaValidationException(msg: String) extends Exception(msg)

object SchemaResources {
  val createUserSchema: String = """{
                           |  "$schema": "http://json-schema.org/draft-04/schema#",
                           |  "definitions": {},
                           |  "id": "createUserSchema",
                           |  "properties": {
                           |    "name": {
                           |      "id": "/properties/name",
                           |      "type": "string"
                           |    }
                           |  },
                           |  "required": ["name"],
                           |  "type": "object"
                           |}""".stripMargin

  val updateUserSchema: String = """{
                           |  "$schema": "http://json-schema.org/draft-04/schema#",
                           |  "definitions": {},
                           |  "id": "updateUserSchema",
                           |  "properties": {
                           |    "name": {
                           |      "id": "/properties/name",
                           |      "type": "string"
                           |    }
                           |  },
                           |  "required": ["name"],
                           |  "type": "object"
                           |}""".stripMargin

  val createWalletSchema: String = """{
                                     |  "$schema": "http://json-schema.org/draft-04/schema#",
                                     |  "definitions": {},
                                     |  "id": "createWalletSchema",
                                     |  "properties": {
                                     |    "credit": {
                                     |      "id": "/properties/credit",
                                     |      "type": "number"
                                     |    }
                                     |  },
                                     |  "required": ["credit"],
                                     |  "type": "object"
                                     |}""".stripMargin

  val updateWalletSchema: String = """{
                                     |  "$schema": "http://json-schema.org/draft-04/schema#",
                                     |  "definitions": {},
                                     |  "id": "updateWalletSchema",
                                     |  "properties": {
                                     |    "credit": {
                                     |      "id": "/properties/credit",
                                     |      "type": "number"
                                     |    }
                                     |  },
                                     |  "required": ["credit"],
                                     |  "type": "object"
                                     |}""".stripMargin

  val purchaseSchema: String = """{
                                     |  "$schema": "http://json-schema.org/draft-04/schema#",
                                     |  "definitions": {},
                                     |  "id": "purchaseSchema",
                                     |  "properties": {
                                     |    "value": {
                                     |      "id": "/properties/value",
                                     |      "type": "number"
                                     |    }
                                     |  },
                                     |  "required": ["value"],
                                     |  "type": "object"
                                     |}""".stripMargin

  val createCreditCardSchema: String = """{
                                   |  "$schema": "http://json-schema.org/draft-04/schema#",
                                   |  "definitions": {},
                                   |  "id": "createCreditCardSchema",
                                   |  "properties": {
                                   |    "credit": {
                                   |      "id": "/properties/credit",
                                   |      "type": "integer"
                                   |    },
                                   |    "cvv": {
                                   |      "id": "/properties/cvv",
                                   |      "type": "string"
                                   |    },
                                   |    "dueDate": {
                                   |      "id": "/properties/dueDate",
                                   |      "type": "integer"
                                   |    },
                                   |    "expirationDate": {
                                   |      "id": "/properties/expirationDate",
                                   |      "type": "string"
                                   |    },
                                   |    "number": {
                                   |      "id": "/properties/number",
                                   |      "type": "string"
                                   |    }
                                   |  },
                                   |  "required":["credit", "cvv", "dueDate", "expirationDate", "number"],
                                   |  "type": "object"
                                   |}""".stripMargin

  val updateCreditCardSchema: String = """{
                                         |  "$schema": "http://json-schema.org/draft-04/schema#",
                                         |  "definitions": {},
                                         |  "id": "updateCreditCardSchema",
                                         |  "properties": {
                                         |    "credit": {
                                         |      "id": "/properties/credit",
                                         |      "type": "integer"
                                         |    },
                                         |    "cvv": {
                                         |      "id": "/properties/cvv",
                                         |      "type": "string"
                                         |    },
                                         |    "dueDate": {
                                         |      "id": "/properties/dueDate",
                                         |      "type": "integer"
                                         |    },
                                         |    "expirationDate": {
                                         |      "id": "/properties/expirationDate",
                                         |      "type": "string"
                                         |    },
                                         |    "number": {
                                         |      "id": "/properties/number",
                                         |      "type": "string"
                                         |    }
                                         |  },
                                         |  "required":["credit", "cvv", "dueDate", "expirationDate", "number"],
                                         |  "type": "object"
                                         |}""".stripMargin
}