package com.example.scalahttp4sdemo

import cats.Applicative
import cats.implicits._


trait PackageService [F[_]]{
  def fetchPackageOfCustomer(customer: CustomerService.Customer): F[PackageService.Package]
}

object PackageService {
  implicit def apply[F[_]](implicit ev: PackageService[F]): PackageService[F] = ev

  case class Package( var id: Int,
                      var name: String,
                      var subscriptionFee: BigDecimal,
                      var phoneLimitation: Int,
                      var smsLimitation: Int,
                      var exPhoneFee: BigDecimal,
                      var exSmsFee: BigDecimal)

  val packagesDb: List[Package] = List(
    Package(1, "Starter", 38, 10, 10, 1, 0.5),
    Package(2, "Standard", 58, 30, 40, 1, 0.5),
    Package(3, "Premier", 188, 300, 200, 1, 0.5)
  )

  def impl[F[_]: Applicative]: PackageService[F] = new PackageService[F]{
    def fetchPackageOfCustomer(customer: CustomerService.Customer): F[PackageService.Package] =
      packagesDb.filter(_.id == customer.packageId).head.pure[F]
  }

}
