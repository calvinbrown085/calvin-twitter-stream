package com.calvin.twitter.streamer
import java.util.UUID

import cats.effect.Effect
import cats.implicits._
import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.regions.Regions
import com.calvin.twitter.streamer.models.BasicTweet
import fs2.Pipe
import io.circe.Json
import jp.co.bizreach.kinesis._

trait KinesisProducer[F[_]] {}

object KinesisProducer {

  private[this] val creds: AWSCredentialsProvider = new AWSCredentialsProvider {
    override def getCredentials: AWSCredentials = new AWSCredentials {
      override def getAWSAccessKeyId: String = "" //replace me

      override def getAWSSecretKey: String = "" //replace me
    }
    override def refresh(): Unit = ()
  }
  implicit val region: Regions = Regions.US_EAST_1
  val client                   = AmazonKinesis(creds)

  def putRecordsIntoKinesis[F[_]](implicit F: Effect[F]): Pipe[F, Json, Unit] = { s =>
    s.evalMap { json =>
      val request = PutRecordRequest(
        streamName   = "twitter-stream",
        partitionKey = UUID.randomUUID().toString,
        data         = json.toString().getBytes("UTF-8")
      )
      F.fromEither(client.putRecord(request)).void
    }
  }

}
