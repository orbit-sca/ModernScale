package com.brice.api

import com.brice.api.ProjectEndpoints.{ProjectError, ProjectListResponse}
import com.brice.domain.*
import org.scalajs.dom
import org.scalajs.dom.HttpMethod
import scala.scalajs.js
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import zio.json.*

/** API client for project endpoints */
object ProjectApiClient:

  /** Fetch all projects with optional filtering */
  def listProjects(category: Option[ProjectCategory] = None, status: Option[ProjectStatus] = None): Future[Either[ProjectError, List[Project]]] =
    val params = List(
      category.map(c => s"category=${c.toString.toLowerCase}"),
      status.map(s => s"status=${s.toString.toLowerCase}")
    ).flatten.mkString("&")

    val url = if params.isEmpty then "/api/projects" else s"/api/projects?$params"

    dom.fetch(url)
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[ProjectListResponse] match
          case Right(response) => Right(response.projects)
          case Left(error) => Left(ProjectError(s"Failed to parse response: $error"))
      }
      .recover { case error =>
        Left(ProjectError("Network error", Some(error.getMessage)))
      }

  /** Fetch development projects */
  def listDevelopmentProjects(): Future[Either[ProjectError, List[Project]]] =
    dom.fetch("/api/projects/category/development")
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[ProjectListResponse] match
          case Right(response) => Right(response.projects)
          case Left(error) => Left(ProjectError(s"Failed to parse response: $error"))
      }
      .recover { case error =>
        Left(ProjectError("Network error", Some(error.getMessage)))
      }

  /** Fetch analytics projects */
  def listAnalyticsProjects(): Future[Either[ProjectError, List[Project]]] =
    dom.fetch("/api/projects/category/analytics")
      .toFuture
      .flatMap(_.text().toFuture)
      .map { body =>
        body.fromJson[ProjectListResponse] match
          case Right(response) => Right(response.projects)
          case Left(error) => Left(ProjectError(s"Failed to parse response: $error"))
      }
      .recover { case error =>
        Left(ProjectError("Network error", Some(error.getMessage)))
      }

  /** Fetch a single project by ID */
  def getProject(id: String): Future[Either[ProjectError, Project]] =
    dom.fetch(s"/api/projects/$id")
      .toFuture
      .flatMap { response =>
        response.text().toFuture.map { body =>
          if response.ok then
            body.fromJson[Project] match
              case Right(project) => Right(project)
              case Left(error) => Left(ProjectError(s"Failed to parse response: $error"))
          else
            body.fromJson[ProjectError] match
              case Right(errorResponse) => Left(errorResponse)
              case Left(_) => Left(ProjectError(s"Server error: ${response.status}"))
        }
      }
      .recover { case error =>
        Left(ProjectError("Network error", Some(error.getMessage)))
      }
