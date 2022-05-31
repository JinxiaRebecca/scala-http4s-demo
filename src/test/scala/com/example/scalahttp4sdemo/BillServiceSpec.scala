package com.example.scalahttp4sdemo

import com.example.scalahttp4sdemo.dao.BillDao
import com.example.scalahttp4sdemo.service.{Bill, BillService, PackageService, UsageService}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class BillServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {
  val mockedBillDao: BillDao= mock[BillDao]
  val mockedUsageService: UsageService = mock[UsageService]
  val mockedPackageService: PackageService = mock[PackageService]
  val billService: BillService = new BillService(mockedBillDao, mockedUsageService, mockedPackageService)
  val now: LocalDate = LocalDate.now()
  val mockedBills: Seq[Bill] = Seq(
    Bill(1, 1, "Lily", "Starter", 0, 0, 38, now.minusDays(7), now.minusDays(7), "completed"),
    Bill(2, 2, "Coco", "Standard", 0, 0, 58, now.minusDays(5), now.minusDays(5), "completed"),
    Bill(3, 3, "Nico", "Premier", 0, 0, 188, now.minusDays(6),now.minusDays(6), "completed"),
    Bill(1312156876, 1, "Lily", "Starter", 36, 11, 64.5, now.minusDays(7),
      now.minusDays(7).plusMonths(1).minusDays(1), "pendingPayment"),
    Bill(4, 2, "Coco", "Standard", 23, 30, 58, now.minusDays(5),
      now.minusDays(5).plusMonths(1).minusDays(1), "pendingPayment" )
  )


  "Bill service spec" should {
    "return the latest bill date when querying the customer's latest bill date given a valid customer id" in {
      val customerId = 1
      val expectedResult = now.minusDays(7)
      when(mockedBillDao.getBillsByCustomerId(customerId)) thenReturn List(mockedBills.head)
      billService.getLatestBillDateByCustomerId(customerId) shouldEqual expectedResult
    }

    "return false when update the bill status given a valid bill id but status is the same" in {
      val needUpdateBill = Bill(4, 1, "Lily", "Starter", 36, 11, 64.5, now.minusDays(7),
        now.minusDays(7).plusMonths(1).minusDays(1), "pendingPayment")
      when(mockedBillDao.fetchBillById(any[Int])) thenReturn mockedBills.filter(bill => bill.id ==needUpdateBill.id)
      billService.updateStatusOfSpecificBill(needUpdateBill) shouldEqual false
    }

    "return true when update the bill status given a valid bill id and status" in {
      val needUpdateBill = Bill(1312156876, 1, "Lily", "Starter", 36, 11, 64.5, now.minusDays(7),
        now.minusDays(7).plusMonths(1).minusDays(1), "completed")
      //mockedBillDao.bills returns mockedBills
      when(mockedBillDao.bills) thenReturn mockedBills
      when(mockedBillDao.fetchBillById(any[Int])) thenReturn mockedBills.filter(_.id == needUpdateBill.id)
      val updatedDb = mockedBills.updated(3, needUpdateBill)
      when(mockedBillDao.updateStatusOfBill(any[Bill])) thenReturn updatedDb
      billService.updateStatusOfSpecificBill(needUpdateBill) shouldEqual true
    }

    "return exception when update the bill status given a invalid bill id" in {
      val needUpdateBill =  Bill(-1, 1, "Lily", "Starter", 0, 0, 38, now.minusDays(7), now.minusDays(7), "completed")
      when(mockedBillDao.fetchBillById(any[Int])) thenReturn Seq()
      assertThrows[RuntimeException](billService.updateStatusOfSpecificBill(needUpdateBill))
    }

    "return false when update the bill status given a valid bill id but status is completed" in {
      val needUpdateBill = Bill(1, 1, "Lily", "Starter", 0, 0, 38, now.minusDays(7), now.minusDays(7), "pendingPay")
      when(mockedBillDao.fetchBillById(any[Int])) thenReturn mockedBills.filter(_.id == needUpdateBill.id)
      billService.updateStatusOfSpecificBill(needUpdateBill) shouldEqual false
    }

  }

}
