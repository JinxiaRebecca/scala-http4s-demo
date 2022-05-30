package com.example.scalahttp4sdemo

import java.time.{LocalDate}
case class Usage(
                  id: Int,
                  customerId: Int,
                  phoneUse: Int,
                  smsUse: Int,
                  consumptionDate: LocalDate)

class UsageService {


  val now = LocalDate.now()
  val usages: List[Usage] = List(
    Usage(1, 1, 20, 5, now.minusDays(7)),
    Usage(2, 1, 3, 1, now.minusDays(6)),
    Usage(3, 1, 2, 2, now.minusDays(5)),
    Usage(4, 1, 5, 1, now.minusDays(4)),
    Usage(5, 1, 5, 2, now.minusDays(3)),
    Usage(6, 1, 1, 0, now.minusDays(2))
  )


  private def fetchTotalUsagesByCustomer(customer: Customer): List[Usage] =
    usages.filter(_.customerId == customer.id)
    .filter(usage => Utils().filterCurrentBillPeriod(usage.consumptionDate, customer.billDate))

  def calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(customer: Customer): Int =
    fetchTotalUsagesByCustomer(customer).map(_.phoneUse).sum

  def calculateSmsUsagesPerCustomerOfCurrentBillPeriod(customer: Customer): Int =
    fetchTotalUsagesByCustomer(customer).map(_.smsUse).sum
}
