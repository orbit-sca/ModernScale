package com.brice.http

import com.brice.api.ContactEndpoints
import com.brice.api.ContactEndpoints.{ContactRequest, ContactResponse, ContactError}
import com.brice.services.{EmailService, EmailConfig}
import sttp.tapir.server.ServerEndpoint
import zio.*

/** Contact form routes implementation */
object ContactRoutes {

  /** Create contact route with email service */
  def contactRoute(emailService: EmailService): ServerEndpoint[Any, Task] =
    ContactEndpoints.contactEndpoint
      .serverLogic { request =>
        // Validate the request
        val result: Task[Either[ContactError, ContactResponse]] =
          if (request.name.trim.isEmpty) {
            ZIO.succeed(Left(ContactError("Invalid request", Some("Name is required"))))
          } else if (request.email.trim.isEmpty || !request.email.contains("@")) {
            ZIO.succeed(Left(ContactError("Invalid request", Some("Valid email is required"))))
          } else if (request.message.trim.isEmpty) {
            ZIO.succeed(Left(ContactError("Invalid request", Some("Message is required"))))
          } else {
            // Send the email
            emailService.sendContactEmail(
              request.name.trim,
              request.email.trim,
              request.message.trim
            ).map { _ =>
              Right(ContactResponse(
                success = true,
                message = "Thank you for contacting us! We'll get back to you soon."
              ))
            }.catchAll { error =>
              ZIO.succeed(Left(ContactError(
                error = "Failed to send message",
                details = Some(error.getMessage)
              )))
            }
          }
        result
      }

  /** Create all contact routes with email service */
  def routes(emailService: EmailService): List[ServerEndpoint[Any, Task]] = List(
    contactRoute(emailService)
  )
}