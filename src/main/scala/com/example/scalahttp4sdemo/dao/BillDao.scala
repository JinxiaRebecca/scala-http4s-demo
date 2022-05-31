package com.example.scalahttp4sdemo.dao

import com.example.scalahttp4sdemo.service.Bill

import java.time.LocalDate

class BillDao {
  val bills: List[Bill] = List (
    Bill(1, 1, "Lily", "Starter", 0, 0, 38, LocalDate.now().minusDays(7), LocalDate.now().minusDays(7), "completed"),
    Bill(2, 2, "Coco", "Standard", 0, 0, 58, LocalDate.now().minusDays(5), LocalDate.now().minusDays(5),"completed"),
    Bill(3, 3, "Nico", "Premier", 0, 0, 188, LocalDate.now().minusDays(6), LocalDate.now().minusDays(6), "completed")
  )
  def getBillsByCustomerId(customerId: Int): List[Bill] = bills.filter(_.customerId == customerId)

}
