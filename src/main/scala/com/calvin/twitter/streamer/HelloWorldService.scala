package com.calvin.twitter.streamer

import cats.effect.Effect
import com.calvin.twitter.streamer.models.Hashtags
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class HelloWorldService[F[_]: Effect] extends Http4sDsl[F] {

  final case class Response(totalTweets: Int, perMinuteAvg: Int, totalRunningMinutes: Int)
  object Response{
    import io.circe.generic.semiauto._
    implicit final val encoder: Encoder[Response] = deriveEncoder
    implicit final val decoder: Decoder[Response] = deriveDecoder
  }

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root => {
        Ok(Response(
          totalTweets = TWStream.tweetCount,
          perMinuteAvg = ServerStream.perMinuteAvg,
          totalRunningMinutes = ServerStream.totalRunningMinutes).asJson)
      }
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))
    }
  }
}
