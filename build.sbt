name := "Reactive-Scala"

version := "1.0"

scalaVersion := "2.11.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.5.6" % "test"

)
