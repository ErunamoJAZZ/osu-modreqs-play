package services

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.time.LocalDateTime

import model.ModRequest
import play.api.Configuration

/**
  * Created by erunamo on 8/10/16.
  */
class ChoListener(osuAPI: OsuAPI, configuration: Configuration) {

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

  // Log on to the server.
  writer.write("PASS " + pass + "\r\n")
  writer.write("NICK " + nick + "\r\n")
  writer.write("USER " + login + " 8 * : Java IRC Testing\r\n")
  writer.flush()

  //Read lines in a stream!
  Stream.continually(reader.readLine)
    .takeWhile(_ != null)
    .foreach { line =>

      /**
        * Caso más común. Simplemente ignora esos  mensajes.
        */
      if (line.contains("JOIN :") || line.contains("PART :") || line.contains("QUIT :")) {
        Unit
      }

      /**
        * Caso del PING. Se requiere para que el servidor no lo desconecte.
        */
      else if (line.startsWith("PING ")) {
        println(s"<ping>: $line")
        // We must respond to PINGs to avoid being disconnected.
        writer.write(s"PONG ${line.substring(5)}\r\n")
        //writer.write("PRIVMSG " + channel + " :I got pinged!\r\n")
        writer.flush()
      }

      /**
        * Caso chévere.
        * Acá se rescata las url y quién lo hizo.
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
            val id_type = bm_url.group(4).charAt(1) // 'b' or 's'
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
        * Cuando se acaba de conectar, que lo meta dentro del canal
        */
      else if (line.indexOf("376") >= 0) {
        writer.write("JOIN " + channel + "\r\n")
        writer.flush()
      }

      /**
        * Nada...
        */
      else {
        // Print the raw line received by the bot.
        println(s"<> => '$line'")

      }
    }
}
