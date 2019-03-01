package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

case class Urls(display_url: String, expanded_url: String, indicies: List[Int], url: String)
object Urls {
  import io.circe.generic.semiauto._
  implicit final val encoder: Encoder[Urls] = deriveEncoder
  implicit final val decoder: Decoder[Urls] = deriveDecoder
}
