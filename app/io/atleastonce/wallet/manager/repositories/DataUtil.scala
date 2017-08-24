package io.atleastonce.wallet.manager.repositories

import io.getquill.{PostgresJdbcContext, SnakeCase}

object DataUtil {
  lazy val ctx = new PostgresJdbcContext[SnakeCase]("ctx")
}
