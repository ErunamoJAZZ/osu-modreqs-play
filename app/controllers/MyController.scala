package controllers

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

  //implicit val localDateOrdering: Ordering[LocalDateTime] = Ordering.by(_.toEpochDay)

  def test(t: String, map: String) = Action.async {
    osuApi.modRequetPlz("Eru", t.charAt(0), map).map(_ => Ok)
  }

  def index = Action.async {
    for {
      m <- modRequestsDAO.getLast2days
      b <- beatmapsDAO.getInSet(m.map(_.beatmap_id))
    } yield {
      /*val mySet = m/*.sortBy(_.time)*/.map { modreq =>
        (modreq, b.find(_.beatmapset_id == modreq.beatmap_id).get)
      }*/
      val mySet2 = b.map { bmap =>
        (m.collectFirst {
          case i if (i.beatmap_id == bmap.beatmapset_id) => i
        }.get, bmap)
      }
      Ok(views.html.beatmaps(mySet2))
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
