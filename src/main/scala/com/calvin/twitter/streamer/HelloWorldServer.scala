package com.calvin.twitter.streamer

import cats.effect.{Effect, IO, IOApp}
import fs2.{Scheduler, Stream, StreamApp}
import io.prometheus.client.{CollectorRegistry, Counter}
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object HelloWorldServer extends IOApp {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def run(args: List[String]) = ServerStream.stream[IO]
}

object ServerStream {

  def helloWorldService[F[_]: Effect](
      locationHandler: LocationHandler[F],
      hashtagHandler: HashtagHandler[F],
      cr: CollectorRegistry) = new HelloWorldService[F].service(locationHandler, hashtagHandler, cr)

  var perMinuteAvg        = 0
  var perSecondAvg        = 0
  var totalRunningMinutes = 0

  def calcAveragePerMinute[F[_]](scheduler: Scheduler)(implicit F: Effect[F], ec: ExecutionContext) = {
    scheduler
      .awakeEvery(60.seconds)
      .evalMap(_ =>
        F.delay {
          totalRunningMinutes += 1
          perMinuteAvg = TWStream.tweetCount / ServerStream.totalRunningMinutes
          perSecondAvg = perMinuteAvg / 60
      })
  }
  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    for {
      cr <- Stream.eval(Effect[F].delay(new CollectorRegistry))
      counter <- Stream.eval(
        Effect[F].delay(
          Counter.build("internal_counter", "Counter for helper").labelNames("counter_name").register(cr)))
      config <- Stream.eval(Config.loadConfig[F])
      scheduler <- Scheduler[F](5)
      locationHandler = LocationHandler[F]
      hashtagHandler  = HashtagHandler[F]
      twitStream      = TWStream.stream[F](config, counter)
      server <- BlazeBuilder[F]
        .bindHttp(config.port, "0.0.0.0")
        .mountService(helloWorldService(locationHandler, hashtagHandler, cr), "/")
        .serve mergeHaltBoth calcAveragePerMinute[F](scheduler).drain merge DataIngest
        .ingest(twitStream, locationHandler, hashtagHandler)
        .drain
    } yield server
}
