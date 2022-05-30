package com.example.scalahttp4sdemo.dao

import com.example.scalahttp4sdemo.service.Usage
import java.time.LocalDate


class UsageDao {
  val now: LocalDate = LocalDate.now()
  val usages: List[Usage] = List(
    Usage(1, 1, 20, 5, now.minusDays(7)),
    Usage(2, 1, 3, 1, now.minusDays(6)),
    Usage(3, 1, 2, 2, now.minusDays(5)),
    Usage(4, 1, 5, 1, now.minusDays(4)),
    Usage(5, 1, 5, 2, now.minusDays(3)),
    Usage(6, 1, 1, 0, now.minusDays(2))
  )

  def queryAllUsagesByCustomerId(customerId: Int): List[Usage] = usages.filter(_.customerId == customerId)
}
