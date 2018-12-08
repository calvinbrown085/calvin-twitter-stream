package com.calvin.twitter.streamer

import cats.effect.{Effect, IO}
import fs2.{Scheduler, Stream, StreamApp}
import io.prometheus.client.{CollectorRegistry, Counter}
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object HelloWorldServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {

  def helloWorldService[F[_]: Effect](cr: CollectorRegistry) = new HelloWorldService[F].service(cr)

  var perMinuteAvg = 0
  var totalRunningMinutes = 0

  def calcAveragePerMinute[F[_]](scheduler: Scheduler)(implicit F: Effect[F], ec: ExecutionContext) = {
    scheduler.awakeEvery(60.seconds).evalMap(_ => F.delay{
      totalRunningMinutes += 1
      perMinuteAvg = TWStream.tweetCount / ServerStream.totalRunningMinutes
    })
  }

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    for {
      cr <- Stream.eval(Effect[F].delay(new CollectorRegistry))
      counter <- Stream.eval(Effect[F].delay(Counter.build("internal_counter", "Counter for helper").labelNames("counter_name").register(cr)))
      config <- Stream.eval(Config.loadConfig[F])
      scheduler <- Scheduler[F](5)
      server <- BlazeBuilder[F]
          .bindHttp(config.port, "0.0.0.0")
          .mountService(helloWorldService(cr), "/")
          .serve mergeHaltBoth TWStream.stream[F](config, counter).drain merge calcAveragePerMinute[F](scheduler).drain
    } yield server
}
