
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:7
  StatusController_0: controllers.StatusController,
  // @LINE:9
  UserController_1: io.atleastonce.wallet.manager.controllers.UserController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:7
    StatusController_0: controllers.StatusController,
    // @LINE:9
    UserController_1: io.atleastonce.wallet.manager.controllers.UserController
  ) = this(errorHandler, StatusController_0, UserController_1, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, StatusController_0, UserController_1, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.StatusController.probe"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """users""", """io.atleastonce.wallet.manager.controllers.UserController.getAll"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """users/""" + "$" + """id<[^/]+>""", """io.atleastonce.wallet.manager.controllers.UserController.findById(id:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """users""", """io.atleastonce.wallet.manager.controllers.UserController.createUser"""),
    ("""PUT""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """users""", """io.atleastonce.wallet.manager.controllers.UserController.updateUser"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:7
  private[this] lazy val controllers_StatusController_probe0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_StatusController_probe0_invoker = createInvoker(
    StatusController_0.probe,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.StatusController",
      "probe",
      Nil,
      "GET",
      this.prefix + """""",
      """ Probe URL""",
      Seq()
    )
  )

  // @LINE:9
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_getAll1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("users")))
  )
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_getAll1_invoker = createInvoker(
    UserController_1.getAll,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "io.atleastonce.wallet.manager.controllers.UserController",
      "getAll",
      Nil,
      "GET",
      this.prefix + """users""",
      """""",
      Seq()
    )
  )

  // @LINE:10
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_findById2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("users/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_findById2_invoker = createInvoker(
    UserController_1.findById(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "io.atleastonce.wallet.manager.controllers.UserController",
      "findById",
      Seq(classOf[String]),
      "GET",
      this.prefix + """users/""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:11
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_createUser3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("users")))
  )
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_createUser3_invoker = createInvoker(
    UserController_1.createUser,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "io.atleastonce.wallet.manager.controllers.UserController",
      "createUser",
      Nil,
      "POST",
      this.prefix + """users""",
      """""",
      Seq()
    )
  )

  // @LINE:12
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_updateUser4_route = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("users")))
  )
  private[this] lazy val io_atleastonce_wallet_manager_controllers_UserController_updateUser4_invoker = createInvoker(
    UserController_1.updateUser,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "io.atleastonce.wallet.manager.controllers.UserController",
      "updateUser",
      Nil,
      "PUT",
      this.prefix + """users""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:7
    case controllers_StatusController_probe0_route(params) =>
      call { 
        controllers_StatusController_probe0_invoker.call(StatusController_0.probe)
      }
  
    // @LINE:9
    case io_atleastonce_wallet_manager_controllers_UserController_getAll1_route(params) =>
      call { 
        io_atleastonce_wallet_manager_controllers_UserController_getAll1_invoker.call(UserController_1.getAll)
      }
  
    // @LINE:10
    case io_atleastonce_wallet_manager_controllers_UserController_findById2_route(params) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        io_atleastonce_wallet_manager_controllers_UserController_findById2_invoker.call(UserController_1.findById(id))
      }
  
    // @LINE:11
    case io_atleastonce_wallet_manager_controllers_UserController_createUser3_route(params) =>
      call { 
        io_atleastonce_wallet_manager_controllers_UserController_createUser3_invoker.call(UserController_1.createUser)
      }
  
    // @LINE:12
    case io_atleastonce_wallet_manager_controllers_UserController_updateUser4_route(params) =>
      call { 
        io_atleastonce_wallet_manager_controllers_UserController_updateUser4_invoker.call(UserController_1.updateUser)
      }
  }
}
