package com.example.scalahttp4sdemo.service

import com.example.scalahttp4sdemo.Utils
import com.example.scalahttp4sdemo.dao.BillDao

import java.time.LocalDate

case class Bill(
                 id: Int,
                 customerId: Int,
                 customerName: String,
                 packageName: String,
                 phoneUse: Int,
                 smsUse: Int,
                 consumptionCost: BigDecimal,
                 billStartDate: LocalDate,
                 billEndDate: LocalDate,
                 status: String)
case class UsageResponse(
                          var phoneUseLeft: Int,
                          var smsUseLeft: Int,
                        )

class BillService(billDao: BillDao, usageService: UsageService, packageService: PackageService) {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def getLatestBillDateByCustomerId(customerId: Int): LocalDate = billDao.getBillsByCustomerId(customerId).map(_.billEndDate).max

  def calculateQueriedBillForSpecificCustomer(customer: Customer, queryDate: LocalDate): Bill = {
    val subscribedDate = customer.subscribedDate
    val currentBillStartDate = Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate)
    val currentBillEndDate = Utils.getRequiredBillPeriodEndDate(subscribedDate, queryDate)
    val phoneUsed = usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
    val smsUsed = usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
    val packages = packageService.fetchPackageByPackageId(customer.packageId)
    val phoneUseLeft = packages.phoneLimitation - phoneUsed
    val smsUseLeft = packages.phoneLimitation - smsUsed
    val exPhoneFee = if (phoneUseLeft < 0) Math.abs(phoneUseLeft) * packages.exPhoneFee else BigDecimal.valueOf(0)
    val exSmsFee = if(smsUseLeft < 0) Math.abs(smsUseLeft) * packages.exSmsFee else BigDecimal.valueOf(0)
    val totalCost = packages.subscriptionFee + exSmsFee + exPhoneFee
    Bill(scala.util.Random.nextInt(Integer.MAX_VALUE), customer.id, customer.name, packages.name, phoneUsed, smsUsed, totalCost,
      currentBillStartDate, currentBillEndDate, "pendingPayment")
  }


}
