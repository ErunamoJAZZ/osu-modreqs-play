package model

import slick.driver.SQLiteDriver
import play.api.libs.json.{JsValue, Json}


object DriverDatabase extends SQLiteDriver {


  override val api = MyAPI

  object MyAPI extends API {

    type CharBoolean = Boolean

    /**
      * Links de donde saqué para implementar
      * http://slick.typesafe.com/doc/3.0.3/userdefined.html
      * http://stackoverflow.com/questions/29750861/convert-between-localdate-and-sql-date
      * http://stackoverflow.com/questions/31713204/how-to-implement-enums-in-scala-slick-3
      *
      * @return
      */
    implicit def date2localDate = MappedColumnType.base[java.time.LocalDate, java.sql.Date](
      localDate => java.sql.Date.valueOf(localDate),
      date => date.toLocalDate
    )

    /**
      * http://stackoverflow.com/questions/8992282/convert-localdate-to-localdatetime-or-java-sql-timestamp
      *
      * @return
      */
    implicit def timestamp2localDateTime = MappedColumnType.base[java.time.LocalDateTime, java.sql.Timestamp](
      localDateTime => java.sql.Timestamp.valueOf(localDateTime),
      timestamp => timestamp.toLocalDateTime
    )

    implicit def json2String = MappedColumnType.base[JsValue, String](
      json => json.toString,
      str => Json.parse(str)
    )
    implicit lazy val modeStaring_fmt = Json.format[ModeStaringJs]
    implicit def ModeStaringJs2String = MappedColumnType.base[Seq[ModeStaringJs], String](
      ms => Json.toJson(ms).toString,
      str => Json.parse(str).as[Seq[ModeStaringJs]]
    )
  }
}
