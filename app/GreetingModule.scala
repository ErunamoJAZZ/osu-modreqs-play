import controllers.GreeterController
import model.{BeatmapsDAO, CreatorDB, ModRequestsDAO, SurveysDAO}
import slick.backend.DatabaseConfig
import play.api.i18n.Langs
import services.ServicesModule
import slick.driver.JdbcProfile
import play.api.Configuration

trait GreetingModule extends ServicesModule {

  import com.softwaremill.macwire.MacwireMacros._

  def dbConfig: DatabaseConfig[JdbcProfile]

  def configuration: Configuration

  lazy val creatorDB = wire[CreatorDB]
  lazy val beatmapsDAO = wire[BeatmapsDAO]
  lazy val modRequest = wire[ModRequestsDAO]
  lazy val surveysDAO = wire[SurveysDAO]
  lazy val greeterController = wire[GreeterController]

  def langs: Langs
}
