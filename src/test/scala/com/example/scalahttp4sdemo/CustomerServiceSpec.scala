package com.example.scalahttp4sdemo
import com.example.scalahttp4sdemo.dao.CustomerDao
import com.example.scalahttp4sdemo.service.{Customer, CustomerService}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class CustomerServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  val mockedCustomerDao: CustomerDao = mock[CustomerDao]
  val customerService: CustomerService = new CustomerService(mockedCustomerDao)
  val now: LocalDate = LocalDate.now()
  val mockedCustomers: List[Customer] = List(
    Customer(1, "Lily", 1, now.minusDays(7)),
    Customer(2, "Coco", 2, now.minusDays(5)),
    Customer(3, "Nico", 3, now.minusDays(6)),
  )

  "Customer Service Spec" should {

    "return customer when querying customer given a valid customer id" in {
      when(mockedCustomerDao.fetchCustomerById(any[Int])) thenReturn Customer(1, "Lily", 1, now.minusDays(7))
      customerService.fetchCustomerByCustomerId(1) shouldEqual Customer(1, "Lily", 1, LocalDate.now().minusDays(7))
    }

    "return exception when querying customer given a invalid customer id" in {
      when(mockedCustomerDao.fetchCustomerById(any[Int])) thenReturn null
      assertThrows[RuntimeException](customerService.fetchCustomerByCustomerId(-1))
    }

    "return all customer list when query all customers" in {
      when(mockedCustomerDao.fetchAllCustomers()) thenReturn mockedCustomers
      customerService.fetchAllCustomers() shouldEqual mockedCustomers
    }


  }

}
