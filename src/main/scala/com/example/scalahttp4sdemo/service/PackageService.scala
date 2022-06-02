package com.example.scalahttp4sdemo.service

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

  def fetchPackageByPackageId(packageId: Int): Package =
    packagesDb.filter(_.id == packageId).head

}
