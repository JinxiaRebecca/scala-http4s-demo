package com.example.scalahttp4sdemo

import java.time.{LocalDate, YearMonth}

object Utils {
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterSpecificBillPeriod(consumptionDate: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean = {
    startDate.isBefore(endDate) &&
      ((consumptionDate.isAfter(startDate) || consumptionDate.isEqual(startDate)) &&
        consumptionDate.isBefore(endDate) || consumptionDate.isEqual(endDate))
  }

  def isTheFirstBill(subscribedDate: LocalDate, latestBillDate: LocalDate, queryDate: LocalDate): Boolean =
    subscribedDate.isEqual(latestBillDate) && queryDate.isEqual(subscribedDate)

  def getLastBillDate(subscribedDate: LocalDate, latestBillDate: LocalDate): LocalDate = {
    val day = subscribedDate.getDayOfMonth
    val nextMothDay = latestBillDate.plusMonths(1).lengthOfMonth()
    if (day > nextMothDay) latestBillDate.plusMonths(1) else YearMonth.from(latestBillDate).plusMonths(1).atDay(day)
  }

  def getTheCurrentBillDate(subscribedDate: LocalDate, latestBillDate: LocalDate, queryDate: LocalDate): LocalDate = {
    if (isTheFirstBill(subscribedDate, latestBillDate, queryDate)) subscribedDate
    else getLastBillDate(subscribedDate, latestBillDate)
  }







}
