package controllers

import model.{Beatmap, BeatmapsDAO, CreatorDB}
import play.api.i18n.Langs
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.OsuAPI

import scala.concurrent.ExecutionContext.Implicits.global

class MyController(
                    osuApi: OsuAPI,
                    creatorDB: CreatorDB,
                    beatmapsDAO: BeatmapsDAO) extends Controller {


  def test(t:String,map:String) = Action.async {
    osuApi.modRequetPlz("Eru",t.charAt(0), map).map(_ => Ok)
  }

  def index = Action {
    Ok(Html("<h1>Welcome</h1><p>Your new application is ready.</p>"))
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
