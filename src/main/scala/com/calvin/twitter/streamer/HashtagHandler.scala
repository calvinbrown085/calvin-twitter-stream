package com.calvin.twitter.streamer

import cats.effect.Sync
import cats.implicits._
import com.calvin.twitter.streamer.models.{BasicTweet, Hashtag, HashtagText}
import fs2.{Pipe, Stream}

trait HashtagHandler[F[_]] {
  def ingestHashtag: Pipe[F, BasicTweet, BasicTweet]
  def addOrIncHashtag(hashTags: List[Hashtag]): F[Unit]
  val printMap: F[String]
}

object HashtagHandler {

  def apply[F[_]](implicit F: Sync[F]): HashtagHandler[F] = new HashtagHandler[F] {
    val dataMap: scala.collection.mutable.Map[HashtagText, Int] = scala.collection.mutable.Map.empty[HashtagText, Int]

    override def ingestHashtag: Pipe[F, BasicTweet, BasicTweet] =
      _.flatMap(
        basicTweet =>
          Stream.eval(addOrIncHashtag(basicTweet.entities.hashtags)) *> Stream
            .eval(F.whenA(basicTweet.text.contains("pic.twitter.com"))(F.delay(println(basicTweet.text)))) *> Stream
            .emit(basicTweet))

    override def addOrIncHashtag(hashTags: List[Hashtag]): F[Unit] = {
      hashTags.traverse(
        hashTag =>
          dataMap
            .get(hashTag.text)
            .fold(F.delay(dataMap.+=(hashTag.text -> 1)))(k => F.delay(dataMap.+=(hashTag.text -> (k + 1)))))
    }.void

    override val printMap: F[String] = F.delay(s"${dataMap.keys} -> ${dataMap.values}")
  }
}
