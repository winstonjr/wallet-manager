//package io.atleastonce.wallet.manager.controllers
//
//import controllers.StatusController
//import org.scalatestplus.play._
//import org.scalatestplus.play.guice._
//import play.api.libs.json.Json
//import play.api.test._
//import play.api.test.Helpers._
//
//class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
//  "UserController POST" should {
//    "Must create a new user" in {
//      val controller = new StatusController(stubControllerComponents())
//      val userData = controller.probe().apply(FakeRequest(POST, "/users")
//        .withJsonBody(Json.parse("""{ "name": "n1" }""")))
//
//      status(userData) mustBe OK
//      contentType(userData) mustBe Some("application/json")
//    }
//  }
//
////  "StatusController GET" should {
////
////    "probe that application is up and running" in {
////      val controller = new StatusController(stubControllerComponents())
////      val statusData = controller.probe().apply(FakeRequest(GET, "/"))
////
////      status(statusData) mustBe OK
////      contentType(statusData) mustBe Some("application/json")
////    }
////  }
//}
