package controllers

import java.time.{LocalDateTime, ZoneOffset}

import model.{Beatmap, BeatmapsDAO, CreatorDB, ModRequestsDAO}
import play.api.i18n.Langs
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.OsuAPI

import scala.concurrent.ExecutionContext.Implicits.global

class MyController(
                    osuApi: OsuAPI,
                    creatorDB: CreatorDB,
                    beatmapsDAO: BeatmapsDAO,
                    modRequestsDAO: ModRequestsDAO) extends Controller {

  implicit val localDateOrdering: Ordering[LocalDateTime] = Ordering.by(_.toEpochSecond(ZoneOffset.ofHours(-5)))

  def test(t: String, map: String) = Action.async {
    osuApi.modRequetPlz("Eru", t.charAt(0), map).map(_ => Ok)
  }

  def index = Action.async {
    for {
      m2 <- modRequestsDAO.getLast2days222
    } yield {
      val set = m2.sortBy(_._1.time).zipWithIndex.reverse
      Ok(views.html.beatmaps(set))
    }
  }

  def about = Action {
    Ok(views.html.about())
  }

  def createDB = Action.async {
    creatorDB.create
      //.flatMap(_ =>
      //  beatmapsDAO.insert(Beatmap(None, Json.obj("test" -> 2)))
      .map(_ => Ok("Listo :)"))
  }

  def dropDB = Action.async {
    creatorDB.drop.map(_ => Ok("Listo :("))
  }

}
