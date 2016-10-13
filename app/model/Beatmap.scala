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
                    beatmapset_id: Long,
                    artist: Option[String],
                    title: Option[String],
                    creator: Option[String],
                    bpm: Option[String],
                    favourite_count: Option[String]
                  )

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class BeatmapsTable(tag: Tag) extends Table[Beatmap](tag, "beatmap") {
  def * = (beatmapset_id, artist, title, creator,
    bpm, favourite_count) <> (Beatmap.tupled, Beatmap.unapply)

  //val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val beatmapset_id = column[Long]("beatmapset_id", O.PrimaryKey)
  val artist = column[Option[String]]("artist", O.SqlType("TEXT"))
  val title = column[Option[String]]("title", O.SqlType("TEXT"))
  val creator = column[Option[String]]("creator", O.SqlType("TEXT"))
  val bpm = column[Option[String]]("bpm", O.SqlType("TEXT"))
  val favourite_count = column[Option[String]]("favourite_count")

}

class BeatmapsDAO(dbConfig: DatabaseConfig[JdbcProfile]) {

  def insert(m: Beatmap): Future[Option[Long]] = {
    val a = (DAO.BeatmapsQuery returning DAO.BeatmapsQuery
      .map(_.beatmapset_id))
      .insertOrUpdate(m)
    dbConfig.db.run(a)
  }

  def get(beatmapset_id: Long): Future[Option[Beatmap]] = {
    val q = DAO.BeatmapsQuery.filter(_.beatmapset_id === beatmapset_id)
    dbConfig.db.run(q.result.headOption)
  }

  def getLast2days: Future[Seq[Beatmap]] = {
    val twoDaysAgo = LocalDateTime.now.minusDays(2)
    val q = for {
      b <- DAO.BeatmapsQuery
      r <- DAO.ModRequestsQuery
      if b.beatmapset_id === r.beatmap_id &&
        r.time > twoDaysAgo
    } yield b
    dbConfig.db.run(q.result)
  }

}
