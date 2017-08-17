
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/winston/projects/personal/wallet-manager/conf/routes
// @DATE:Tue Aug 15 19:04:01 BRT 2017

package io.atleastonce.wallet.manager.controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final io.atleastonce.wallet.manager.controllers.ReverseUserController UserController = new io.atleastonce.wallet.manager.controllers.ReverseUserController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final io.atleastonce.wallet.manager.controllers.javascript.ReverseUserController UserController = new io.atleastonce.wallet.manager.controllers.javascript.ReverseUserController(RoutesPrefix.byNamePrefix());
  }

}
