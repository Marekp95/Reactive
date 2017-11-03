name := "Reactive-Scala"

version := "1.0"

scalaVersion := "2.11.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.5.6" % "test",
  "com.typesafe.akka" %% "akka-persistence-tck" % "2.5.6" % "test"
)
