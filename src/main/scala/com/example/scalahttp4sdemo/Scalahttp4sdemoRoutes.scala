package com.example.scalahttp4sdemo

import cats.effect.Sync
import cats.implicits._
import org.http4s.{HttpRoutes, ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{QueryParamDecoderMatcher, ValidatingQueryParamDecoderMatcher}

import java.time.{LocalDate, MonthDay}
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder


object Scalahttp4sdemoRoutes {
  final case class Customer(id: Int,
                            name: String,
                            packageId: Int,
                            billDate: LocalDate)
  final case class Usage(
                          id: Int,
                          customerId: Int,
                          phoneUse: Int,
                          smsUse: Int,
                          consumptionDate: LocalDate)
  final case class UsageResponse(
                            phoneUseLeft: Int,
                            smsUseLeft: Int,
                            billStartDate: LocalDate)

  final case class Packages(id: Int,
                            name: String,
                            subscriptionFee: BigDecimal,
                            phoneLimitation: Int,
                            smsLimitation: Int,
                            exPhoneFee: BigDecimal,
                            exSmsFee: BigDecimal)
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
  val PackagesDB: List[Packages] = List(
    Packages(1, "Starter", 38, 10, 10, 1, 0.5),
    Packages(2, "Standard", 58, 30, 40, 1, 0.5),
    Packages(3, "Premier", 188, 300, 200, 1, 0.5)
  )
  val customers: List[Customer] = List(
    Customer(1, "Lily", 1, now.minusDays(7)),
    Customer(2, "Coco", 2, now.minusDays(5)),
    Customer(3, "Nico", 3, now.minusDays(6)),
  )

  val UsagesDB: mutable.ListBuffer[Usage] = ListBuffer(
    Usage(1, 1, 20, 5, now.minusDays(7)),
    Usage(2, 1, 3, 1, now.minusDays(6)),
    Usage(3, 1, 2, 2, now.minusDays(5)),
    Usage(4, 1, 5, 1, now.minusDays(4)),
    Usage(5, 1, 5, 2, now.minusDays(3)),
    Usage(6, 1, 1, 0, now.minusDays(2))
  )
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterCurrentBillPeriod(date: LocalDate, billDate: LocalDate): Boolean = {
    val now = LocalDate.now()
    val bill = billDate.getDayOfMonth
    val currentBillBeginDate = MonthDay.of(now.getMonth, bill)
    val currentBillEndDate = MonthDay.of(now.getMonth.plus(1), bill - 1)
    val compareMonthDate = MonthDay.from(date)
    (compareMonthDate.isAfter(currentBillBeginDate) || compareMonthDate.equals(currentBillBeginDate) ) &&
      (compareMonthDate.isBefore(currentBillEndDate) || compareMonthDate.equals(currentBillEndDate))
  }
  def calculateUsagesPerCustomerOfCurrentBillPeriod(customer: Customer):ListBuffer[Usage] =
    UsagesDB.filter(usage => usage.customerId == customer.id)
    .filter(usage => filterCurrentBillPeriod(usage.consumptionDate, customer.billDate))
  def fetchPackageOfCustomer(customer: Customer): Packages =
    PackagesDB.filter(_.id == customer.packageId).head

  def calculateAllBillsPerCustomer(customer: Customer): BillResponse = {
    val usages = calculateUsagesPerCustomerOfCurrentBillPeriod(customer)
    val packages = fetchPackageOfCustomer(customer)
    val phoneUseLeft = usages.map(_.phoneUse).sum - packages.phoneLimitation
    val smsUseLeft = usages.map(_.smsUse).sum - packages.smsLimitation
    val exPhoneUseFee: BigDecimal = if (phoneUseLeft > 0)  packages.exPhoneFee * phoneUseLeft else 0
    val exSmsUseFee: BigDecimal = if (smsUseLeft > 0) packages.exSmsFee * smsUseLeft else 0
    val consumptionCost = packages.subscriptionFee + exPhoneUseFee + exSmsUseFee
    val now = LocalDate.now()
    val billStartDate = LocalDate.of(now.getYear, now.getMonth, customer.billDate.getDayOfMonth)
    BillResponse(customer.id, customer.name, packages.name, usages.map(_.phoneUse).sum, usages.map(_.smsUse).sum, consumptionCost, billStartDate)
  }


  def UsageRoutes[F[_]: Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "reserved-usage" :? CustomerIdQueryParamMather(customerId) =>
        val customer: Customer = customers.filter(customer => customer.id == customerId).head
        val usages = calculateUsagesPerCustomerOfCurrentBillPeriod(customer)
        val packageOfCustomer = fetchPackageOfCustomer(customer)
        val phoneUseLeft = packageOfCustomer.phoneLimitation - usages.map(_.phoneUse).sum
        val smsUseLeft = packageOfCustomer.phoneLimitation - usages.map(_.smsUse).sum
        val now = LocalDate.now()
        val billStartTime: LocalDate = LocalDate.of(now.getYear, now.getMonth, customer.billDate.getDayOfMonth)
        Ok(UsageResponse(phoneUseLeft, smsUseLeft, billStartTime))
    }
  }

  def BillRoutes[F[_] :Sync]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "all-bills" =>
        val allBills = customers.map(
          calculateAllBillsPerCustomer
        )
        Ok(allBills)
    }
  }


}