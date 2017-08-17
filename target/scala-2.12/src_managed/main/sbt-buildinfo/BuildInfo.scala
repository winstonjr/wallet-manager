package io.atleastonce.wallet.manager

import scala.Predef._

/** This object was generated by sbt-buildinfo. */
case object BuildInfo {
  /** The value is "wallet-manager". */
  val name: String = "wallet-manager"
  /** The value is "1.0.0.0". */
  val version: String = "1.0.0.0"
  /** The value is "2.12.2". */
  val scalaVersion: String = "2.12.2"
  /** The value is "version := \"1.0.0.0\"\n". */
  val versionFile: String = "version := \"1.0.0.0\"\n"
  /** The value is "2017-08-15 22:04:05.204". */
  val builtAtString: String = "2017-08-15 22:04:05.204"
  /** The value is 1502834645204L. */
  val builtAtMillis: scala.Long = 1502834645204L
  override val toString: String = {
    "name: %s, version: %s, scalaVersion: %s, versionFile: %s, builtAtString: %s, builtAtMillis: %s" format (
      name, version, scalaVersion, versionFile, builtAtString, builtAtMillis
    )
  }
  val toMap: Map[String, Any] = Map[String, Any](
    "name" -> name,
    "version" -> version,
    "scalaVersion" -> scalaVersion,
    "versionFile" -> versionFile,
    "builtAtString" -> builtAtString,
    "builtAtMillis" -> builtAtMillis)

  val toJson: String = toMap.map(i => "\"" + i._1 + "\":\"" + i._2 + "\"").mkString("{", ", ", "}")
}
