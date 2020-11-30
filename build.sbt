name := "TP-Algo-4-2"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.18",
  "org.jpmml" % "jpmml-sparkml" % "1.6.1",
  "org.tpolecat" %% "doobie-core" % "0.9.0",
  "org.tpolecat" %% "doobie-hikari" % "0.9.0",
  "co.fs2" %% "fs2-io" % "2.3.0",
  "org.apache.spark" %% "spark-mllib" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.2.0" % Test
)
