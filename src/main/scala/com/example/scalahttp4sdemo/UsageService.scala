package com.example.scalahttp4sdemo

import java.time.{LocalDate, MonthDay}


class UsageService {
  case class Usage(
                        var  id: Int,
                        var customerId: Int,
                        var  phoneUse: Int,
                        var  smsUse: Int,
                        var  consumptionDate: LocalDate)

  val now = LocalDate.now()
  val usages: List[Usage] = List(
    Usage(1, 1, 20, 5, now.minusDays(7)),
    Usage(2, 1, 3, 1, now.minusDays(6)),
    Usage(3, 1, 2, 2, now.minusDays(5)),
    Usage(4, 1, 5, 1, now.minusDays(4)),
    Usage(5, 1, 5, 2, now.minusDays(3)),
    Usage(6, 1, 1, 0, now.minusDays(2))
  )
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  private def filterCurrentBillPeriod(date: LocalDate, billDate: LocalDate): Boolean = {
    val now = LocalDate.now()
    val bill = billDate.getDayOfMonth
    val currentBillBeginDate = MonthDay.of(now.getMonth, bill)
    val currentBillEndDate = MonthDay.of(now.getMonth.plus(1), bill - 1)
    val compareMonthDate = MonthDay.from(date)
    (compareMonthDate.isAfter(currentBillBeginDate) || compareMonthDate.equals(currentBillBeginDate) ) &&
      (compareMonthDate.isBefore(currentBillEndDate) || compareMonthDate.equals(currentBillEndDate))
  }

  private def fetchTotalUsagesByCustomer(customer: Customer): List[Usage] =
    usages.filter(_.customerId == customer.id)
    .filter(usage => filterCurrentBillPeriod(usage.consumptionDate, customer.billDate))

  def calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(customer: Customer): Int =
    fetchTotalUsagesByCustomer(customer).map(_.phoneUse).sum

  def calculateSmsUsagesPerCustomerOfCurrentBillPeriod(customer: Customer): Int =
    fetchTotalUsagesByCustomer(customer).map(_.smsUse).sum
}
