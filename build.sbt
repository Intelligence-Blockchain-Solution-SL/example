organization := "es.ibs"

name := "example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-explaintypes", "-encoding", "utf-8",
  "-Xlint", "-Xfatal-warnings", "-Xlint:-unused")

libraryDependencies ++= Seq(
  "es.ibs"                        %% "thesis"                   % "1.0-SNAPSHOT"
)