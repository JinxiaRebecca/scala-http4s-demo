package com.example.scalahttp4sdemo
import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait Usage[F[_]] {
  def fetchUsagePerCustomerOfCurrentBillPeriod(n: Usage.CustomerId): F[Usage.Query]
}

object Usage {
  implicit def apply[F[_]](implicit ev: Usage[F]): Usage[F] = ev

  final case class CustomerId(CustomerId: String) extends AnyVal

  final case class Query(greeting: String) extends AnyVal
  object Query {
    implicit val greetingEncoder: Encoder[Query] = new Encoder[Query] {
      final def apply(a: Query): Json = Json.obj(
        ("usage", Json.fromString(a.greeting)),
      )
    }
    implicit def greetingEntityEncoder[F[_]]: EntityEncoder[F, Query] =
      jsonEncoderOf[F, Query]
  }

  def impl[F[_]: Applicative]: Usage[F] = new Usage[F]{
    def fetchUsagePerCustomerOfCurrentBillPeriod(n: Usage.CustomerId): F[Query] =
      Query("customerId " + n.CustomerId).pure[F]
  }

}

