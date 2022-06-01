package com.example.scalahttp4sdemo.controller

import cats.effect._
import cats.implicits._
import com.example.scalahttp4sdemo.service.{Bill, BillService}
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import scala.util.Try


object PaymentRoutes {

  def paymentRoutes[F[_] : Concurrent](billService: BillService): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val directorDecoder: EntityDecoder[F, Bill] = jsonOf[F, Bill]
    HttpRoutes.of[F] {
      case req@POST -> Root / "directors" =>
        for {
          bill <- req.as[Bill]
          res <- Try(billService.updateStatusOfSpecificBill(bill))
            .toEither.fold(
            l => BadRequest(l.getMessage),
            r => Ok(r.asJson)
          )

        } yield res
    }
  }

}
