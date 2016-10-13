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

    val osuApiKey = configuration.getConfig("osu").flatMap(_.getString("key")).getOrElse("")
    //ToDo!!!!!!!!!!!
    //Consulta el id principal de mapa, y guarda una copia del json.

    //https://b.ppy.sh/thumb/______s_id_.jpg
    //https://b.ppy.sh/preview/______s_id_.mp3

    //"difficultyrating"
    //"bpm"

    val query_url = s"https://osu.ppy.sh/api/get_beatmaps?k=$osuApiKey&$bm_type=$bm_id"
    println(query_url)
    wsClient.url(query_url).get().map{ r =>
      println(r.toString)
      beatmapsDAO.insert(Beatmap(None, r.json))
      modRequestsDAO.insert(ModRequest(None, LocalDateTime.now,nick, 0))
    }





    Future.successful(Unit)
  }
}
