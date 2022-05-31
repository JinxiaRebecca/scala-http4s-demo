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
  def updateStatusOfSpecificBill(needUpdateBill: Bill): Boolean = {
    val billsById = billDao.fetchBillById(needUpdateBill.id)
    val oldBil = if (billsById.nonEmpty) billsById.head else  throw new RuntimeException("bill not exist")
    if (oldBil.status.equals("completed") || oldBil.status.equals(needUpdateBill.status) || needUpdateBill.consumptionCost != oldBil.consumptionCost) false
    else {
      val updatedResult = billDao.updateStatusOfBill(needUpdateBill)
      if (updatedResult.contains(needUpdateBill)) true else false
    }
  }

  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def getLatestBillDateByCustomerId(customerId: Int): LocalDate = billDao.getBillsByCustomerId(customerId).map(_.billEndDate).max

  def calculateQueriedBillForSpecificCustomer(customer: Customer, queryDate: LocalDate): Bill = {
    val subscribedDate = customer.subscribedDate
    val currentBillStartDate = Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate)
    val currentBillEndDate = Utils.getRequiredBillPeriodEndDate(subscribedDate, queryDate)
    val bills = getRequiredPeriodBillForCustomer(customer.id, currentBillStartDate, currentBillEndDate)
    if (bills.nonEmpty) bills.head else generateBillForCustomer(customer, currentBillStartDate, currentBillEndDate)
    // TODO if the bill is generated first time, insert the bill to db
  }

  private def getRequiredPeriodBillForCustomer(customerId: Int, startDate: LocalDate, endDate: LocalDate): Seq[Bill] =
    billDao.getBillsByCustomerId(customerId).filter(bill => bill.billStartDate.equals(startDate) && bill.billEndDate.isEqual(endDate))

  private def generateBillForCustomer(customer: Customer, currentBillStartDate: LocalDate, currentBillEndDate: LocalDate): Bill = {
    val phoneUsed = usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
    val smsUsed = usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(customer.id, currentBillStartDate, currentBillEndDate)
    val packages = packageService.fetchPackageByPackageId(customer.packageId)
    val phoneUseLeft = packages.phoneLimitation - phoneUsed
    val smsUseLeft = packages.phoneLimitation - smsUsed
    val exPhoneFee = if (phoneUseLeft < 0) Math.abs(phoneUseLeft) * packages.exPhoneFee else BigDecimal.valueOf(0)
    val exSmsFee = if(smsUseLeft < 0) Math.abs(smsUseLeft) * packages.exSmsFee else BigDecimal.valueOf(0)
    val totalCost = packages.subscriptionFee + exSmsFee + exPhoneFee
    val id = scala.util.Random.nextInt(Integer.MAX_VALUE)
    Bill(id, customer.id, customer.name, packages.name, phoneUsed, smsUsed, totalCost,
      currentBillStartDate, currentBillEndDate, "pendingPayment")
  }



}
