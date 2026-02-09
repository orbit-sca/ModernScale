package com.brice.http

import com.brice.api.ProjectEndpoints
import com.brice.api.ProjectEndpoints.{ProjectError, ProjectListResponse}
import com.brice.domain.*
import com.brice.services.ProjectService
import sttp.tapir.server.ServerEndpoint
import zio.*

/** Project routes implementation */
object ProjectRoutes:

  /** List all projects with optional filtering */
  def listProjectsRoute(projectService: ProjectService): ServerEndpoint[Any, Task] =
    ProjectEndpoints.listProjectsEndpoint
      .serverLogic { filter =>
        projectService.listProjects(filter)
          .map { projects =>
            Right(ProjectListResponse(projects, projects.length))
          }
          .catchAll { error =>
            ZIO.succeed(Left(ProjectError(
              error = "Failed to list projects",
              details = Some(error.getMessage)
            )))
          }
      }

  /** Get a single project by ID */
  def getProjectRoute(projectService: ProjectService): ServerEndpoint[Any, Task] =
    ProjectEndpoints.getProjectEndpoint
      .serverLogic { id =>
        projectService.getProject(id)
          .map {
            case Some(project) => Right(project)
            case None => Left(ProjectError(
              error = "Project not found",
              details = Some(s"No project with ID: $id")
            ))
          }
          .catchAll { error =>
            ZIO.succeed(Left(ProjectError(
              error = "Failed to get project",
              details = Some(error.getMessage)
            )))
          }
      }

  /** List development projects */
  def listDevelopmentRoute(projectService: ProjectService): ServerEndpoint[Any, Task] =
    ProjectEndpoints.listDevelopmentProjectsEndpoint
      .serverLogic { _ =>
        projectService.listByCategory(ProjectCategory.Development)
          .map { projects =>
            Right(ProjectListResponse(projects, projects.length))
          }
          .catchAll { error =>
            ZIO.succeed(Left(ProjectError(
              error = "Failed to list development projects",
              details = Some(error.getMessage)
            )))
          }
      }

  /** List analytics projects */
  def listAnalyticsRoute(projectService: ProjectService): ServerEndpoint[Any, Task] =
    ProjectEndpoints.listAnalyticsProjectsEndpoint
      .serverLogic { _ =>
        projectService.listByCategory(ProjectCategory.Analytics)
          .map { projects =>
            Right(ProjectListResponse(projects, projects.length))
          }
          .catchAll { error =>
            ZIO.succeed(Left(ProjectError(
              error = "Failed to list analytics projects",
              details = Some(error.getMessage)
            )))
          }
      }

  /** Create all project routes */
  def routes(projectService: ProjectService): List[ServerEndpoint[Any, Task]] = List(
    listDevelopmentRoute(projectService),
    listAnalyticsRoute(projectService),
    listProjectsRoute(projectService),
    getProjectRoute(projectService)
  )
