package com.example.scalahttp4sdemo

import cats.Applicative
import cats.implicits._

import java.time.{LocalDate, MonthDay}

trait UsageService[F[_]] {
  def calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(customer: CustomerService.Customer): F[Int]
  def calculateSmsUsagesPerCustomerOfCurrentBillPeriod(customer: CustomerService.Customer): F[Int]
}

object UsageService {
  implicit def apply[F[_]](implicit ev: UsageService[F]): UsageService[F] = ev
  implicit def transfer[F[_]](implicit ev: PackageService[F]): PackageService[F] = ev
  final case class Usage(
                          id: Int,
                          customerId: Int,
                          phoneUse: Int,
                          smsUse: Int,
                          consumptionDate: LocalDate)

  val now = LocalDate.now()
  val usagesDb: List[Usage] = List(
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

  private def fetchTotalUsagesByCustomer(customer: CustomerService.Customer): List[Usage] =
    usagesDb.filter(_.customerId == customer.id)
    .filter(usage => filterCurrentBillPeriod(usage.consumptionDate, customer.billDate))

  def impl[F[_]: Applicative]: UsageService[F] = new UsageService[F]{
    def calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(customer: CustomerService.Customer): F[Int] =
      fetchTotalUsagesByCustomer(customer).map(_.phoneUse).sum.pure[F]

    def calculateSmsUsagesPerCustomerOfCurrentBillPeriod(customer: CustomerService.Customer): F[Int] =
      fetchTotalUsagesByCustomer(customer).map(_.smsUse).sum.pure[F]
  }

}
