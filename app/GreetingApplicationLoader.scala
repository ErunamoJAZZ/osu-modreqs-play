
import com.softwaremill.macwire.MacwireMacros._
import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.routing.Router
import router.Routes
import slick.driver.JdbcProfile
import play.api.db.slick.{DbName, SlickComponents}

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with GreetingComponents).application
  }
}

trait GreetingComponents
  extends BuiltInComponents
    with GreetingModule
    with I18nComponents
    with DatabaseSlickModule {
  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = wire[Routes] withPrefix "/"
}

trait DatabaseSlickModule extends SlickComponents {
  lazy val dbConfig = api.dbConfig[JdbcProfile](DbName("default"))
}