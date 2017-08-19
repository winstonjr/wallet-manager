package io.atleastonce.wallet.manager.domain

import java.security.MessageDigest
import java.util.UUID

case class User(id: String,
                name: String,
                accessKey: String,
                secretAccessKey: String,
                wallets: List[Wallet] = List.empty) {

  private val uuidReg = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$".r
  require(null != id && id.nonEmpty, "id é obrigatório")
  require(uuidReg.findFirstIn(id).isDefined, "id deve ser um UUID")
  require(null != name && name.nonEmpty, "name é obrigatório")
  require(null != accessKey && accessKey.nonEmpty, "accessKey é obrigatório")
  require(uuidReg.findFirstIn(accessKey).isDefined, "accessKey deve ser um UUID")
  require(null != secretAccessKey && secretAccessKey.nonEmpty, "secretAccessKey é obrigatório")

  /**
    * Método responsável por adicionar uma carteira em um usuário
    *
    * - A wallet can only belong to a user
    * @param wallet carteira a ser adicionada
    * @return O usuário atualizado ou uma mensagem de erro
    */
  def addWallet(wallet: Wallet): Either[User, Throwable] = {
    // TODO: Salvar essa informação no banco de dados
    val newWalletList = this.wallets :+ wallet
    Left(this.copy(wallets = newWalletList))
  }
}

object User {
  def generateSecretKey:String =
    MessageDigest
      .getInstance("SHA-256")
      .digest(UUID.randomUUID().toString.getBytes())
      .map("%02X" format _)
      .mkString
}