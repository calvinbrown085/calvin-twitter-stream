package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

case class UserMentions(id_str: String, name: String, screen_name: String)
object UserMentions {
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[UserMentions] = deriveEncoder
  implicit final val decoder: Decoder[UserMentions] = deriveDecoder
}
