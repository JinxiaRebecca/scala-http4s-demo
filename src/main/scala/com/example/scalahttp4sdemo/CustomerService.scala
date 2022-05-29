package com.example.scalahttp4sdemo

import java.time.LocalDate

case class Customer(
                     var id: Int,
                     var name: String,
                     var packageId: Int,
                     var billDate: LocalDate
                   )

class CustomerService {
  val now = LocalDate.now()
  val customers: List[Customer] = List(

    Customer(1, "Lily", 1, now.minusDays(7)),
    Customer(2, "Coco", 2, now.minusDays(5)),
    Customer(3, "Nico", 3, now.minusDays(6)),
  )

  def fetchCustomerByCustomerId(id: Int): Customer = customers.filter(_.id == id).head match {
    case customer: Customer => customer
    case _ => throw new RuntimeException("customer not exist")
  }
}
