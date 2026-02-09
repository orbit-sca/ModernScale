package com.brice.api

import com.brice.api.CourseEndpoints.{CourseError, CourseListResponse}
import com.brice.domain.*
import org.scalajs.dom
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import zio.json.*

/** API client for course endpoints */
object CourseApiClient:

  /** Fetch all courses */
  def listCourses(): Future[Either[CourseError, List[Course]]] =
    dom.fetch("/api/courses")
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[CourseListResponse] match
          case Right(response) => Right(response.courses)
          case Left(error) => Left(CourseError(s"Failed to parse response: $error"))
      }
      .recover { case error =>
        Left(CourseError("Network error", Some(error.getMessage)))
      }

  /** Fetch a single course by ID */
  def getCourse(id: String): Future[Either[CourseError, Course]] =
    dom.fetch(s"/api/courses/$id")
      .toFuture
      .flatMap { response =>
        response.text().toFuture.map { body =>
          if response.ok then
            body.fromJson[Course] match
              case Right(course) => Right(course)
              case Left(error) => Left(CourseError(s"Failed to parse response: $error"))
          else
            body.fromJson[CourseError] match
              case Right(errorResponse) => Left(errorResponse)
              case Left(_) => Left(CourseError(s"Server error: ${response.status}"))
        }
      }
      .recover { case error =>
        Left(CourseError("Network error", Some(error.getMessage)))
      }
