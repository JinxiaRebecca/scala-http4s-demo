package com.example.scalahttp4sdemo.dao

import com.example.scalahttp4sdemo.service.Bill

import java.time.LocalDate

class BillDao {
  val bills: Seq[Bill] = Seq (
    Bill(1, 1, "Lily", "Starter", 0, 0, 38, LocalDate.now().minusDays(7), LocalDate.now().minusDays(7), "completed"),
    Bill(2, 2, "Coco", "Standard", 0, 0, 58, LocalDate.now().minusDays(5), LocalDate.now().minusDays(5),"completed"),
    Bill(3, 3, "Nico", "Premier", 0, 0, 188, LocalDate.now().minusDays(6), LocalDate.now().minusDays(6), "completed")
  )
  def getBillsByCustomerId(customerId: Int): Seq[Bill] = bills.filter(_.customerId == customerId)

  def insertNewBill(bill: Bill): Seq[Bill] = bills :+ bill

  def fetchBillById(id: Int): Seq[Bill] = bills.filter(_.id == id)

  def updateStatusOfBill(bill: Bill): Seq[Bill] = {
      val index = bills.indexOf(bills.filter(_.id == bill.id).head)
      bills.updated(index, bill)
  }

}
