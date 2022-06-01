package com.example.scalahttp4sdemo.controller

import cats.effect.Sync
import com.example.scalahttp4sdemo.Utils
import com.example.scalahttp4sdemo.service.{CustomerService, PackageService, UsageService}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import java.time.LocalDate
case class UsageResponse(
                           phoneUseLeft: Int,
                           smsUseLeft: Int,
                        )
object UsageController {
  def UsageRoutes[F[_] : Sync](customerService: CustomerService, usageService: UsageService, packageService: PackageService): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "reserved-usage" / IntVar(customerId) =>
        val customer = customerService.fetchCustomerByCustomerId(customerId)
        val subscribedDate = customer.subscribedDate
        val currentBillStartDate = Utils.getRequiredBillPeriodStartDate(subscribedDate, LocalDate.now())
        val currentBillEndDate = Utils.getRequiredBillPeriodEndDate(subscribedDate, LocalDate.now())
        val phoneUse = usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(customerId, currentBillStartDate, currentBillEndDate)
        val smsUse = usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(customerId, currentBillStartDate, currentBillEndDate)
        val packages = packageService.fetchPackageByPackageId(customer.packageId)
        Ok(UsageResponse(packages.phoneLimitation - phoneUse, packages.smsLimitation - smsUse))
    }
  }

}
