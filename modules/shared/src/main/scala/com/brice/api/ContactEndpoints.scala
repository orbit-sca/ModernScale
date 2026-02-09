package com.brice.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import zio.json.*

/** Contact form API endpoints - shared between backend and frontend */
object ContactEndpoints {

  /** Contact form submission request */
  case class ContactRequest(
    name: String,
    email: String,
    message: String
  )

  object ContactRequest {
    given JsonCodec[ContactRequest] = DeriveJsonCodec.gen[ContactRequest]
    given Schema[ContactRequest] = Schema.derived[ContactRequest]
  }

  /** Contact form submission response */
  case class ContactResponse(
    success: Boolean,
    message: String
  )

  object ContactResponse {
    given JsonCodec[ContactResponse] = DeriveJsonCodec.gen[ContactResponse]
    given Schema[ContactResponse] = Schema.derived[ContactResponse]
  }

  /** Error response for contact form */
  case class ContactError(
    error: String,
    details: Option[String] = None
  )

  object ContactError {
    given JsonCodec[ContactError] = DeriveJsonCodec.gen[ContactError]
    given Schema[ContactError] = Schema.derived[ContactError]
  }

  /** Contact form endpoint - POST /api/contact */
  val contactEndpoint: PublicEndpoint[ContactRequest, ContactError, ContactResponse, Any] =
    endpoint.post
      .in("api" / "contact")
      .in(jsonBody[ContactRequest])
      .out(jsonBody[ContactResponse])
      .errorOut(jsonBody[ContactError])
      .description("Submit contact form")
      .tag("Contact")
}