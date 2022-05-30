package com.example.scalahttp4sdemo
import com.example.scalahttp4sdemo.service.{Customer, CustomerService}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class CustomerServiceSpec extends AnyWordSpec with Matchers{

  "Customer Service Spec" should {

    "return customer when querying customer given a valid customer id" in {
      val customerService = new CustomerService()
      customerService.fetchCustomerByCustomerId(1) shouldEqual Customer(1, "Lily", 1, LocalDate.now().minusDays(7))
    }

    "return exception when querying customer given a invalid customer id" in {
      val customerService = new CustomerService()
      assertThrows[RuntimeException](customerService.fetchCustomerByCustomerId(-1))
    }


  }

}
