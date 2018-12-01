package com.calvin.twitter.streamer.models


import io.circe.{Decoder, Encoder}

case class Entities(hashtags: List[Hashtags], urls: List[Urls], user_mentions: List[UserMentions], symbols: List[Symbols])
object Entities{
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[Entities] = deriveEncoder
  implicit final val decoder: Decoder[Entities] = deriveDecoder
}
