val Http4sVersion = "0.21.11"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.5"
val LogbackVersion = "1.2.3"
val pmmlVersion = "1.5.5"
val doobieVersion = "0.9.4"
val postgresVersion = "42.2.18"
val xmlVersion = "2.3.2"

organization := "edu.fiuba"

name := "fp-fiuba-4-3"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.jpmml" % "pmml-evaluator" % pmmlVersion,
  "org.postgresql" % "postgresql" % postgresVersion,
  "ch.qos.logback" % "logback-classic" % LogbackVersion,
  "jakarta.xml.bind" % "jakarta.xml.bind-api" % xmlVersion,
  "org.glassfish.jaxb" % "jaxb-runtime" % xmlVersion,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "org.specs2" %% "specs2-core" % Specs2Version % Test,
  "io.circe" %% "circe-literal" % CirceVersion % Test
)

libraryDependencies += "org.http4s" %% "http4s-json4s-native" % Http4sVersion
libraryDependencies += "org.http4s" %% "http4s-json4s-jackson" % Http4sVersion
