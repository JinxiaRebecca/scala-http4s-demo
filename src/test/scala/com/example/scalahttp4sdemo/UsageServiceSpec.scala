package com.example.scalahttp4sdemo


import com.example.scalahttp4sdemo.dao.UsageDao
import com.example.scalahttp4sdemo.service.{Usage, UsageService}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class UsageServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  val now: LocalDate = LocalDate.now()

  val mockedUsageDao: UsageDao = mock[UsageDao]
  val usageService: UsageService = new UsageService(mockedUsageDao)
  val validCustomerId: Int = 100
  val invalidCustomerId: Int = -1

  val mockedUsages: List[Usage] = List(
    Usage(20, 100, 20, 5, now.minusDays(7)),
    Usage(21, 100, 10, 1, now.minusDays(6))
  )


  "Usage service spec" should {

    "return a number when querying bill phone or sms used when given a valid customer id and period" in {
      when(mockedUsageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn mockedUsages
      val startTime = now.minusDays(7)
      val endTime = now.minusDays(6)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(validCustomerId, startTime, endTime).shouldEqual(30)
      usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(validCustomerId, startTime, endTime) shouldEqual 6
    }

    "return 0 when querying bill phone or sms used when given a valid customer id without consumption" in {
      when(mockedUsageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn mockedUsages
      val invalidStartTime = now.plusDays(1)
      val invalidEndTime = now.plusDays(2)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(validCustomerId, invalidStartTime, invalidEndTime) shouldEqual 0
      usageService.calculateSmsUsagesForSpecificPeriodByCustomerId(validCustomerId, invalidStartTime, invalidEndTime) shouldEqual 0
    }

    "return 0 when querying bill phone or sms used when given a invalid customer id" in {
      when(mockedUsageDao.queryAllUsagesByCustomerId(any[Int])) thenReturn List()
      val startTime = now.minusDays(7)
      val endTime = now.minusDays(6)
      usageService.calculatePhoneUsagesForSpecificPeriodByCustomerId(invalidCustomerId, startTime, endTime) shouldEqual 0
    }

  }

}
