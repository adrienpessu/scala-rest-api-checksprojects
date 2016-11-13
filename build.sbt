name := """play-reactive-mongo-db"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

//routesGenerator := InjectedRoutesGenerator //I bet it's useless since play 2.5

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.12"
)

libraryDependencies ++= Seq(
  "com.auth0" % "java-jwt" % "2.2.1"
  // other dependencies separated by commas
)

fork in run := false