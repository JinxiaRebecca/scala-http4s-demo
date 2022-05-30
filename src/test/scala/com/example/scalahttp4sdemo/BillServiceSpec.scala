package com.example.scalahttp4sdemo

import com.example.scalahttp4sdemo.dao.BillDao
import com.example.scalahttp4sdemo.service.{Bill, BillService}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class BillServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  val mockedBillDao: BillDao= mock[BillDao]
  val billService: BillService = new BillService(mockedBillDao)
  val now: LocalDate = LocalDate.now()
  val mockedBills: List[Bill] = List(
    Bill(1, 1, "Lily", "Starter", 0, 0, 38, now.minusDays(7), "completed"),
    Bill(2, 2, "Coco", "Standard", 0, 0, 58, now.minusDays(5), "completed"),
    Bill(3, 3, "Nico", "Premier", 0, 0, 188, now.minusDays(6), "completed")
  )

  "Bill service spec" should {
    "return the latest bill date when querying the customer's latest bill date given a valid customer id" in {
      val customerId = 1
      val expectedResult = now.minusDays(7)
      when(mockedBillDao.getBillsByCustomerId(customerId)) thenReturn List(mockedBills.head)
      billService.getLatestBillDateByCustomerId(customerId) shouldEqual expectedResult
    }
  }

}
