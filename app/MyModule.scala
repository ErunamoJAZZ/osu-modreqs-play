import controllers.MyController
import model.{BeatmapsDAO, CreatorDB, ModRequestsDAO, SurveysDAO}
import slick.backend.DatabaseConfig
import play.api.i18n.Langs
import slick.driver.JdbcProfile
import play.api.{Configuration, Environment}
import play.api.libs.ws.WSClient
import services.{ChoListener, OsuAPI}
import akka.actor.ActorSystem
import play.api.inject.{ApplicationLifecycle, DefaultApplicationLifecycle}

trait MyModule {

  import com.softwaremill.macwire.MacwireMacros._

  def dbConfig: DatabaseConfig[JdbcProfile]

  def configuration: Configuration

  def environment: Environment

  def wsClient: WSClient

  def actorSystem: ActorSystem

  def applicationLifecycle: ApplicationLifecycle

  lazy val creatorDB = wire[CreatorDB]
  lazy val beatmapsDAO = wire[BeatmapsDAO]
  lazy val modRequest = wire[ModRequestsDAO]
  lazy val surveysDAO = wire[SurveysDAO]
  lazy val greeterController = wire[MyController]

  lazy val osuApi = wire[OsuAPI]
  val choListener = wire[ChoListener]
}
