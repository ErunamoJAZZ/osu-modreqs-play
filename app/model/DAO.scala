package model

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import model.DriverDatabase.api._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Objeto DAO para el acceso unico a los objetos de consulta en la BD.
 */
object DAO {
  val BeatmapsQuery = TableQuery[BeatmapsTable]
  val ModRequestsQuery = TableQuery[ModRequestsTable]
  val SurveysQuery = TableQuery[SurveysTable]
}


class CreatorDB (dbConfig: DatabaseConfig[JdbcProfile]) {

  lazy val TablesSchema = Array(
    DAO.BeatmapsQuery.schema, DAO.ModRequestsQuery.schema, DAO.SurveysQuery.schema
  ).reduceLeft(_ ++ _)

  def create: Future[Unit] = {
    dbConfig.db.run(TablesSchema.create)
  }

  def drop:Future[Unit] = {
    dbConfig.db.run(TablesSchema.drop)
  }
}