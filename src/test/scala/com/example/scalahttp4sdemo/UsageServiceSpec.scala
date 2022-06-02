package com.example.scalahttp4sdemo


import com.example.scalahttp4sdemo.dao.{CustomerDao, UsageDao}
import com.example.scalahttp4sdemo.service.{Customer, CustomerService, Package, PackageService, Usage, UsageResponse, UsageService}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class UsageServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  val now: LocalDate = LocalDate.now()

  val usageDao: UsageDao = mock[UsageDao]
  val customerDao: CustomerDao = mock[CustomerDao]
  val customerService: CustomerService = mock[CustomerService]
  val packageService: PackageService = mock[PackageService]
  val usageService: UsageService = new UsageService(usageDao, customerService, packageService)

  val validCustomerId: Int = 100
  val invalidCustomerId: Int = -1

  val mockedUsages: List[Usage] = List(
    Usage(20, 100, 20, 5, now.minusDays(7)),
    Usage(21, 100, 10, 1, now.minusDays(6))
  )

 val customers: List[Customer] =  List(
   Customer(1, "Lily", 1, now.minusDays(7)),
   Customer(2, "Coco", 2, now.minusDays(5)),
   Customer(3, "Nico", 3, now.minusDays(6)),
 )


  "Usage service spec" should {

    "return a number when querying bill phone or sms used when given a valid customer id and period" in {
      when(usageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn mockedUsages
      val startTime = now.minusDays(7)
      val endTime = now.minusDays(6)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(validCustomerId, startTime, endTime).shouldEqual(30)
      usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(validCustomerId, startTime, endTime) shouldEqual 6
    }

    "return 0 when querying bill phone or sms used when given a valid customer id without consumption" in {
      when(usageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn mockedUsages
      val invalidStartTime = now.plusDays(1)
      val invalidEndTime = now.plusDays(2)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(validCustomerId, invalidStartTime, invalidEndTime) shouldEqual 0
      usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(validCustomerId, invalidStartTime, invalidEndTime) shouldEqual 0
    }

    "return 0 when querying bill phone or sms used when given a invalid customer id" in {
      when(usageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn List()
      val startTime = now.minusDays(7)
      val endTime = now.minusDays(6)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(invalidCustomerId, startTime, endTime) shouldEqual 0
    }

    "return exception when calculating the required bill period reserved usages given a customer id not exist" in {
      val customerId = -1;
      when(customerDao.fetchCustomerById(any[Int])) thenReturn  List()
      when(customerService.fetchCustomerByCustomerId(any[Int])).thenCallRealMethod
      assertThrows[Exception](usageService.calculateRequiredBillPeriodReservedUsagesForCustomer(customerId, LocalDate.now()).get)
    }

    "return exception when calculating the required bill period reserved usages given a valid customer id but invalid query date" in {
      val customerId = 130;
      when(customerDao.fetchCustomerById(customerId)) thenReturn List(Customer(130, "", 1, now.minusDays(7)))
      val queryDate = LocalDate.now().plusDays(1)
      assertThrows[RuntimeException](usageService.calculateRequiredBillPeriodReservedUsagesForCustomer(customerId, queryDate).get)
    }

    "return the subscribed usage in package when calculating the required bill period reserved usages given a customer without any usage" in {
      val customerId = 2
      val usageResponse = UsageResponse(30, 40)
      when(customerService.fetchCustomerByCustomerId(any[Int])) thenReturn Customer(2, "Coco", 2, now.minusDays(5))
      when(usageDao.queryAllUsagesByCustomerId(any[Int])).thenReturn(List())
      when(packageService.fetchPackageByPackageId(any[Int])).thenReturn(Package(2, "Standard", 58, 30, 40, 1, 0.5))
      usageService.calculateRequiredBillPeriodReservedUsagesForCustomer(customerId, now).get shouldEqual usageResponse
    }

    "return usage left in current bill period when calculating the required bill period reserved usages given customer which has consumption" in {
      val customerId = 1

      when(customerService.fetchCustomerByCustomerId(any[Int])) thenReturn Customer(1, "Lily", 1, now.minusDays(7))
      val usages: List[Usage] =  List(
        Usage(1, 1, 20, 5, now.minusDays(7)),
        Usage(2, 1, 3, 1, now.minusDays(6)),
        Usage(3, 1, 2, 2, now.minusDays(5)),
        Usage(4, 1, 5, 1, now.minusDays(4)),
        Usage(5, 1, 5, 2, now.minusDays(3)),
        Usage(6, 1, 1, 0, now.minusDays(2))
      )
      when(usageDao.queryAllUsagesByCustomerId(any[Int])).thenReturn(usages)
      when(packageService.fetchPackageByPackageId(any[Int])).thenReturn(Package(1, "Starter", 38, 10, 10, 1, 0.5))
      val expectedResult = UsageResponse(-26, -1)
      usageService.calculateRequiredBillPeriodReservedUsagesForCustomer(customerId, now).get shouldEqual expectedResult
    }

  }

}
