
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers {

  // @LINE:7
  class ReverseStatusController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def probe(): Call = {
      
      Call("GET", _prefix)
    }
  
  }


}
