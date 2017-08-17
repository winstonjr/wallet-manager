package io.atleastonce.wallet.manager.domain

case class User(id: String,
                name: String,
                accessKey: String,
                secretAccessKey: String,
                wallets: List[Wallet] = List.empty) {

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