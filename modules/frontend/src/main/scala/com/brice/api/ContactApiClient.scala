package com.brice.api

import com.brice.api.ContactEndpoints.{ContactRequest, ContactResponse, ContactError}
import org.scalajs.dom
import org.scalajs.dom.HttpMethod
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import zio.json.*

/** API client for contact form submissions */
object ContactApiClient {

  /** Submit a contact form */
  def submitContact(name: String, email: String, message: String): Future[Either[ContactError, ContactResponse]] = {
    val request = ContactRequest(name, email, message)
    val requestBody = request.toJson

    val promise = Promise[Either[ContactError, ContactResponse]]()

    dom.fetch(
      "/api/contact",
      new dom.RequestInit {
        method = HttpMethod.POST
        headers = js.Dictionary(
          "Content-Type" -> "application/json"
        )
        body = requestBody
      }
    ).toFuture.flatMap { response =>
      response.text().toFuture.map { body =>
        if (response.ok) {
          body.fromJson[ContactResponse] match {
            case Right(successResponse) => Right(successResponse)
            case Left(error) => Left(ContactError(s"Failed to parse response: $error"))
          }
        } else {
          body.fromJson[ContactError] match {
            case Right(errorResponse) => Left(errorResponse)
            case Left(_) => Left(ContactError(s"Server error: ${response.status} ${response.statusText}"))
          }
        }
      }
    }.recover { case error =>
      Left(ContactError("Network error", Some(error.getMessage)))
    }
  }
}