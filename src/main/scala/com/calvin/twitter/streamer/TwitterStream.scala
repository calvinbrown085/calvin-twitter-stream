package com.calvin.twitter.streamer

import java.util.concurrent.atomic.AtomicInteger

import org.http4s._
import org.http4s.client.blaze._
import org.http4s.client.oauth1
import cats.effect._
import fs2.Stream
import fs2.io.stdout
import fs2.text.{lines, utf8Encode}
import jawnfs2._
import io.circe.Json

object TWStream {

  import models._

  var tweetCount = 0

  // jawn-fs2 needs to know what JSON AST you want
  implicit val f = io.circe.jawn.CirceSupportParser.facade

  /* These values are created by a Twitter developer web app.
   * OAuth signing is an effect due to generating a nonce for each `Request`.
   */
  def sign[F[_]: Effect](consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
      (req: Request[F]): F[Request[F]] = {
    val consumer = oauth1.Consumer(consumerKey, consumerSecret)
    val token    = oauth1.Token(accessToken, accessSecret)
    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
  }
  /* Create a http client, sign the incoming `Request[F]`, stream the `Response[IO]`, and
   * `parseJsonStream` the `Response[F]`.
   * `sign` returns a `F`, so we need to `Stream.eval` it to use a for-comprehension.
   */
  def jsonStream[F[_]: Effect](consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
      (req: Request[F]): Stream[F, BasicTweet] =
    for {
      client <- Http1Client.stream[F]()
      sr  <- Stream.eval(sign(consumerKey, consumerSecret, accessToken, accessSecret)(req))
      res <- client.streaming(sr)(resp => basicTweetStream(resp.body.chunks.parseJsonStream))
    } yield res

  def basicTweetStream[F[_]: Effect](s: Stream[F, Json]): Stream[F, BasicTweet] = {
    s.flatMap(j =>
      Stream.eval[F, Unit](Effect[F].delay(tweetCount += 1)) >>
          j.as[BasicTweet].fold(_ => Stream.empty, tweet => Stream.emit(tweet)))
  }

  /* Stream the sample statuses.
   * Plug in your four Twitter API values here.
   * We map over the Circe `Json` objects to pretty-print them with `spaces2`.
   * Then we `to` them to fs2's `lines` and then to `stdout` `Sink` to print them.
   */
  def stream[F[_]: Effect]: Stream[F, Unit] = {
    val req = Request[F](Method.GET, Uri.uri("https://stream.twitter.com/1.1/statuses/sample.json"))
    val s   = jsonStream("", "", "", "")(req)
    s.map(_.toString).through(lines).through(utf8Encode).to(stdout)
  }
}
