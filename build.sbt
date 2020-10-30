name := "fptp"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.postgresql" %  "postgresql"    % "42.2.18",
  "org.tpolecat"   %% "doobie-core"   % "0.9.0",
  "org.tpolecat"   %% "doobie-hikari" % "0.9.0",
  "co.fs2"         %% "fs2-io"        % "2.3.0",
  "org.scalatest"  %% "scalatest"     % "3.2.0" % Test
)

