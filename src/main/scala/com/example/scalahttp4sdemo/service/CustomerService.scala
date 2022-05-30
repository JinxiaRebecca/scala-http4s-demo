package com.example.scalahttp4sdemo.service

import com.example.scalahttp4sdemo.dao.CustomerDao

import java.time.LocalDate

case class Customer(
                     var id: Int,
                     var name: String,
                     var packageId: Int,
                     var subscribedDate: LocalDate
                   )

class CustomerService(customerDao: CustomerDao) {

  def fetchCustomerByCustomerId(id: Int): Customer = customerDao.fetchCustomerById(id) match {
    case customer: Customer => customer
    case _ => throw new RuntimeException("customer not exist")
  }
}
