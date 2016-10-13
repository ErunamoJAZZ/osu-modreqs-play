package services

import java.time.LocalDateTime

import model._
import play.api.Configuration
import play.api.libs.json.{JsDefined, JsUndefined, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by erunamo on 8/10/16.
  */
class OsuAPI(beatmapsDAO: BeatmapsDAO, modRequestsDAO: ModRequestsDAO,
             wsClient: WSClient, configuration: Configuration) {

  implicit lazy val modeStaring_wr = Json.writes[ModeStaringJs]

  def modRequetPlz(nick: String, bm_type: Char, bm_id: String): Future[Unit] = {

    val osuApiKey = configuration.getConfig("osu").flatMap(_.getString("key")).getOrElse("")
    //ToDo!!!!!!!!!!!
    //Consulta el id principal de mapa, y guarda una copia del json.

    //https://b.ppy.sh/thumb/______s_id_.jpg
    //https://b.ppy.sh/preview/______s_id_.mp3

    val query_base = s"https://osu.ppy.sh/api/get_beatmaps?k=$osuApiKey"

    val query_general = s"$query_base&$bm_type=$bm_id"
    println(query_general)
    wsClient.url(query_general).get().map { r =>
      println(r.toString)

      //get beatmap_id from array head
      r.json.head match {
        case JsUndefined() => play.Logger.warn("Mapset does not exist?")
        case JsDefined(jsHead) =>
          (jsHead \ "beatmapset_id").asOpt[String] match {
            case Some(beatmapset_id) =>

              //Insert or Update Beatmap information
              beatmapsDAO.insert(
                Beatmap(
                  beatmapset_id.toLong,
                  (jsHead \ "artist").asOpt[String],
                  (jsHead \ "title").asOpt[String],
                  (jsHead \ "creator").asOpt[String],
                  (jsHead \ "bpm").asOpt[String],
                  (jsHead \ "favourite_count").asOpt[String]
                ))

              //Get all maps in set
              wsClient.url(s"$query_base&s=$beatmapset_id").get().map { list_maps =>
                val stars = (list_maps.json \\ "difficultyrating").map(_.as[String].toDouble)
                val versions = (list_maps.json \\ "version").map(_.as[String])
                val modes = (list_maps.json \\ "mode").map(_.as[String].toShort)

                val listModeStar = stars.zip(versions).zip(modes).map {
                  case ((star, version), mode) =>
                    ModeStaringJs(star, version, mode)
                }
                val lms_js = Json.toJson(listModeStar.sortBy(k => (k.mode, k.difficultyrating)))

                modRequestsDAO.insert(
                  ModRequest(
                    None,
                    LocalDateTime.now,
                    nick,
                    lms_js,
                    beatmapset_id.toLong
                  ))
              }
            case None => play.Logger.warn("Strange case where beatmap_id does not exist??")
          }
      }


    }





    Future.successful(Unit)
  }
}
