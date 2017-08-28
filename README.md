# wallet-manager
Wallet Challenge
## deploy
### pré-requisitos
* [flyway](https://flywaydb.org/) (migrations de banco de dados)
* [sbt](http://www.scala-sbt.org/) (scala building tool)
* [httpie](https://httpie.org/) (cURL mais simples ;D)
### migration do banco de dados
* Acesso a pasta [db]
* configurar os campos do arquivo [flyway.conf]
  - flyway.url=[URL do banco de dados]
  - flyway.user=[usuário de acesso ao banco de dados]
* executar a linha de comando:
```
flyway migrate
```
* Digitar a senha do flyway quando solicitado
### execução do projeto
```sbtshell
sbt \
    -Dctx.dataSource.password=[senha do banco de dados]         \
    -Dctx.dataSource.user=[usuário do banco de dados]           \
    -Dctx.dataSource.databaseName=[nome do banco de dados]      \
    -Dctx.dataSource.portNumber=[porta do banco de dados]       \
    -Dctx.dataSource.serverName=[IP ou URI do banco de dados]   \
    -Dctx.dataSource.ssl=[Se utiliza ssl ou não true/false]     \
    -Dctx.dataSource.sslfactory=[qual factory caso utilize ssl] \
run
```
### deploy do projeto
* Na pasta raiz do projeto executar a seguinte linha de comando:
```sbtshell
sbt universal:packageBin
```
* Procurar a pasta universal, geralmente no caminho:
```
[projeto]/target/universal
```
* Descompactar o pacote gerado
```
unzip wallet-manager-1.0.0.0.zip
```
* Acessar o novo diretório
```
cd wallet-manager-1.0.0.0/bin
```
* Executar a seguinte linha de comando:
```
./wallet-manager
    -Dctx.dataSource.password=[senha do banco de dados]       \
    -Dctx.dataSource.user=[usuário do banco de dados]         \
    -Dctx.dataSource.databaseName=[nome do banco de dados]    \
    -Dctx.dataSource.portNumber=[porta do banco de dados]     \
    -Dctx.dataSource.serverName=[IP ou URI do banco de dados] \
    -Dctx.dataSource.ssl=[Se utiliza ssl ou não true/false]
```
### testes unitários
Para executar os testes utilizar a seguinte linha de comando.:
```sbtshell
sbt test
```
### testes via linha de comando
##### Usuários
* criar um novo usuário
```
echo '{"name":"Winston wallet"}' | http POST localhost:9000/users
```
* listar todos os usuários
```
http GET localhost:9000/users
```
* listar usuário específico
```
http GET localhost:9000/users/$userId
```
* atualizar um usuário
```
http GET localhost:9000/users/$userId
```
##### Carteiras
echo '{"value":100.00}' |http POST localhost:9000/users/f9f392f2-772d-4085-8354-15af9989d5cc/wallets/f0bd4631-32e4-4905-af9d-7c626924c8ff/purchases

* criar nova carteira
```
echo '{"credit":0.00}' | http POST localhost:9000/users/$userId/wallets

```
* atualizar carteira
```
echo '{"credit":1200.00}' | http PUT localhost:9000/users/$userId/wallets/$walletId
```
* listar todas as carteiras de um usuário
```
http GET localhost:9000/users/$userId/wallets

```
* fazer uma compra utilizando carteira
```
echo '{"value":1000.00}' |http POST localhost:9000/users/f9f392f2-772d-4085-8354-15af9989d5cc/wallets/f0bd4631-32e4-4905-af9d-7c626924c8ff/purchases
```
* listar uma carteira de um usuário
```
http GET localhost:9000/users/$userId/wallets/$walletId
```
##### Cartões de crédito



http GET  localhost:9000/users/f9f392f2-772d-4085-8354-15af9989d5cc/wallets/f0bd4631-32e4-4905-af9d-7c626924c8ff/cards/625e2ed0-7977-47d1-90de-e80ab7996152
* listar cartões de uma carteira
```
http GET  localhost:9000/users/$userId/wallets/$walletId/cards
```
* listar cartão de uma carteira
```
http GET  localhost:9000/users/$userId/wallets/$walletId/cards/$ccId
```
* criar um cartão
```
echo '{ "number": "1234123412341234", "cvv": "123", "dueDate": 25, "credit": 1000.00, "expirationDate": "2020-06-01" }' | http POST localhost:9000/users/$userId/wallets/$walletId/cards
```
* atualizar um cartão
```
echo '{ "number": "1234123412341234", "cvv": "123", "dueDate": 20, "expirationDate": "2027-08-20", "credit": 3000.00 }' | http PUT  localhost:9000/users/$userId/wallets/$walletId/cards/$ccId
```
* realizar uma transação de pagamento em um cartão
```
echo '{ "value": 1000.00 }' | http POST localhost:9000/users/$userId/wallets/$walletId/cards/$ccId/payments
```
## problemas
* Concorrencia nas atualizações
* Provável baixa performance nos serviços (muitas consultas n+1 devido ao modelo de dados)
* Falta de segurança nos endpoints
* Dados sensíveis transitando e guardados sem criptografia
* O multi-tenant não está totalmente correto, podem existir adições cruzadas nas carteiras
* Não considera snapshots para o calculo do cartão (todo histórico de transações precisa estar na memória)
* testes integrados não foram feitos
## novidades (para mim)
* Utilização do ORM Quill
* Utilização do Rapture para de-serialização de dados