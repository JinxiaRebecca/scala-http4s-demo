package com.example.scalahttp4sdemo

import cats.effect.IO
import com.example.scalahttp4sdemo.controller.DemoController
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite

class HelloWorldSpec extends CatsEffectSuite {

  test("HelloWorld returns status code 200") {
    assertIO(retHelloWorld.map(_.status) ,Status.Ok)
  }

  private[this] val retHelloWorld: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/hello/world")
    val helloWorld = HelloWorld.impl[IO]
    DemoController.helloWorldRoutes(helloWorld).orNotFound(getHW)
  }
}