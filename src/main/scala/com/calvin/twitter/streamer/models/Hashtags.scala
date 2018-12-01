package com.calvin.twitter.streamer.models


import io.circe.{Decoder, Encoder}

case class Hashtags(indicies: List[Int], text: String)
object Hashtags{
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[Hashtags] = deriveEncoder
  implicit final val decoder: Decoder[Hashtags] = deriveDecoder
}
