name := """play-scala-slick-example-part2"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

//libraryDependencies += guice
libraryDependencies ++= Seq(jdbc)
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0"
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.3"
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.3"

libraryDependencies += "com.h2database" % "h2" % "1.4.199"

libraryDependencies += specs2 % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings",
  "-language:higherKinds"
)
