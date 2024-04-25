name := """event_manager"""
organization := "com.edu.utrgv"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.13"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.36.0.1"
libraryDependencies += jdbc

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.edu.utrgv.controllers._"

//libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
//libraryDependencies += "com.typesafe.play" %% "play-sql" % "2.8.11"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.edu.utrgv.binders._"
