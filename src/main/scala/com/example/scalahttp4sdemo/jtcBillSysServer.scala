package com.example.scalahttp4sdemo

import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import com.example.scalahttp4sdemo.controller.{BillController, PaymentController, DemoController, UsageController}
import com.example.scalahttp4sdemo.dao.{BillDao, CustomerDao, UsageDao}
import com.example.scalahttp4sdemo.service.{BillService, CustomerService, PackageService, UsageService}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger

object jtcBillSysServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      customerService = new  CustomerService(new CustomerDao())
      usageService = new UsageService(new UsageDao())
      packageService=  new PackageService()
      billService = new BillService(new BillDao(), usageService, packageService)
      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = (
        DemoController.helloWorldRoutes[F](helloWorldAlg) <+>
        DemoController.jokeRoutes[F](jokeAlg) <+>
          UsageController.UsageRoutes[F](customerService, usageService, packageService) <+>
          BillController.BillRoutes[F](customerService, billService) <+>
          PaymentController.paymentRoutes[F](billService)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
        Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain
}
