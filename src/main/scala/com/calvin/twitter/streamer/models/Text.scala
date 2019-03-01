package com.calvin.twitter.streamer.models

import io.circe.{Decoder, Encoder}

final case class Text(value: String) extends AnyVal
object Text {
  import io.circe.generic.extras.semiauto._
  implicit final val e: Encoder[Text] = deriveUnwrappedEncoder
  implicit final val d: Decoder[Text] = deriveUnwrappedDecoder
}
