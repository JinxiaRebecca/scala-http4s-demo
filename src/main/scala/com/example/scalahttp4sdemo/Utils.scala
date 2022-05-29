package com.example.scalahttp4sdemo

import java.time.{LocalDate, MonthDay}

case class Utils(){
  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  def filterCurrentBillPeriod(date: LocalDate, billDate: LocalDate): Boolean =  {
    val now = LocalDate.now()
    val bill = billDate.getDayOfMonth
    val currentBillBeginDate = MonthDay.of(now.getMonth, bill)
    val currentBillEndDate = MonthDay.of(now.getMonth.plus(1), bill - 1)
    val compareMonthDate = MonthDay.from(date)
    (compareMonthDate.isAfter(currentBillBeginDate) || compareMonthDate.equals(currentBillBeginDate) ) &&
      (compareMonthDate.isBefore(currentBillEndDate) || compareMonthDate.equals(currentBillEndDate))
  }

}
