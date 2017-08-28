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
* listar todos os usuários
* listar usuário específico
* atualizar um usuário
##### Carteiras
* criar nova carteira
* atualizar carteira
* listar todas as carteiras de um usuário
* fazer uma compra utilizando carteira
##### Cartões de crédito


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