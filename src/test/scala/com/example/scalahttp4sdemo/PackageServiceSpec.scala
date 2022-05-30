package com.example.scalahttp4sdemo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PackageServiceSpec extends AnyWordSpec with Matchers {
  val packageService = new PackageService()

  "Package service spec" should {
    "return package when querying a specific package given a valid package id" in {
      val validPackageId = fetchPackageService.packagesDb.map(_.id).head
      packageService.fetchPackageByPackageId(validPackageId) shouldEqual fetchPackageService.packagesDb.head
    }

    "return exception when querying a specific package given an not exist package id" in {
      val invalidPackageId = -1
      assertThrows[RuntimeException](packageService.fetchPackageByPackageId(invalidPackageId))
    }



  }

  private[this] val fetchPackageService: PackageService = {
    new PackageService()
  }

}
