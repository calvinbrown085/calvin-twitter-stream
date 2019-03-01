package com.calvin.twitter.streamer

import cats.effect.Sync
import cats.implicits._
import com.calvin.twitter.streamer.models.{BasicTweet, Text}
import fs2.{Pipe, Stream}

trait HashtagHandler[F[_]] {
  def ingestHashtag: Pipe[F, BasicTweet, BasicTweet]
  def addOrIncHashtag(text: List[Text]): F[Unit]
  val printMap: F[String]
}

object HashtagHandler {

  def apply[F[_]](implicit F: Sync[F]): HashtagHandler[F] = new HashtagHandler[F] {
    val dataMap: scala.collection.mutable.Map[Text, Int] = scala.collection.mutable.Map.empty[Text, Int]

    private val hashTagRegex = """#\w*""".r
    private[this] def grabHashtags(text: Text): List[Text] =
      hashTagRegex.findAllIn(text.value).mkString.split("#").filterNot(m => m == "").toList.map(Text(_))

    override def ingestHashtag: Pipe[F, BasicTweet, BasicTweet] =
      _.flatMap(
        basicTweet =>
          Stream.eval(addOrIncHashtag(grabHashtags(basicTweet.text))) *> Stream
            .emit(basicTweet))

    override def addOrIncHashtag(text: List[Text]): F[Unit] = {
      text.traverse(
        text =>
          dataMap
            .get(text)
            .fold(F.delay(dataMap.+=(text -> 1)))(k => F.delay(dataMap.+=(text -> (k + 1)))))
    }.void

    override val printMap: F[String] = F.delay(s"${dataMap.keys} -> ${dataMap.values}")
  }
}
