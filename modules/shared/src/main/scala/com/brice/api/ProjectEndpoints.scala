package com.brice.api

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import zio.json.*
import com.brice.domain.*

/** Project API endpoints - shared between backend and frontend */
object ProjectEndpoints:

  /** Error response for project operations */
  final case class ProjectError(
    error: String,
    details: Option[String] = None
  )

  object ProjectError:
    given JsonCodec[ProjectError] = DeriveJsonCodec.gen[ProjectError]
    given Schema[ProjectError] = Schema.derived[ProjectError]

  /** Response containing a list of projects */
  final case class ProjectListResponse(
    projects: List[Project],
    total: Int
  )

  object ProjectListResponse:
    given JsonCodec[ProjectListResponse] = DeriveJsonCodec.gen[ProjectListResponse]
    given Schema[ProjectListResponse] = Schema.derived[ProjectListResponse]

  // Schema derivations for domain types
  given Schema[ProjectCategory] = Schema.derivedEnumeration[ProjectCategory].defaultStringBased
  given Schema[ProjectStatus] = Schema.derivedEnumeration[ProjectStatus].defaultStringBased
  given Schema[Project] = Schema.derived[Project]
  given Schema[ProjectFilter] = Schema.derived[ProjectFilter]

  /**
   * List all projects with optional filtering
   * GET /api/projects?category=development&status=completed
   */
  val listProjectsEndpoint: PublicEndpoint[ProjectFilter, ProjectError, ProjectListResponse, Any] =
    endpoint.get
      .in("api" / "projects")
      .in(
        query[Option[String]]("category")
          .map(_.flatMap(parseCategory))(_.map(_.toString.toLowerCase))
          .and(
            query[Option[String]]("status")
              .map(_.flatMap(parseStatus))(_.map(_.toString.toLowerCase))
          )
          .mapTo[ProjectFilter]
      )
      .out(jsonBody[ProjectListResponse])
      .errorOut(jsonBody[ProjectError])
      .description("List projects with optional filtering by category and status")
      .tag("Projects")

  /**
   * Get a single project by ID
   * GET /api/projects/:id
   */
  val getProjectEndpoint: PublicEndpoint[String, ProjectError, Project, Any] =
    endpoint.get
      .in("api" / "projects" / path[String]("id"))
      .out(jsonBody[Project])
      .errorOut(jsonBody[ProjectError])
      .description("Get a project by ID")
      .tag("Projects")

  /**
   * List development projects (convenience endpoint)
   * GET /api/projects/development
   */
  val listDevelopmentProjectsEndpoint: PublicEndpoint[Unit, ProjectError, ProjectListResponse, Any] =
    endpoint.get
      .in("api" / "projects" / "category" / "development")
      .out(jsonBody[ProjectListResponse])
      .errorOut(jsonBody[ProjectError])
      .description("List all development projects")
      .tag("Projects")

  /**
   * List analytics projects (convenience endpoint)
   * GET /api/projects/analytics
   */
  val listAnalyticsProjectsEndpoint: PublicEndpoint[Unit, ProjectError, ProjectListResponse, Any] =
    endpoint.get
      .in("api" / "projects" / "category" / "analytics")
      .out(jsonBody[ProjectListResponse])
      .errorOut(jsonBody[ProjectError])
      .description("List all analytics projects")
      .tag("Projects")

  // Helper functions for parsing query params
  private def parseCategory(s: String): Option[ProjectCategory] =
    s.toLowerCase match
      case "development" => Some(ProjectCategory.Development)
      case "analytics"   => Some(ProjectCategory.Analytics)
      case _             => None

  private def parseStatus(s: String): Option[ProjectStatus] =
    s.toLowerCase match
      case "completed"   => Some(ProjectStatus.Completed)
      case "in_progress" | "inprogress" => Some(ProjectStatus.InProgress)
      case "planned"     => Some(ProjectStatus.Planned)
      case _             => None
