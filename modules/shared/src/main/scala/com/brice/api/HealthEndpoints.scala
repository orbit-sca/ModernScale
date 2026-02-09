package com.brice.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import zio.json.*

/** Health check API endpoints - shared between backend and frontend */
object HealthEndpoints {

  /** Response model for health check */
  case class HealthResponse(status: String, message: String)

  object HealthResponse {
    given JsonCodec[HealthResponse] = DeriveJsonCodec.gen[HealthResponse]
    given Schema[HealthResponse] = Schema.derived[HealthResponse]
  }

  /** Health check endpoint - GET /api/health */
  val healthEndpoint: PublicEndpoint[Unit, Unit, HealthResponse, Any] =
    endpoint.get
      .in("api" / "health")
      .out(jsonBody[HealthResponse])
      .description("Check if the API is healthy")
      .tag("Health")

}