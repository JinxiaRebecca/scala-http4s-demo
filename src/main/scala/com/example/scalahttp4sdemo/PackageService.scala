package com.example.scalahttp4sdemo

case class Package( var id: Int,
                    var name: String,
                    var subscriptionFee: BigDecimal,
                    var phoneLimitation: Int,
                    var smsLimitation: Int,
                    var exPhoneFee: BigDecimal,
                    var exSmsFee: BigDecimal)
class PackageService {

  val packagesDb: List[Package] = List(
    Package(1, "Starter", 38, 10, 10, 1, 0.5),
    Package(2, "Standard", 58, 30, 40, 1, 0.5),
    Package(3, "Premier", 188, 300, 200, 1, 0.5)
  )

  def fetchPackageOfCustomer(customer: Customer): Package =
    packagesDb.filter(_.id == customer.packageId)
              .head match {
      case specificPackage: Package => specificPackage
      case _ => throw new RuntimeException(s"Package for $customer is not found")
    }

}
