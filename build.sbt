name := """play-hibernate"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, JavaAppPackaging/*, DockerPlugin*/)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  // If you enable PlayEbean plugin you must remove these
  // JPA dependencies to avoid conflicts.
  javaJpa,
  "org.hibernate" % "hibernate-core" % "4.3.10.Final",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.10.Final",
  "mysql" % "mysql-connector-java" % "5.1.36",
  cache,
  javaWs,
  evolutions,
  "org.assertj" % "assertj-core" % "3.1.0" % "test",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.projectlombok" % "lombok" % "1.16.8",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "org.mockito" % "mockito-core" % "1.10.19",
  "org.jsoup" % "jsoup" % "1.9.2",
  "org.hibernate" % "hibernate-search-orm" % "5.3.0.Final",
  //"org.apache.lucene" % "lucene-core" % "5.3.1",
  "dom4j" % "dom4j" % "1.6"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-a")

//fork in run := true

//fork in run := true

fork in run := true