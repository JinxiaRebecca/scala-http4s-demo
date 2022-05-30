package com.example.scalahttp4sdemo

import java.time.LocalDate

object Utils {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterSpecificBillPeriod(consumptionDate: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean = {
    startDate.isBefore(endDate) &&
      ((consumptionDate.isAfter(startDate) || consumptionDate.isEqual(startDate)) &&
        consumptionDate.isBefore(endDate) || consumptionDate.isEqual(endDate))
  }

}
