package com.example.scalahttp4sdemo

import java.time.LocalDate

object Utils {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterSpecificBillPeriod(consumptionDate: LocalDate, startTime: LocalDate, endTime: LocalDate): Boolean = {
    (consumptionDate.isAfter(startTime) || consumptionDate.isEqual(startTime)) &&
      consumptionDate.isBefore(endTime) || consumptionDate.isEqual(endTime)
  }

}
