package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

case class Location(value: String) extends AnyVal
object Location {
  import io.circe.generic.extras.semiauto._
  implicit final val encoder: Encoder[Location] = deriveUnwrappedEncoder
  implicit final val decoder: Decoder[Location] = deriveUnwrappedDecoder
}
