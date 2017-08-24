package io.atleastonce.wallet.manager.repositories

import javax.inject.{Inject, Singleton}

import io.atleastonce.wallet.manager.domain.WalletDTO
import io.getquill.{PostgresJdbcContext, SnakeCase}
import play.api.Configuration

@Singleton
class CreditCardRepo @Inject()(configuration: Configuration) {
//  val logger = play.api.Logger(this.getClass)
//  lazy val ctx = new PostgresJdbcContext[SnakeCase](configuration.getConfig("ctx").get.underlying)
//  import ctx._
//  implicit val queryMeta: SchemaMeta[WalletDTO] = schemaMeta[WalletDTO](
//    "wallet.wallets"
//  )
}
