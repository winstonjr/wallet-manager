package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class StatusControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "StatusController GET" should {

    "probe that application is up and running" in {
      val controller = new StatusController(stubControllerComponents())
      val statusData = controller.probe().apply(FakeRequest(GET, "/"))

      status(statusData) mustBe OK
      contentType(statusData) mustBe Some("application/json")
    }
  }
}
