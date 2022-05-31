package com.example.scalahttp4sdemo.dao

import com.example.scalahttp4sdemo.service.Customer

import java.time.LocalDate

class CustomerDao {
  val now: LocalDate = LocalDate.now()
  val customers: List[Customer] = List(
    Customer(1, "Lily", 1, now.minusDays(7)),
    Customer(2, "Coco", 2, now.minusDays(5)),
    Customer(3, "Nico", 3, now.minusDays(6)),
  )

  def fetchAllCustomers(): List[Customer] = customers

  def fetchCustomerById(customerId: Int): List[Customer] = customers.filter(_.id == customerId)

}
