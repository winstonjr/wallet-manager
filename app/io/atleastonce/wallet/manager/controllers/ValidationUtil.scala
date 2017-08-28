package io.atleastonce.wallet.manager.controllers

import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.github.fge.jsonschema.core.util.AsJson
import com.github.fge.jackson.JacksonUtils

object ValidationUtil {
  private val VALIDATOR = JsonSchemaFactory.byDefault.getValidator

  private def buildResult(rawSchema: String, rawData: String): JsonNode = {
    val ret = JsonNodeFactory.instance.objectNode

    val schemaNode = ret.remove(rawSchema)
    val data = ret.remove(rawData)

    val report = VALIDATOR.validateUnchecked(schemaNode, data)
    val success = report.isSuccess
    val node = report.asInstanceOf[AsJson].asJson

    ret.put("valid", success)
    ret.put("results", JacksonUtils.prettyPrint(node))
    ret
  }
}
