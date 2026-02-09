package com.brice.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import zio.json.*
import com.brice.domain.*

/** Course API endpoints - shared between backend and frontend */
object CourseEndpoints:

  /** Error response for course operations */
  final case class CourseError(
    error: String,
    details: Option[String] = None
  )

  object CourseError:
    given JsonCodec[CourseError] = DeriveJsonCodec.gen[CourseError]
    given Schema[CourseError] = Schema.derived[CourseError]

  /** Response containing a list of courses */
  final case class CourseListResponse(
    courses: List[Course],
    total: Int
  )

  object CourseListResponse:
    given JsonCodec[CourseListResponse] = DeriveJsonCodec.gen[CourseListResponse]
    given Schema[CourseListResponse] = Schema.derived[CourseListResponse]

  // Schema derivations for domain types
  given Schema[CourseLevel] = Schema.derivedEnumeration[CourseLevel].defaultStringBased
  given Schema[Course] = Schema.derived[Course]

  /**
   * List all courses
   * GET /api/courses
   */
  val listCoursesEndpoint: PublicEndpoint[Unit, CourseError, CourseListResponse, Any] =
    endpoint.get
      .in("api" / "courses")
      .out(jsonBody[CourseListResponse])
      .errorOut(jsonBody[CourseError])
      .description("List all available courses")
      .tag("Courses")

  /**
   * Get a single course by ID
   * GET /api/courses/:id
   */
  val getCourseEndpoint: PublicEndpoint[String, CourseError, Course, Any] =
    endpoint.get
      .in("api" / "courses" / path[String]("id"))
      .out(jsonBody[Course])
      .errorOut(jsonBody[CourseError])
      .description("Get a course by ID")
      .tag("Courses")
