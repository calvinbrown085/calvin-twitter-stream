package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

case class BasicTweet(id_str: String, text: Text, user: User, entities: Entities)
object BasicTweet {
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[BasicTweet] = deriveEncoder
  implicit final val decoder: Decoder[BasicTweet] = deriveDecoder
}
