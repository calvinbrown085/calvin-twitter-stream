package com.calvin.twitter.streamer

import cats.effect.Effect
import pureconfig.generic.auto._
object Config {

  final case class Auth(
      accessSecret: String,
      accessToken: String,
      consumerKey: String,
      consumerSecret: String,
      port: Int)

  def loadConfig[F[_]](implicit F: Effect[F]): F[Auth] = F.delay {
    val accessSecret = scala.util.Properties.envOrElse("ACCESS_SECRET", "fail!")
    val accessToken =
      scala.util.Properties.envOrElse("ACCESS_TOKEN", "fail")
    val consumerKey = scala.util.Properties.envOrElse("CONSUMER_KEY", "fail")
    val consumerSecret =
      scala.util.Properties.envOrElse("CONSUMER_SECRET", "fail")
    val port = scala.util.Properties.envOrElse("PORT", "8080")
    Auth(
      accessSecret   = accessSecret,
      accessToken    = accessToken,
      consumerKey    = consumerKey,
      consumerSecret = consumerSecret,
      port           = port.toInt)
  }
}
