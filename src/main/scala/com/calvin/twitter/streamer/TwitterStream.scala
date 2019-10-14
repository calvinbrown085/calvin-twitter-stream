package com.calvin.twitter.streamer

import java.util.concurrent.atomic.AtomicInteger

import org.http4s._
import org.http4s.client.blaze._
import org.http4s.client.oauth1
import cats.effect._
import cats.implicits._
import fs2.Stream
import jawnfs2._
import io.circe.Json
import io.prometheus.client.Counter

object TWStream {

  import models._

  var tweetCount = 0

  // jawn-fs2 needs to know what JSON AST you want
  implicit val f = io.circe.jawn.CirceSupportParser.facade

  /* These values are created by a Twitter developer web app.
   * OAuth signing is an effect due to generating a nonce for each `Request`.
   */
  def sign[F[_]: Effect](consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)(
      req: Request[F]): F[Request[F]] = {
    val consumer = oauth1.Consumer(consumerKey, consumerSecret)
    val token    = oauth1.Token(accessToken, accessSecret)
    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
  }
  /* Create a http client, sign the incoming `Request[F]`, stream the `Response[IO]`, and
   * `parseJsonStream` the `Response[F]`.
   * `sign` returns a `F`, so we need to `Stream.eval` it to use a for-comprehension.
   */
  def jsonStream[F[_]: Effect](
      counter: Counter,
      consumerKey: String,
      consumerSecret: String,
      accessToken: String,
      accessSecret: String): Stream[F, Json] =
    for {
      client <- Http1Client.stream[F]()
      sr <- Stream.eval(
        sign(consumerKey, consumerSecret, accessToken, accessSecret)(
          Request[F](Method.GET, Uri.uri("https://stream.twitter.com/1.1/statuses/sample.json"))))
      res <- client.streaming(sr)(resp => resp.body.chunks.parseJsonStream)
    } yield res

  def basicTweetStream[F[_]: Effect](j: Json, counter: Counter): Stream[F, BasicTweet] =
    Stream.eval[F, Unit](Effect[F].delay { counter.labels("tweet_count").inc(); tweetCount += 1 }) >>
      j.as[BasicTweet].fold(_ => Stream.empty, tweet => Stream.emit(tweet))

  def stream[F[_]: Effect](authConfig: Config.Auth, counter: Counter): Stream[F, BasicTweet] = {
    jsonStream(
      counter,
      authConfig.consumerKey,
      authConfig.consumerSecret,
      authConfig.accessToken,
      authConfig.accessSecret).flatMap(j => basicTweetStream(j, counter))
  }

  def basicJsonStream[F[_]: Effect](authConfig: Config.Auth, counter: Counter): Stream[F, Json] = {
    jsonStream(
      counter,
      authConfig.consumerKey,
      authConfig.consumerSecret,
      authConfig.accessToken,
      authConfig.accessSecret)
  }
}
