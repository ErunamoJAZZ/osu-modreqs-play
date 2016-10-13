package model

import java.time.LocalDateTime
import javax.inject.Inject

import play.api.libs.json.JsValue
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import model.DriverDatabase.api._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Tipo de clase para la utilización de albumes.
 */
case class Survey(
                   id: Option[Long],
                   time: LocalDateTime,
                   text: String,
                   beatmap_id: Long)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class SurveysTable(tag: Tag) extends Table[Survey](tag, "survey") {
  def * = (id.?, time, text, beatmap_id) <> (Survey.tupled, Survey.unapply)

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val time = column[LocalDateTime]("time")
  val text = column[String]("text")
  val beatmap_id = column[Long]("beatmap_id")

  lazy val beatmapFk = foreignKey("bm_survey_fk", beatmap_id, DAO.BeatmapsQuery)(r =>
    r.beatmapset_id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
}

class SurveysDAO (dbConfig: DatabaseConfig[JdbcProfile]) {

  def insert(m:Survey): Future[Long] = {
    val a = (DAO.SurveysQuery returning DAO.SurveysQuery.map(_.id)) += m
    dbConfig.db.run(a)
  }

}