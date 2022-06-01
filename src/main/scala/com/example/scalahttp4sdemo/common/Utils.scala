package com.example.scalahttp4sdemo.common

import java.time.temporal.TemporalAdjusters
import java.time.{LocalDate, Period, YearMonth}

object Utils {
  def getRequiredBillPeriodStartDate(subscribedDate: LocalDate, queryDate: LocalDate): LocalDate =
    if (queryDate.isBefore(subscribedDate)) throw new RuntimeException(s"query date must be after $subscribedDate")
    else getTheSpecificStartDate(subscribedDate, queryDate)

  def getRequiredBillPeriodEndDate(subscribedDate: LocalDate, queryDate: LocalDate): LocalDate =
    if (subscribedDate.isEqual(queryDate)) subscribedDate
    else getRequiredBillPeriodStartDate(subscribedDate, queryDate).plusMonths(1).minusDays(1)

  implicit def intToLong(data: Int): Long = data.toLong

  private def getTheSpecificStartDate(subscribedDate: LocalDate, queryDate: LocalDate): LocalDate = {
    val day = subscribedDate.getDayOfMonth
    val period: Period = Period.between(subscribedDate, queryDate)
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

}
