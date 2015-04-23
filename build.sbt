name := """play-quadro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

val activateVersion = "1.7"

resolvers ++= Seq(
  "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository",
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases",
  "fwbrasil.net" at "http://fwbrasil.net/maven/"
)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scalatestplus" %% "play" % "1.1.0" % "test",
  "net.fwbrasil" %% "activate-play" % activateVersion,
  "net.fwbrasil" %% "activate-jdbc" % activateVersion,
  "net.fwbrasil" %% "activate-slick" % activateVersion,
  "net.fwbrasil" %% "activate-core" % activateVersion,
  "net.fwbrasil" %% "activate-mongo" % activateVersion,
  "org.quartz-scheduler" % "quartz" % "2.2.1",
  "io.spray" % "spray-json_2.11" % "1.3.1",
  "net.databinder.dispatch" % "dispatch-core_2.11" % "0.11.2",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.9"
)

Keys.fork in Test := false