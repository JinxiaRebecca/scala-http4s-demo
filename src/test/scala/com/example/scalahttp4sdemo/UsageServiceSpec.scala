package com.example.scalahttp4sdemo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class UsageServiceSpec extends AnyWordSpec with Matchers {

  "Usage service spec" should {
    val usageService = new UsageService()
    val firstCustomer = Customer(1, "Lily", 1, LocalDate.now().minusDays(7))
    val secondCustomer = Customer(-1, "Tom", 2, LocalDate.of(1997,1, 1))

    "return a number when querying current bill pone use given a valid customer" in {
      usageService.calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(firstCustomer) shouldEqual 36
    }

    "return a number when querying current bill sms use given a valid customer" in {
      usageService.calculateSmsUsagesPerCustomerOfCurrentBillPeriod(firstCustomer) shouldEqual 11
    }

    "return 0 when querying current bill phone use given a customer without consumption" in {
      usageService.calculatePhoneUsagesPerCustomerOfCurrentBillPeriod(secondCustomer) shouldEqual 0
    }

    "return 0 when querying current bill sms use given a customer without consumption" in {
      usageService.calculateSmsUsagesPerCustomerOfCurrentBillPeriod(secondCustomer) shouldEqual 0
    }






  }

}
