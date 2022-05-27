package com.example.scalahttp4sdemo

import cats.Applicative
import com.example.scalahttp4sdemo.Scalahttp4sdemoRoutes.now
import java.time.LocalDate
import cats.implicits._

trait CustomerService[F[_]] {
  def fetchCustomerByCustomerId(id: Int): F[CustomerService.Customer]
}


object CustomerService {
  implicit def apply[F[_]](implicit ev: CustomerService[F]): CustomerService[F] = ev
  case class Customer(
                       var id: Int,
                       var name: String,
                       var packageId: Int,
                       var billDate: LocalDate
                     )
  val customers: List[CustomerService.Customer] = List(
    CustomerService.Customer(1, "Lily", 1, now.minusDays(7)),
    CustomerService.Customer(2, "Coco", 2, now.minusDays(5)),
    CustomerService.Customer(3, "Nico", 3, now.minusDays(6)),
  )

  def impl[F[_]: Applicative]: CustomerService[F] = new CustomerService[F]{
    def fetchCustomerByCustomerId(id: Int): F[CustomerService.Customer] =
      customers.filter(_.id == id).head.pure[F]
  }


}
