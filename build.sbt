val CirceV = "0.11.1"
val Http4sVersion = "0.20.9"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.calvin.twitter.streamer",
    name := "twitter-streaming-project",
    version := "1.0.0",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig" % "0.10.1",
      "com.spinoco"     %% "fs2-kafka"           % "0.2.0",
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.http4s"      %% "http4s-prometheus-metrics" % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceV,
      "io.circe"        %% "circe-generic-extras"       % CirceV,
      "io.circe"        %% "circe-java8"         % CirceV,
      "io.circe"        %% "circe-parser"        % CirceV,
      "org.specs2"     %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.3.1"),
    scalafmtOnCompile := true

  )

enablePlugins(JavaAppPackaging)
