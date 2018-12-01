package com.calvin.twitter.streamer.models


import io.circe.{Decoder, Encoder}

case class Symbols(text: String)
object Symbols{
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[Symbols] = deriveEncoder
  implicit final val decoder: Decoder[Symbols] = deriveDecoder
}
