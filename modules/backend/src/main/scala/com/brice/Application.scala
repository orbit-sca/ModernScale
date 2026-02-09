package com.brice

import com.brice.http.HttpServer
import zio.*

object Application extends ZIOAppDefault:

  def run: ZIO[Any, Throwable, Unit] =
    for
      _ <- ZIO.logInfo("Starting Brice.solutions backend application...")
      _ <- HttpServer.start
    yield ()
