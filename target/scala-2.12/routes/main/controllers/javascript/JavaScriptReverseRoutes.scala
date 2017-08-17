
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers.javascript {

  // @LINE:7
  class ReverseStatusController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def probe: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.StatusController.probe",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }


}
