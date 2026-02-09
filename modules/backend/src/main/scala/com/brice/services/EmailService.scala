package com.brice.services

import zio.*
import java.net.{HttpURLConnection, URL}
import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import scala.util.Using

/** Email service for sending contact form submissions */
trait EmailService {
  def sendContactEmail(name: String, email: String, message: String): Task[Unit]
}

object EmailService {

  /** Live implementation using Resend HTTP API */
  class Live(config: EmailConfig) extends EmailService {

    override def sendContactEmail(name: String, email: String, message: String): Task[Unit] = {
      ZIO.attempt {
        val url = new URL("https://api.resend.com/emails")
        val connection = url.openConnection().asInstanceOf[HttpURLConnection]

        try {
          connection.setRequestMethod("POST")
          connection.setRequestProperty("Authorization", s"Bearer ${config.apiKey}")
          connection.setRequestProperty("Content-Type", "application/json")
          connection.setDoOutput(true)
          connection.setConnectTimeout(10000)
          connection.setReadTimeout(10000)

          val emailBody = s"""New contact form submission from Brice.solutions:

Name: ${name}
Email: ${email}

Message:
${message}

---
Sent from Brice.solutions contact form"""

          // Escape JSON special characters
          val escapedBody = emailBody
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")

          val escapedName = name.replace("\\", "\\\\").replace("\"", "\\\"")

          val jsonPayload = s"""{
            "from": "${config.fromEmail}",
            "to": ["${config.toEmail}"],
            "subject": "New Contact Form Submission from ${escapedName}",
            "text": "${escapedBody}"
          }"""

          val writer = new OutputStreamWriter(connection.getOutputStream, "UTF-8")
          writer.write(jsonPayload)
          writer.flush()
          writer.close()

          val responseCode = connection.getResponseCode

          if (responseCode >= 200 && responseCode < 300) {
            // Success
            ()
          } else {
            // Read error response
            val errorStream = Option(connection.getErrorStream).getOrElse(connection.getInputStream)
            val reader = new BufferedReader(new InputStreamReader(errorStream))
            val response = new StringBuilder
            var line: String = null
            while ({ line = reader.readLine(); line != null }) {
              response.append(line)
            }
            reader.close()
            throw new RuntimeException(s"Resend API error ($responseCode): ${response.toString}")
          }
        } finally {
          connection.disconnect()
        }
      }.mapError { e =>
        new RuntimeException(s"Failed to send email: ${e.getMessage}", e)
      }
    }
  }

  /** Create a live email service layer */
  val live: ZLayer[EmailConfig, Nothing, EmailService] =
    ZLayer.fromFunction((config: EmailConfig) => new Live(config))

  /** Send a contact email */
  def sendContactEmail(name: String, email: String, message: String): ZIO[EmailService, Throwable, Unit] =
    ZIO.serviceWithZIO[EmailService](_.sendContactEmail(name, email, message))
}

/** Email configuration for Resend */
case class EmailConfig(
  apiKey: String,
  fromEmail: String,
  toEmail: String
)

object EmailConfig {

  /** Load configuration from environment variables */
  val live: ZLayer[Any, Nothing, EmailConfig] =
    ZLayer.succeed {
      EmailConfig(
        apiKey = sys.env.getOrElse("RESEND_API_KEY", ""),
        fromEmail = sys.env.getOrElse("EMAIL_FROM", "onboarding@resend.dev"),
        toEmail = sys.env.getOrElse("EMAIL_TO", "build@modernscale.dev")
      )
    }
}
