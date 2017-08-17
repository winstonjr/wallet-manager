package controllers

import javax.inject._

import io.atleastonce.wallet.manager.BuildInfo
import org.json4s._
import org.json4s.native.Serialization._
import play.api.mvc._

@Singleton
class StatusController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val formats = DefaultFormats

  def probe() = Action { implicit request: Request[AnyContent] =>
    Ok(write(BuildInfo.toMap)).as(JSON)
  }
}
