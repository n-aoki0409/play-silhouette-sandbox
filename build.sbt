name := """play-silhouette-sandbox"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.6"
libraryDependencies += "com.iheart" %% "ficus" % "1.4.3"

// silhouette
val silhouetteVersion = "6.1.1"
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion,
  "com.mohiva" %% "play-silhouette-totp" % silhouetteVersion
)

// play-slick
val mysqlVersion = "8.0.19"
val playSlickVersion = "5.0.0"
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % mysqlVersion,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
