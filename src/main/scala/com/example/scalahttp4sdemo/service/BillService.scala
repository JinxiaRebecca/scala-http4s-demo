package com.example.scalahttp4sdemo.service

import com.example.scalahttp4sdemo.dao.BillDao

import java.time.LocalDate

case class Bill(
                 id: Int,
                 customerId: Int,
                 customerName: String,
                 packageName: String,
                 phoneUse: Int,
                 smsUse: Int,
                 consumptionCost: BigDecimal,
                 billDate: LocalDate,
                 status: String)
case class UsageResponse(
                          var phoneUseLeft: Int,
                          var smsUseLeft: Int,
                        )

class BillService(billDao: BillDao) {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def getLatestBillDateByCustomerId(customerId: Int): LocalDate = billDao.getBillsByCustomerId(customerId).map(_.billDate).max


}
