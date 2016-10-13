package services

import java.time.LocalDateTime

import model.{Beatmap, BeatmapsDAO, ModRequest, ModRequestsDAO}
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by erunamo on 8/10/16.
  */
class OsuAPI(beatmapsDAO: BeatmapsDAO, modRequestsDAO: ModRequestsDAO,
             wsClient: WSClient, configuration: Configuration) {

  def modRequetPlz(nick:String, bm_type:Char, bm_id:String): Future[Unit] = {

    val osuApiKey = configuration.getConfig("osu").flatMap(_.getString("key"))
    //ToDo!!!!!!!!!!!
    //Consulta el id principal de mapa, y guarda una copia del json.
    wsClient.url(s"/api/get_beatmaps?$bm_type&$bm_id??????").get().map{ r =>
      beatmapsDAO.insert(Beatmap(None, r.json))
      modRequestsDAO.insert(ModRequest(None, LocalDateTime.now,nick, 0))
    }





    Future.successful(Unit)
  }
}
