package com.calvin.twitter.streamer

import cats.effect.Effect
import pureconfig.generic.auto._


object Config {

  final case class Auth(accessSecret: String, accessToken: String, consumerKey: String, consumerSecret: String, port: Int)

  def loadConfig[F[_]](implicit F: Effect[F]): F[Auth] = {
    pureconfig.loadConfig[Auth].fold[F[Auth]](_ => F.raiseError(new Throwable("Can't load config!")), a => F.pure(a))
  }
}
