package com.example.scalahttp4sdemo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class UtilsSpec extends AnyWordSpec with Matchers {
  val now: LocalDate = LocalDate.now()

  "Utils Spec" should {

    "return true when verifying a specific bill period given consumption date between start and end date" in {
      val consumptionDate = now
      val startDate = now.minusDays(1)
      val endDate = now.plusDays(1)
      Utils.filterSpecificBillPeriod(consumptionDate, startDate, endDate) shouldEqual true
    }

    "return true when verifying a specific bill period given consumption date equals start or end date" in {
      Utils.filterSpecificBillPeriod(now, now, now.plusDays(1)) shouldEqual true
      Utils.filterSpecificBillPeriod(now.plusDays(1), now, now.plusDays(1)) shouldEqual true
    }

    "return false when verifying a specific bill period given consumption not between start and end date" in {
      Utils.filterSpecificBillPeriod(now.minusDays(1), now, now.plusDays(1)) shouldEqual false
      Utils.filterSpecificBillPeriod(now.plusDays(2), now, now.plusDays(1)) shouldEqual false
    }

    "return false when verifying a specific bill period given the start date is after end date" in {
      Utils.filterSpecificBillPeriod(now, now.plusDays(1), now) shouldEqual false
    }

    "return subscribedDate when querying the required bill start date given the valid queryDate equals subscribedDate" in {
      val subscribedDate = now
      val queryDate = now
      val expectedDate = now
      Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return subscribedDate when querying the required bill start date given the queryDate within one month after subscribedDate" in {
      val subscribedDate = now
      val queryDate = now.plusDays(20)
      val expectedDate = now
      Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return the last day of next month when querying the required bill start date given the subscribedDate greater than the last day of next month" in {
      val subscribedDate = LocalDate.of(2022, 1, 31)
      val queryDate = LocalDate.of(2022, 3, 2)
      val expectedDate = LocalDate.of(2022, 2, 28)
      Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return the same day of next year when querying the required bill start date given the subscribedDate is the last month of the year" in {
      val subscribedDate = LocalDate.of(2021, 12, 15)
      val queryDate = LocalDate.of(2022, 1, 20)
      val expectedDate = LocalDate.of(2022, 1, 15)
      Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return the same day of the month when querying the required bill start date given the subscribedDate is before some months of queryDate" in {
      val subscribedDate = LocalDate.of(2022, 1, 5)
      val queryDate = LocalDate.of(2022, 5, 10)
      val expectedDate = LocalDate.of(2022, 5, 5)
      Utils.getRequiredBillPeriodStartDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return exception when querying the required bill start date given the queryDate is before subscribedDate" in {
      assertThrows[RuntimeException](Utils.getRequiredBillPeriodStartDate(now, now.minusDays(4)))
    }

    "return exception when querying the required bill end date given the queryDate is before subscribedDate" in {
      assertThrows[RuntimeException](Utils.getRequiredBillPeriodEndDate(now, now.minusDays(4)))
    }

    "return the last day of the same month when querying the required bill end date given the subscribedDate is the first day of month" in {
      val subscribedDate = LocalDate.of(2022, 5, 1)
      val queryDate = LocalDate.of(2022, 5, 20)
      val expectedDate = LocalDate.of(2022, 5, 31)
      Utils.getRequiredBillPeriodEndDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return the subscribedDate when querying the required bill end date given the queryDate is the same day as subscribedDate" in {
      val subscribedDate = LocalDate.of(2022, 5, 20)
      val queryDate = LocalDate.of(2022, 5, 20)
      val expectedDate = LocalDate.of(2022, 5, 20)
      Utils.getRequiredBillPeriodEndDate(subscribedDate, queryDate) shouldEqual expectedDate
    }

    "return the day before day of subscribedDate in next year when querying required bill end date given the queryDate is not the same year as subscribedDate" in {
      val subscribedDate = LocalDate.of(2021, 11, 18)
      val queryDate = LocalDate.of(2022, 1, 10)
      val expectedDate = LocalDate.of(2022, 1, 17)
      Utils.getRequiredBillPeriodEndDate(subscribedDate, queryDate) shouldEqual expectedDate
    }
  }

}
