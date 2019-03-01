package com.calvin.twitter.streamer

import cats.implicits._
import cats.effect.Effect
import fs2.{Pipe, Stream}
import com.calvin.twitter.streamer.models.BasicTweet

object DataIngest {

  def ingest[F[_]](
      dataStream: Stream[F, BasicTweet],
      locationHandler: LocationHandler[F],
      hashtagHandler: HashtagHandler[F])(implicit F: Effect[F]): Stream[F, BasicTweet] =
    dataStream
      .through(locationHandler.ingestLocation)
      .through(hashtagHandler.ingestHashtag)
}
