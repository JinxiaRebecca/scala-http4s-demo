package com.example.scalahttp4sdemo

import cats.effect.Sync
import cats.implicits._
import com.example.scalahttp4sdemo.service.{Bill, BillService, CustomerService, PackageService, UsageService}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

import java.time.LocalDate

object Scalahttp4sdemoRoutes {
  case class UsageResponse(
                            var phoneUseLeft: Int,
                            var smsUseLeft: Int,
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

  def UsageRoutes[F[_]: Sync](customerService: CustomerService, usageService: UsageService, packageService: PackageService): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "reserved-usage"/ IntVar(customerId)  =>
            val customer = customerService.fetchCustomerByCustomerId(customerId)
            val subscribedDate = customer.subscribedDate
            val currentBillStartDate = Utils.getRequiredBillPeriodStartDate(subscribedDate, LocalDate.now())
            val currentBillEndDate = Utils.getRequiredBillPeriodEndDate(subscribedDate, LocalDate.now())
            val phoneUse= usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(customerId, currentBillStartDate, currentBillEndDate)
            val smsUse = usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(customerId, currentBillStartDate, currentBillEndDate)
            val packages = packageService.fetchPackageByPackageId(customer.packageId)
            Ok(UsageResponse(packages.phoneLimitation - phoneUse, packages.smsLimitation -smsUse))
    }
  }

    def BillRoutes[F[_] :Sync](customerService: CustomerService, billService: BillService): HttpRoutes[F] = {
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
      }
    }



}