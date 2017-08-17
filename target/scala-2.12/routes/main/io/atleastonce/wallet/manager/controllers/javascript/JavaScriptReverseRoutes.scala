
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:9
package io.atleastonce.wallet.manager.controllers.javascript {

  // @LINE:9
  class ReverseUserController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def getAll: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "io.atleastonce.wallet.manager.controllers.UserController.getAll",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "users"})
        }
      """
    )
  
    // @LINE:12
    def updateUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "io.atleastonce.wallet.manager.controllers.UserController.updateUser",
      """
        function() {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "users"})
        }
      """
    )
  
    // @LINE:10
    def findById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "io.atleastonce.wallet.manager.controllers.UserController.findById",
      """
        function(id0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "users/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0))})
        }
      """
    )
  
    // @LINE:11
    def createUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "io.atleastonce.wallet.manager.controllers.UserController.createUser",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "users"})
        }
      """
    )
  
  }


}
