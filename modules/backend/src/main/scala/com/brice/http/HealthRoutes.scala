package com.brice.http

import com.brice.api.HealthEndpoints
import com.brice.api.HealthEndpoints.HealthResponse
import sttp.tapir.server.ServerEndpoint
import zio.*

/** Health check routes implementation */
object HealthRoutes {

  /** Implement the health endpoint with actual logic */
  val healthRoute: ServerEndpoint[Any, Task] =
    HealthEndpoints.healthEndpoint
      .serverLogicSuccess { _ =>
        ZIO.succeed(
          HealthResponse(
            status = "ok",
            message = "Brice.solutions API is running"
          )
        )
      }

  /** All health routes */
  val routes: List[ServerEndpoint[Any, Task]] = List(
    healthRoute
  )

}            