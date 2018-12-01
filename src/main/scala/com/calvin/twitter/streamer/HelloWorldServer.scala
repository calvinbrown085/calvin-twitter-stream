package com.calvin.twitter.streamer

import cats.effect.{Effect, IO}
import fs2.{Stream, Scheduler, StreamApp}
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {

  def helloWorldService[F[_]: Effect] = new HelloWorldService[F].service

  var perMinuteAvg = 0
  var totalRunningMinutes = 1

  def calcAveragePerMinute[F[_]](scheduler: Scheduler)(implicit F: Effect[F], ec: ExecutionContext) = {
    scheduler.awakeEvery(60.seconds).evalMap(_ => F.delay{
      perMinuteAvg = TWStream.tweetCount / ServerStream.totalRunningMinutes
      totalRunningMinutes += 1
    })
  }

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    for {
      config <- Stream.eval(Config.loadConfig[F])
      scheduler <- Scheduler[F](5)
      server <- BlazeBuilder[F]
          .bindHttp(config.port, "0.0.0.0")
          .mountService(helloWorldService, "/")
          .serve mergeHaltBoth TWStream.stream[F](config).drain merge calcAveragePerMinute[F](scheduler).drain
    } yield server
}
