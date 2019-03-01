package com.calvin.twitter.streamer

import cats.effect.Sync
import cats.implicits._
import com.calvin.twitter.streamer.models.Location

trait LocationHandler[F[_]] {
  def addOrIncLocation(location: Location): F[Unit]
  val printMap: F[String]
}

object LocationHandler {

  def apply[F[_]](implicit F: Sync[F]): LocationHandler[F] = new LocationHandler[F] {
    val dataMap: scala.collection.mutable.Map[Location, Int] = scala.collection.mutable.Map.empty[Location, Int]
    override def addOrIncLocation(location: Location): F[Unit] = {
      dataMap.get(location).fold(F.delay(dataMap.+=(location -> 1)))(k => F.delay(dataMap.+=(location -> (k + 1))))
    }.void

    override val printMap: F[String] = F.delay(s"${dataMap.keys} -> ${dataMap.values}")
  }



}