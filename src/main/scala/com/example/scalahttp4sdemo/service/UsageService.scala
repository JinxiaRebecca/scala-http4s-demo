package com.example.scalahttp4sdemo.service

import com.example.scalahttp4sdemo.common.Utils
import com.example.scalahttp4sdemo.dao.UsageDao

import java.time.LocalDate
import scala.util.Try
case class Usage(
                  id: Int,
                  customerId: Int,
                  phoneUse: Int,
                  smsUse: Int,
                  consumptionDate: LocalDate)
case class UsageResponse(
                          phoneUseLeft: Int,
                          smsUseLeft: Int,
                        )
class UsageService(usageDao: UsageDao, customerService: CustomerService, packageService: PackageService) {
  val now: LocalDate = LocalDate.now()
  val usages: List[Usage] = List(
    Usage(1, 1, 20, 5, now.minusDays(7)),
    Usage(2, 1, 3, 1, now.minusDays(6)),
    Usage(3, 1, 2, 2, now.minusDays(5)),
    Usage(4, 1, 5, 1, now.minusDays(4)),
    Usage(5, 1, 5, 2, now.minusDays(3)),
    Usage(6, 1, 1, 0, now.minusDays(2))
  )


  private def fetchTotalUsagesByCustomer(customerId: Int, startTime: LocalDate, endTime: LocalDate): List[Usage] =
     usageDao.queryAllUsagesByCustomerId(customerId)
    .filter(usage => Utils.filterSpecificBillPeriod(usage.consumptionDate, startTime, endTime))

  def calculatePhoneUsagesForSpecificPeriodByCustomerId(customerId: Int, startTime: LocalDate, endTime: LocalDate): Int =
    fetchTotalUsagesByCustomer(customerId, startTime, endTime).map(_.phoneUse).sum

  def calculateSmsUsagesForSpecificPeriodByCustomerId(customerId: Int, startTime: LocalDate, endTime: LocalDate): Int =
    fetchTotalUsagesByCustomer(customerId, startTime, endTime).map(_.smsUse).sum

  def calculateRequiredBillPeriodReservedUsagesForCustomer(customerId: Int, queryDate: LocalDate): Try[UsageResponse] = {
    for {
      customer <- Try(customerService.fetchCustomerByCustomerId(customerId))
      usageResponse <- calculateUsageLeft(customer, queryDate)
    } yield usageResponse
  }

  private def calculateUsageLeft(customer: Customer, queryDate: LocalDate): Try[UsageResponse] =
    Try {
      val currentBillStartDate = Utils.getRequiredBillPeriodStartDate(customer.subscribedDate, queryDate)
      val currentBillEndDate = Utils.getRequiredBillPeriodEndDate(customer.subscribedDate, queryDate)
      val phoneUse = calculatePhoneUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
      val smsUse = calculateSmsUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
      val packages = packageService.fetchPackageByPackageId(customer.packageId)
      UsageResponse(packages.phoneLimitation - phoneUse, packages.smsLimitation - smsUse)
    }

}
