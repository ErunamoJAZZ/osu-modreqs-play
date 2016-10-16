
import com.softwaremill.macwire.MacwireMacros._
import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.routing.Router
import router.Routes
import slick.driver.JdbcProfile
import play.api.db.slick.{DbName, SlickComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import akka.actor.ActorSystem

/**
  * Application loader that wires up the application dependencies using Macwire
  */
class MyApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with MyComponents).application
  }
}

trait MyComponents
  extends BuiltInComponents
    with MyModule
    with AhcWSComponents // for wsClient
    with I18nComponents
    with DatabaseSlickModule {
  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = wire[Routes] withPrefix "/"
}

trait DatabaseSlickModule extends SlickComponents {
  lazy val dbConfig = api.dbConfig[JdbcProfile](DbName("default"))
}