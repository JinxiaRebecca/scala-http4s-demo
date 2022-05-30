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
  }

}
