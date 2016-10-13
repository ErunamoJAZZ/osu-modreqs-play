name := """modreqs-play-macwire"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.softwaremill.macwire" %% "macros" % "1.0.7",
  "com.softwaremill.macwire" %% "runtime" % "1.0.7",
  "org.xerial" % "sqlite-jdbc" % "3.14.2.1",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.play" %% "play-slick" % "2.0.0"
)
