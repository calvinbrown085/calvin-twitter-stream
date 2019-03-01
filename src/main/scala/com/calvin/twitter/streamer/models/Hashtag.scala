package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

final case class HashtagText(value: String) extends AnyVal
object HashtagText {
  import io.circe.generic.extras.semiauto._
  implicit final val e: Encoder[HashtagText] = deriveUnwrappedEncoder
  implicit final val d: Decoder[HashtagText] = deriveUnwrappedDecoder
}
case class Hashtag(indicies: List[Int], text: HashtagText)
object Hashtag {
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[Hashtag] = deriveEncoder
  implicit final val decoder: Decoder[Hashtag] = deriveDecoder
}
