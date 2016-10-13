package controllers

import model.{Beatmap, BeatmapsDAO, CreatorDB}
import models.Greeting
import play.api.i18n.Langs
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import services.GreetingService

import scala.concurrent.ExecutionContext.Implicits.global

class GreeterController(greetingService: GreetingService, langs: Langs,
                        creatorDB: CreatorDB,
                        beatmapsDAO: BeatmapsDAO) extends Controller {

  val greetingsList = Seq(
    Greeting(1, greetingService.greetingMessage("en"), "sameer"),
    Greeting(2, greetingService.greetingMessage("it"), "sam")
  )

  def greetings = Action {
    Ok(Json.toJson(greetingsList))
  }

  def greetInMyLanguage = Action {
    Ok(greetingService.greetingMessage(langs.preferred(langs.availables).language))
  }

  def index = Action {
    Ok(Html("<h1>Welcome</h1><p>Your new application is ready.</p>"))
  }

  def createDB = Action.async {
    creatorDB.create.flatMap( _ =>
    beatmapsDAO.insert(Beatmap(None,Json.obj("test"->2)))
      .map(_ =>Ok("Listo :)")))
  }
  def dropDB = Action.async {
    creatorDB.drop.map(_ =>Ok("Listo :("))
  }

}
