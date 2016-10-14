package services

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.time.LocalDateTime

import model.ModRequest
import play.api.Configuration
import akka.actor.ActorSystem
import play.api.inject.ApplicationLifecycle

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by erunamo on 8/10/16.
  */
class ChoListener(
                   lifecycle: ApplicationLifecycle, //ToDo
                   configuration: Configuration,
                   actorSystem: ActorSystem,
                   osuAPI: OsuAPI) {


  actorSystem.scheduler.scheduleOnce(3.seconds) {

    println("Init ChoListener Service!!!")

    val server = "cho.ppy.sh"
    val pass = configuration.getConfig("irc").flatMap(_.getString("serverPassword")).getOrElse("")
    val nick = configuration.getConfig("irc").flatMap(_.getString("username")).getOrElse("")
    val login = nick

    // The channel which the bot will join.
    val channel = "#modreqs"
    val privmsg = s"PRIVMSG $channel :"

    //otros cosos
    //from https://mybuddymichael.com/writings/a-regular-expression-for-irc-messages.html
    val osuPattern =
    """\b((https?:\/\/)(osu\.ppy\.sh)(\/[bs]{1}\/)([\d\.-]*))""".r

    // Connect directly to the IRC server.
    val socket = new Socket(server, 6667)
    val writer = new BufferedWriter(
      new OutputStreamWriter(socket.getOutputStream))
    val reader = new BufferedReader(
      new InputStreamReader(socket.getInputStream))

    /**
      * Code for close connection in restart server
      * ToDo
      */
    lifecycle.addStopHook { () =>
      Future.successful {
        writer.write(s"QUIT :Good bye!\r\n")
        writer.flush()
        writer.close()
        reader.close()
        socket.close()
        println("<<<<    Connection closed!!!!!    >>>>")
      }
    }

    // Log on to the server.
    writer.write("PASS " + pass + "\r\n")
    writer.write("NICK " + nick + "\r\n")
    writer.write("USER " + login + " 8 * : Modreqs Scala Testing\r\n")
    writer.flush()

    //Read lines in a stream!
    Stream.continually(reader.readLine)
      .takeWhile(_ != null)
      .foreach { line =>

        /**
          * Most common case. Ignore all Join, Part, and Quit messages.
          */
        if (line.contains("JOIN :") || line.contains("PART :") || line.contains("QUIT :")) {
          Unit
        }

        /**
          * Ping case. Necessary to avoid disconnections.
          */
        else if (line.startsWith("PING ")) {
          println(s"<ping>: $line")
          // We must respond to PINGs to avoid being disconnected.
          writer.write(s"PONG ${line.substring(5)}\r\n")
          writer.flush()
        }

        /**
          * Nice case. Get the map Url using matchPattern.
          */
        else if (line.indexOf(privmsg) > -1) {
          val text = line.substring(line.indexOf(privmsg) + privmsg.length)
          val nick = line.substring(1, line.indexOf("!cho@ppy.sh"))

          // Get multiple beatmap URLs in a chat line.
          val maps: List[(Char, String)] =
          osuPattern.findAllMatchIn(text)
            .toList
            .map { bm_url =>
              println(s"m -- $bm_url")
              // id_type could be 'b' or 's'
              val id_type = bm_url.group(4).charAt(1)
              val id = bm_url.group(5)
              (id_type, id)
            }

          // MOD MY MAP PLZZZZZ
          maps.foreach {
            case (bm_type, bm_id) =>
              println(s" >>>>>>>>>>$maps - ${(nick, bm_type, bm_id)}")
              osuAPI.modRequetPlz(nick, bm_type, bm_id)
          }

        }

        /**
          * Case for join in #modreqs, after server connection was all ready.
          */
        else if (line.indexOf("376") >= 0) {
          writer.write(s"JOIN $channel\r\n")
          writer.write(s"AWAY :Hi, if I don't answer in a while and it is important, " +
            s"try send me a message in forum please. (PD: use #modreqs !!!)\r\n")
          writer.flush()
        }

        /**
          * Nothing... just to debug.
          */
        else {
          // Print the raw line received by the bot.
          println(s"<> => '$line'")
        }
      }

    //java.net.SocketException:
  }
}
