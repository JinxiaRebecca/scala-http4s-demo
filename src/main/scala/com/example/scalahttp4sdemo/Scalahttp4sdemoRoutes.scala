package com.example.scalahttp4sdemo

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.{HttpRoutes, ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{QueryParamDecoderMatcher, ValidatingQueryParamDecoderMatcher}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder


object Scalahttp4sdemoRoutes {
  case class UsageResponse(
                            var phoneUseLeft: Int,
                            var smsUseLeft: Int,
                          )

  final case class BillResponse(
                               customerId: Int,
                               customerName: String,
                               packageName: String,
                               phoneUse: Int,
                               smsUse: Int,
                               consumptionCost: BigDecimal,
                               billStartDate: LocalDate
                               )
  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
  implicit val consumptionDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder[String].emap{ date =>
      Try(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .toEither
        .leftMap { e =>
          ParseFailure(e.getMessage, e.getMessage)
        }
    }

  object CustomerIdQueryParamMather extends QueryParamDecoderMatcher[Int]("customerId")
  object ConsumptionDateQueryParamMather extends ValidatingQueryParamDecoderMatcher[LocalDate]("consumptionDate")

  val now: LocalDate = LocalDate.now()




//  def calculateAllBillsPerCustomer(customer: Customer): BillResponse = {
//    val usages = calculateUsagesPerCustomerOfCurrentBillPeriod(customer)
//    val packages = fetchPackageOfCustomer(customer)
//    val phoneUseLeft = usages.map(_.phoneUse).sum - packages.phoneLimitation
//    val smsUseLeft = usages.map(_.smsUse).sum - packages.smsLimitation
//    val exPhoneUseFee: BigDecimal = if (phoneUseLeft > 0)  packages.exPhoneFee * phoneUseLeft else 0
//    val exSmsUseFee: BigDecimal = if (smsUseLeft > 0) packages.exSmsFee * smsUseLeft else 0
//    val consumptionCost = packages.subscriptionFee + exPhoneUseFee + exSmsUseFee
//    val now = LocalDate.now()
//    val billStartDate = LocalDate.of(now.getYear, now.getMonth, customer.billDate.getDayOfMonth)
//    BillResponse(customer.id, customer.name, packages.name, usages.map(_.phoneUse).sum, usages.map(_.smsUse).sum, consumptionCost, billStartDate)
//  }


  def UsageRoutes[F[_]: Sync](U: UsageService[F], C: CustomerService[F], P: PackageService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "reserved-usage" :? CustomerIdQueryParamMather(customerId) =>
        for {
          customer <- C.fetchCustomerByCustomerId(customerId)
          phoneUse <- U.calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(customer)
          smsUse <- U.calculateSmsUsagesPerCustomerOfCurrentBillPeriod(customer)
          phoneLimitation <- P.fetchPackageOfCustomer(customer).map(_.phoneLimitation)
          smsLimitation <- P.fetchPackageOfCustomer(customer).map(_.smsLimitation)
          resp <- Ok(UsageResponse(phoneLimitation - phoneUse,  smsLimitation - smsUse))
        } yield resp
    }
  }

  def BillRoutes[F[_] :Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "all-bills" => ???
//        val allBills = customers.map(
//          calculateAllBillsPerCustomer
//        )
//        Ok(allBills)
    }
  }


}