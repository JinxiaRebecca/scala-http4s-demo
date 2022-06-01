package com.example.scalahttp4sdemo.controller

import cats.effect.Sync
import cats.implicits._
import com.example.scalahttp4sdemo.service.{Bill, BillService, CustomerService}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.{HttpRoutes, ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.impl.OptionalValidatingQueryParamDecoderMatcher
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object BillController {

  implicit val requiredDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder[String].emap { date =>
      Try(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .toEither
        .leftMap { e =>
          ParseFailure(e.getMessage, e.getMessage)
        }
    }

  object DateQueryParamMather extends OptionalValidatingQueryParamDecoderMatcher[LocalDate]("queryDate")

  def BillRoutes[F[_] : Sync](customerService: CustomerService, billService: BillService): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "all-bills" =>
        customerService.fetchAllCustomers().map(customer =>
          billService.calculateQueriedBillForSpecificCustomer(customer, LocalDate.now())
        ) match {
          case bills: List[Bill] => Ok(bills)
          case _ => NotFound("unknown error happened")
        }
      case GET -> Root / "current-bill" / IntVar(customerId) =>
        val customer = customerService.fetchCustomerByCustomerId(customerId)
        val bill = billService.calculateQueriedBillForSpecificCustomer(customer, LocalDate.now())
        Ok(bill)
      case GET -> Root / "any-bill" / IntVar(customerId) :? DateQueryParamMather(queryDate) =>
        queryDate match {
          case Some(validatedDate) =>
            validatedDate.fold(
              _ => BadRequest("queryDate is badly formatted"),
              date => {
                Try(customerService.fetchCustomerByCustomerId (customerId))
                  .toEither
                  .fold(
                    _ => BadRequest(s"customer $customerId does not exist"),
                    customer => billService.calculateQueriedBillForSpecificCustomer(customer, date) match {
                      case bill: Bill => Ok(bill)
                      case _ => BadRequest(s"you have not subscribed any packages before $date")
                    }
                  )
              }
            )
          case _ => BadRequest("queryDate is not presented")
        }



    }
  }

}
