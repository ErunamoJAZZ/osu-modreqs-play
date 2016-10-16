package controllers

import java.time.{LocalDateTime, ZoneOffset}

import model.{Beatmap, BeatmapsDAO, CreatorDB, ModRequestsDAO}
import play.api.Mode
import play.api.Environment
import play.api.i18n.Langs
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.OsuAPI

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MyController(
                    env: Environment,
                    osuApi: OsuAPI,
                    creatorDB: CreatorDB,
                    beatmapsDAO: BeatmapsDAO,
                    modRequestsDAO: ModRequestsDAO) extends Controller {

  // Little method for .sortBy()
  implicit val localDateOrdering: Ordering[LocalDateTime] =
  Ordering.by(_.toEpochSecond(ZoneOffset.ofHours(-5)))

  /**
    * Index page
    *
    * @return
    */
  def index = Action.async {
    for {
      m2 <- modRequestsDAO.getLast2days222
    } yield {
      val set = m2.sortBy(_._1.time).zipWithIndex.reverse
      println("---------------------------------------------------------------")
      Ok(views.html.beatmaps(set))
    }
  }

  def index3 = Action {
    val set = modRequestsDAO.getLast2days333.sortBy(_._1.time).zipWithIndex.reverse
    println("---------------------------------------------------------------")
    Ok(views.html.beatmaps(set))

  }


  /**
    * About page
    *
    * @return
    */
  def about = Action {
    Ok(views.html.about())
  }

  /**
    * Script for create Database
    *
    * @return
    */
  def createDB = Action.async {
    if (env.mode == Mode.Dev)
      creatorDB.createSQL
        .map(c => Ok(s"$c\n\nListo  :)"))
    else
      Future.successful(NotImplemented("NotImplemented"))
  }

  /**
    * Script for drop Database
    *
    * @return
    */
  def dropDB = Action.async {
    if (env.mode == Mode.Dev)
      creatorDB.dropSQL
        .map(d => Ok(s"$d\n\nListo :("))
    else
      Future.successful(NotImplemented("NotImplemented"))
  }

  /**
    * Just for testing
    *
    * @param t
    * @param map
    * @return
    */
  def test(t: String, map: String) = Action.async {
    if (env.mode == Mode.Dev)
      osuApi.modRequetPlz("Eru", t.charAt(0), map).map(_ => Ok)
    else
      Future.successful(NotImplemented("NotImplemented"))
  }

}
