package services

import model.{BeatmapsDAO, ModRequestsDAO}

trait ServicesModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val greetingService = wire[GreetingService]


  def beatmapsDAO: BeatmapsDAO
  def modRequest: ModRequestsDAO

}
