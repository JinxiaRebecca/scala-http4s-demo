package com.example.scalahttp4sdemo

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite

class UsageSpec extends CatsEffectSuite{
  test("Usage returns status code 200") {
    assertIO(retUsage.map(_.status) ,Status.Ok)
  }

  private[this] val retUsage: IO[Response[IO]] = {
    val getUsage = Request[IO](Method.GET, uri"/reserved-usage/1")
    val usage = Usage.impl[IO]
    Scalahttp4sdemoRoutes.UsageRoutes(usage).orNotFound(getUsage)
  }

}
