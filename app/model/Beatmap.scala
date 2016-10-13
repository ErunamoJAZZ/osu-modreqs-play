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
case class Beatmap(
                    id: Option[Long],
                    json: JsValue)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class BeatmapsTable(tag: Tag) extends Table[Beatmap](tag, "beatmap") {
  def * = (id.?, json) <> (Beatmap.tupled, Beatmap.unapply)

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val json = column[JsValue]("json", O.SqlType("TEXT"))
}

class BeatmapsDAO(dbConfig: DatabaseConfig[JdbcProfile]) {

  def insert(m: Beatmap): Future[Long] = {
    val a = (DAO.BeatmapsQuery returning DAO.BeatmapsQuery.map(_.id)) += m
    dbConfig.db.run(a)
  }

  def get(id: Long): Future[Option[Beatmap]] = {
    val q = DAO.BeatmapsQuery.filter(_.id === id)
    dbConfig.db.run(q.result.headOption)
  }

  def getLast2days: Future[Seq[Beatmap]] = {
    val twoDaysAgo = LocalDateTime.now.minusDays(2)
    val q = for {
      b <- DAO.BeatmapsQuery
      r <- DAO.ModRequestsQuery
      if b.id === r.beatmap_id &&
        r.time > twoDaysAgo
    } yield b
    dbConfig.db.run(q.result)
  }

}
