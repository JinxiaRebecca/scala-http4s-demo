package com.example.scalahttp4sdemo.controller

import cats.effect.Sync
import com.example.scalahttp4sdemo.service.UsageService
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import java.time.LocalDate

object UsageController {
  def UsageRoutes[F[_] : Sync](usageService: UsageService): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "reserved-usage" / IntVar(customerId) =>
        usageService.calculateRequiredBillPeriodReservedUsagesForCustomer(customerId, LocalDate.now())
        .fold(
          _ => BadRequest(s"subscribed package not found"),
          right => Ok(right)
        )

    }
  }

}
