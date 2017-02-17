lazy val commonSettings = Seq (
  scalaVersion := "2.12.1"
)

lazy val fpInScala = (project in file(".")).settings(commonSettings: _*)

initialCommands in console := """
  |import Turtle._
""".stripMargin

scalacOptions ++= Seq (
  "-feature",
  "-deprecation",
  "-target:jvm-1.8"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.8",
  "co.fs2" %% "fs2-core" % "0.9.2",
  "co.fs2" %% "fs2-io" % "0.9.2"
)

logLevel := Level.Info
