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
case class ModRequest(
                       id: Option[Long],
                       time: LocalDateTime,
                       nick: String,
                       beatmap_id: Long)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class ModRequestsTable(tag: Tag) extends Table[ModRequest](tag, "mod_request") {
  def * = (id.?, time, nick, beatmap_id) <> (ModRequest.tupled, ModRequest.unapply)

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val time = column[LocalDateTime]("time")
  val nick = column[String]("nick")
  val beatmap_id = column[Long]("beatmap_id")

  lazy val beatmapFk = foreignKey("bm_survey_fk", beatmap_id, DAO.BeatmapsQuery)(r =>
    r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
}

class ModRequestsDAO (dbConfig: DatabaseConfig[JdbcProfile]) {

  def insert(m:ModRequest): Future[Long] = {
    val a = (DAO.ModRequestsQuery returning DAO.ModRequestsQuery.map(_.id)) += m
    dbConfig.db.run(a)
  }

  def insert(ms: List[ModRequest]): Future[Seq[Long]] = {
    val a = (DAO.ModRequestsQuery returning DAO.ModRequestsQuery.map(_.id)) ++= ms
    dbConfig.db.run(a)
  }
}
