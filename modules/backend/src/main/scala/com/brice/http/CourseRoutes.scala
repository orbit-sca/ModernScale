package com.brice.http

import com.brice.api.CourseEndpoints
import com.brice.api.CourseEndpoints.{CourseError, CourseListResponse}
import com.brice.services.CourseService
import sttp.tapir.server.ServerEndpoint
import zio.*

/** Course routes implementation */
object CourseRoutes:

  /** List all courses */
  def listCoursesRoute(courseService: CourseService): ServerEndpoint[Any, Task] =
    CourseEndpoints.listCoursesEndpoint
      .serverLogic { _ =>
        courseService.listCourses
          .map { courses =>
            Right(CourseListResponse(courses, courses.length))
          }
          .catchAll { error =>
            ZIO.succeed(Left(CourseError(
              error = "Failed to list courses",
              details = Some(error.getMessage)
            )))
          }
      }

  /** Get a single course by ID */
  def getCourseRoute(courseService: CourseService): ServerEndpoint[Any, Task] =
    CourseEndpoints.getCourseEndpoint
      .serverLogic { id =>
        courseService.getCourse(id)
          .map {
            case Some(course) => Right(course)
            case None => Left(CourseError(
              error = "Course not found",
              details = Some(s"No course with ID: $id")
            ))
          }
          .catchAll { error =>
            ZIO.succeed(Left(CourseError(
              error = "Failed to get course",
              details = Some(error.getMessage)
            )))
          }
      }

  /** Create all course routes */
  def routes(courseService: CourseService): List[ServerEndpoint[Any, Task]] = List(
    listCoursesRoute(courseService),
    getCourseRoute(courseService)
  )
