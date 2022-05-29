package com.example.scalahttp4sdemo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class PackageServiceSpec extends AnyWordSpec with Matchers {
  val packageService = new PackageService()
  val now = LocalDate.now()

  "Package service spec" should {
    "return package when querying a specific package given a valid customer id" in {
      val firstCustomer = Customer(1, "Lily", 1, now.minusDays(7))
      packageService.fetchPackageOfCustomer(firstCustomer) shouldEqual Package(1, "Starter", 38, 10, 10, 1, 0.5)
    }

    "return exception when querying a specific package given an not exist package id" in {
      val invalidCustomer = Customer(1, "Lily", -1, now.minusDays(7))
      assertThrows[RuntimeException](packageService.fetchPackageOfCustomer(invalidCustomer))
    }



  }

}
