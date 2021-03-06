val CirceV = "0.9.3"
val Http4sVersion = "0.18.21"
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
      "org.http4s"      %% "http4s-prometheus-server-metrics" % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceV,
      "io.circe"        %% "circe-generic-extras"       % CirceV,
      "io.circe"        %% "circe-java8"         % CirceV,
      "io.circe"        %% "circe-parser"        % CirceV,
      "jp.co.bizreach"  %% "aws-kinesis-scala"   % "0.0.12",
      "org.specs2"     %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4"),
    scalafmtOnCompile := true

  )

enablePlugins(JavaAppPackaging)
