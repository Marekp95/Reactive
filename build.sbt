name := "Reactive-Scala"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.2",
  "org.springframework" % "spring-webmvc" % "5.0.2.RELEASE",
  "org.springframework.boot" % "spring-boot" % "1.5.4.RELEASE",
  "org.springframework.boot" % "spring-boot-autoconfigure" % "1.5.4.RELEASE",
  "org.springframework.boot" % "spring-boot-starter-web" % "1.5.4.RELEASE",
  "org.springframework.boot" % "spring-boot-configuration-processor" % "1.5.4.RELEASE",
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.8",
  "com.typesafe.akka" %% "akka-remote" % "2.5.8",
  "com.typesafe.akka" % "akka-http_2.12" % "10.0.10",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.akka" % "akka-testkit_2.12" % "2.5.8" % "test",
  "com.typesafe.akka" %% "akka-persistence-tck" % "2.5.8" % "test"
)

mainClass in Compile := Some("Application")