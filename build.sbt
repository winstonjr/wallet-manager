name := """wallet-manager"""
organization := "io.atleastonce"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name,
      version,
      scalaVersion,
      "versionFile" -> scala.io.Source.fromFile("./version.sbt").mkString),
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoPackage := "io.atleastonce.wallet.manager"
  )

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test,
  "org.xerial" % "sqlite-jdbc" % "3.18.0",
  "io.getquill" %% "quill-jdbc" % "1.3.0",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "com.propensive" %% "rapture" % "2.0.0-M8",
  "com.propensive" % "rapture-json-json4s_sjs0.6_2.12" % "2.0.0-M8",
  "com.propensive" % "rapture-json-jackson_2.12" % "2.0.0-M8"
)
//libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "io.atleastonce.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "io.atleastonce.binders._"
