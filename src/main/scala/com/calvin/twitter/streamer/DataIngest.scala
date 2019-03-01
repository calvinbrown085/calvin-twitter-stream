package com.calvin.twitter.streamer

import cats.implicits._
import cats.effect.Effect
import fs2.Stream
import com.calvin.twitter.streamer.models.BasicTweet


object DataIngest {

  def ingest[F[_]](dataStream: Stream[F, BasicTweet], locationHandler: LocationHandler[F])(implicit F: Effect[F]): Stream[F, BasicTweet] = {
    dataStream.flatMap(b => Stream.eval(locationHandler.addOrIncLocation(b.user.location)) *> Stream.emit(b))
  }
}