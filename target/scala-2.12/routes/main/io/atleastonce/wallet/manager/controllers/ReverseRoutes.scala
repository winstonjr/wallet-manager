
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:9
package io.atleastonce.wallet.manager.controllers {

  // @LINE:9
  class ReverseUserController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def getAll(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "users")
    }
  
    // @LINE:12
    def updateUser(): Call = {
      
      Call("PUT", _prefix + { _defaultPrefix } + "users")
    }
  
    // @LINE:10
    def findById(id:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "users/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
    // @LINE:11
    def createUser(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "users")
    }
  
  }


}
