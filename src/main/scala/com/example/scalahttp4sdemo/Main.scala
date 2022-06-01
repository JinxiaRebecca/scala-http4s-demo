package com.example.scalahttp4sdemo

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    jtcBillSysServer.stream[IO].compile.drain.as(ExitCode.Success)
}
