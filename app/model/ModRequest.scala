package model

import java.time.LocalDateTime
import javax.inject.Inject

import play.api.libs.json.JsValue
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import model.DriverDatabase.api._
import play.api.db.slick.DatabaseConfigProvider
import play.twirl.api.Html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/*
 * Tipo de clase para la utilización de albumes.
 */
case class ModeStaringJs(
                          difficultyrating: Double,
                          version: String,
                          mode: Short
                        ) {
  def getHtml: Html = {
    val urlStar =
      this.difficultyrating match {
        case x if x <= 1.50 => "https://osu.ppy.sh/images/easy.png"
        case x if x <= 2.25 => "https://osu.ppy.sh/images/normal.png"
        case x if x <= 3.75 => "https://osu.ppy.sh/images/hard.png"
        case x if x <= 5.25 => "https://osu.ppy.sh/images/insane.png"
        case _ => "https://osu.ppy.sh/images/expert.png"
      }

    val urlImage =
      if (this.mode == 0) urlStar
      else if (this.mode == 1) urlStar.replace(".png", "-t.png")
      else if (this.mode == 2) urlStar.replace(".png", "-f.png")
      else urlStar.replace(".png", "-m.png")

    Html(s"""<img src="$urlImage" title="$version ($difficultyrating)">""")
  }
}

case class ModRequest(
                       id: Option[Long],
                       time: LocalDateTime,
                       nick: String,
                       set: Seq[ModeStaringJs],
                       beatmap_id: Long)

/*
 * Clase Tabla, donde se define el mapeo Objeto-Relacional
 */
class ModRequestsTable(tag: Tag) extends Table[ModRequest](tag, "mod_request") {
  def * = (id.?, time, nick, set, beatmap_id) <> (ModRequest.tupled, ModRequest.unapply)

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val time = column[LocalDateTime]("time")
  val nick = column[String]("nick")
  val set = column[Seq[ModeStaringJs]]("set", O.SqlType("TEXT"))
  val beatmap_id = column[Long]("beatmap_id")

  lazy val beatmapFk = foreignKey("bm_survey_fk", beatmap_id, DAO.BeatmapsQuery)(r =>
    r.beatmapset_id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
}

class ModRequestsDAO(dbConfig: DatabaseConfig[JdbcProfile]) {

  /**
    *
    * @param m
    * @return
    */
  def insert(m: ModRequest): Future[Long] = {
    val a = (DAO.ModRequestsQuery returning DAO.ModRequestsQuery.map(_.id)) += m
    val runned = dbConfig.db.run(a)

    runned.onComplete {
      case Success(s) => play.Logger.debug(s"Inserted in ModRequest. Id: $s")
      case Failure(e) => play.Logger.error(s"Error in table ModRequest: $e")
    }
    runned
  }

  /**
    *
    * @param ms
    * @return
    */
  def insert(ms: List[ModRequest]): Future[Seq[Long]] = {
    val a = (DAO.ModRequestsQuery returning DAO.ModRequestsQuery.map(_.id)) ++= ms
    dbConfig.db.run(a)
  }

  /**
    *
    * @return
    */
  def getLast2days: Future[Seq[ModRequest]] = {
    val twoDaysAgo = LocalDateTime.now.minusDays(2)
    val q = DAO.ModRequestsQuery.filter(_.time > twoDaysAgo)

    dbConfig.db.run(q.sortBy(_.time.asc).result)
  }

  /**
    *
    * @return
    */
  def getLast2days222: Future[Seq[(ModRequest, Beatmap)]] = {
    val twoDaysAgo = LocalDateTime.now.minusDays(2)
    val q = for {
      b <- DAO.BeatmapsQuery
      m <- DAO.ModRequestsQuery
      if b.beatmapset_id === m.beatmap_id &&
        m.time > twoDaysAgo
    } yield b

    println(q.distinct.result.statements.mkString(";"))
    dbConfig.db.run(q.distinct.result).flatMap { beats =>
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      val a = beats.map { b =>
        println("<<<<<<<<<<<<<<<<<<<<<<")
        dbConfig.db.run(DAO.ModRequestsQuery
          .filter(_.beatmap_id === b.beatmapset_id).sortBy(_.time.desc).result.head)
          .map(m => (m, b))
      }
      Future.sequence(a)
    }
    /*val q2 = for {
      bm <- q.distinct
      mr <- DAO.ModRequestsQuery.filter(_.beatmap_id === bm.beatmapset_id).sortBy(_.time.desc).take(1)
    } yield {
      (mr, bm)
    }
    dbConfig.db.run(q2.result)*/
  }
}
