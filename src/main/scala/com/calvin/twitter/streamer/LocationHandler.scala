package com.calvin.twitter.streamer

import cats.effect.Sync
import cats.implicits._
import com.amazonaws.regions.Regions
import com.calvin.twitter.streamer.models.{BasicTweet, Location}
import fs2.{Pipe, Stream}

trait LocationHandler[F[_]] {
  def ingestLocation: Pipe[F, BasicTweet, BasicTweet]
  def addOrIncLocation(location: Location): F[Unit]
  def getTop5Locations: F[Vector[(Location, Int)]]
  val printMap: F[String]
}

object LocationHandler {

  def apply[F[_]](implicit F: Sync[F]): LocationHandler[F] = new LocationHandler[F] {

    val dataMap: scala.collection.mutable.Map[Location, Int] = scala.collection.mutable.Map.empty[Location, Int]

    override def ingestLocation: Pipe[F, BasicTweet, BasicTweet] =
      _.flatMap(basicTweet => Stream.eval(addOrIncLocation(basicTweet.user.location)) *> Stream.emit(basicTweet))

    override def addOrIncLocation(location: Location): F[Unit] = {
      dataMap.get(location).fold(F.delay(dataMap.+=(location -> 1)))(k => F.delay(dataMap.+=(location -> (k + 1))))
    }.void

    def getTop5Locations: F[Vector[(Location, Int)]] = F.delay {
      dataMap.toVector.sortWith(_._2 > _._2).take(5)
    }

    override val printMap: F[String] = F.delay(s"${dataMap.keys} -> ${dataMap.values}")
  }

}
