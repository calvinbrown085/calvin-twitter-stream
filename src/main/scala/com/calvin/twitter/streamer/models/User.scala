package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

case class User(id: Int, name: String, screen_name: String, location: Location, url: String, description: String)
object User {
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[User] = deriveEncoder
  implicit final val decoder: Decoder[User] = deriveDecoder
}
