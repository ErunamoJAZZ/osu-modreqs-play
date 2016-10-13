import controllers.GreeterController
import model.{BeatmapsDAO, CreatorDB, ModRequestsDAO, SurveysDAO}
import slick.backend.DatabaseConfig
import play.api.i18n.Langs
import slick.driver.JdbcProfile
import play.api.Configuration
import play.api.libs.ws.WSClient
import services.{ChoListener, OsuAPI}
import akka.actor.ActorSystem
import play.api.inject.{ApplicationLifecycle, DefaultApplicationLifecycle}

trait GreetingModule {

  import com.softwaremill.macwire.MacwireMacros._

  def dbConfig: DatabaseConfig[JdbcProfile]

  def configuration: Configuration

  def wsClient: WSClient

  def actorSystem: ActorSystem

  //def lifecycle: ApplicationLifecycle

  lazy val creatorDB = wire[CreatorDB]
  lazy val beatmapsDAO = wire[BeatmapsDAO]
  lazy val modRequest = wire[ModRequestsDAO]
  lazy val surveysDAO = wire[SurveysDAO]
  lazy val greeterController = wire[GreeterController]

  lazy val osuApi = wire[OsuAPI]
  lazy val choListener = wire[ChoListener]
}
