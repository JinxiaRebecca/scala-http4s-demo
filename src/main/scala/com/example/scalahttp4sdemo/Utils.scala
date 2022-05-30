package com.example.scalahttp4sdemo

import java.time.temporal.TemporalAdjusters
import java.time.{LocalDate, Period, YearMonth}

object Utils {
  def getRequiredBillPeriodStartDate(subscribedDate: LocalDate, queryDate: LocalDate): LocalDate =
    if (queryDate.isBefore(subscribedDate)) throw new RuntimeException(s"query date must be after $subscribedDate")
    else getTheSpecificStartDate(subscribedDate, queryDate)

  implicit def intToLong(data: Int): Long =  data.toLong

  private def getTheSpecificStartDate(subscribedDate: LocalDate, queryDate: LocalDate): LocalDate = {
    val day = subscribedDate.getDayOfMonth
    val period: Period = Period.between(subscribedDate, queryDate)
    println(period)
    val yearMonth: YearMonth = YearMonth.from(subscribedDate)
                                        .plusYears(period.getYears.toLong)
                                        .plusMonths(period.getMonths.toLong)
    val lengthOfMonth = yearMonth.lengthOfMonth()
    if (day > lengthOfMonth) yearMonth.atDay(1).`with`(TemporalAdjusters.lastDayOfMonth()) else yearMonth.atDay(day)
  }

  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterSpecificBillPeriod(consumptionDate: LocalDate, startDate: LocalDate, endDate: LocalDate): Boolean = {
    startDate.isBefore(endDate) &&
      ((consumptionDate.isAfter(startDate) || consumptionDate.isEqual(startDate)) &&
        consumptionDate.isBefore(endDate) || consumptionDate.isEqual(endDate))
  }

  private def isTheFirstBill(subscribedDate: LocalDate, latestBillDate: LocalDate, queryDate: LocalDate): Boolean =
    subscribedDate.isEqual(latestBillDate) && queryDate.isEqual(subscribedDate)

  private def getLastBillDate(subscribedDate: LocalDate, latestBillDate: LocalDate): LocalDate = {
    val day = subscribedDate.getDayOfMonth
    val nextMothDay = latestBillDate.plusMonths(1).lengthOfMonth()
    if (day > nextMothDay) latestBillDate.plusMonths(1) else YearMonth.from(latestBillDate).plusMonths(1).atDay(day)
  }

  def getTheCurrentBillDate(subscribedDate: LocalDate, latestBillDate: LocalDate, queryDate: LocalDate): LocalDate = {
    if (isTheFirstBill(subscribedDate, latestBillDate, queryDate)) subscribedDate
    else getLastBillDate(subscribedDate, latestBillDate)
  }







}
