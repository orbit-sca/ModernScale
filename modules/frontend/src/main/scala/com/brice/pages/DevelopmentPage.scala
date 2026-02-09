package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.sections.ProjectCard
import com.brice.api.ProjectApiClient
import com.brice.domain.*
import scala.concurrent.ExecutionContext.Implicits.global

/** Development projects page - showcases software engineering work */
object DevelopmentPage:

  def apply(): HtmlElement =
    // State for projects and loading
    val projectsVar = Var[List[Project]](List.empty)
    val isLoading = Var(true)
    val errorMessage = Var(Option.empty[String])

    // Fetch projects on mount
    val fetchProjects = Observer[Unit] { _ =>
      isLoading.set(true)
      errorMessage.set(None)

      ProjectApiClient.listDevelopmentProjects().foreach {
        case Right(projects) =>
          projectsVar.set(projects)
          isLoading.set(false)
        case Left(error) =>
          errorMessage.set(Some(error.error))
          isLoading.set(false)
      }
    }

    div(
      cls := "development-page",
      styleAttr := s"min-height: 100vh; background: ${Colors.accent}; color: ${Colors.text};",

      // Trigger fetch on mount
      onMountCallback(_ => fetchProjects.onNext(())),

      // Background grid
      div(
        styleAttr := s"""
          position: absolute;
          inset: 0;
          background-image:
            linear-gradient(rgba(217, 38, 38, 0.02) 1px, transparent 1px),
            linear-gradient(90deg, rgba(217, 38, 38, 0.02) 1px, transparent 1px);
          background-size: 64px 64px;
          pointer-events: none;
        """
      ),

      // Glowing orb decoration
      div(
        styleAttr := s"""
          position: absolute;
          top: 0;
          right: 0;
          width: 800px;
          height: 800px;
          background: radial-gradient(circle, rgba(217, 38, 38, 0.1) 0%, transparent 70%);
          pointer-events: none;
        """
      ),

      // Content
      div(
        styleAttr := s"""
          position: relative;
          z-index: 10;
          padding-top: 128px;
          padding-bottom: 96px;
          padding-left: ${Spacing.lg};
          padding-right: ${Spacing.lg};
        """,

        div(
          styleAttr := s"max-width: ${Layout.containerMaxWidth}; margin: 0 auto;",

          // Header
          div(
            styleAttr := s"margin-bottom: ${Spacing.xxl};",

            // Badge
            div(
              styleAttr := s"""
                display: inline-flex;
                align-items: center;
                gap: ${Spacing.xs};
                padding: ${Spacing.xs} ${Spacing.md};
                background: rgba(255, 255, 255, 0.05);
                border: 1px solid rgba(217, 38, 38, 0.3);
                border-radius: ${BorderRadius.pill};
                margin-bottom: ${Spacing.lg};
              """,
              cpuIcon,
              span(
                styleAttr := s"""
                  font-family: ${Typography.monoFamily};
                  font-size: 11px;
                  color: ${Colors.textLight};
                  text-transform: uppercase;
                  letter-spacing: 0.15em;
                """,
                "Development"
              )
            ),

            // Title
            h1(
              styleAttr := s"""
                font-size: clamp(40px, 8vw, ${Typography.FontSize.hero});
                font-weight: ${Typography.FontWeight.regular};
                color: ${Colors.text};
                letter-spacing: -0.02em;
                margin-bottom: ${Spacing.lg};
              """,
              "Software",
              span(styleAttr := s"color: ${Colors.primary};", ".")
            ),

            // Description
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.lg};
                color: ${Colors.textLight};
                max-width: 700px;
                line-height: 1.7;
              """,
              "Full-stack applications built with Scala, ZIO, and Laminar. ",
              "Type-safe APIs, reactive UIs, and functional programming from end to end."
            )
          ),

          // Projects list
          div(
            styleAttr := s"display: grid; gap: ${Spacing.xl};",

            // Loading state
            child <-- isLoading.signal.map { loading =>
              if loading then
                div(
                  styleAttr := "text-align: center; padding: 80px 0;",
                  div(
                    styleAttr := s"""
                      display: inline-block;
                      width: 32px;
                      height: 32px;
                      border: 2px solid rgba(217, 38, 38, 0.2);
                      border-top-color: ${Colors.primary};
                      border-radius: 50%;
                      animation: spin 1s linear infinite;
                    """
                  )
                )
              else emptyNode
            },

            // Error state
            child <-- errorMessage.signal.map {
              case Some(error) =>
                div(
                  styleAttr := s"""
                    text-align: center;
                    padding: 80px 0;
                    color: ${Colors.textLight};
                  """,
                  codeIconLarge,
                  p(
                    styleAttr := s"font-size: ${Typography.FontSize.lg}; margin-top: ${Spacing.md};",
                    s"Error loading projects: $error"
                  )
                )
              case None => emptyNode
            },

            // Projects
            children <-- projectsVar.signal.combineWith(isLoading.signal).map { case (projects, loading) =>
              if !loading && projects.isEmpty then
                List(
                  div(
                    styleAttr := s"""
                      text-align: center;
                      padding: 80px 0;
                    """,
                    codeIconLarge,
                    p(
                      styleAttr := s"font-size: ${Typography.FontSize.lg}; color: ${Colors.textLight}; margin-top: ${Spacing.md};",
                      "No development projects yet."
                    )
                  )
                )
              else
                projects.zipWithIndex.map { case (project, index) =>
                  ProjectCard(project, index)
                }
            }
          )
        )
      )
    )

  // Icons
  private def cpuIcon: SvgElement =
    svg.svg(
      svg.width := "12",
      svg.height := "12",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.rect(svg.x := "4", svg.y := "4", svg.width := "16", svg.height := "16", svg.rx := "2"),
      svg.rect(svg.x := "9", svg.y := "9", svg.width := "6", svg.height := "6"),
      svg.line(svg.x1 := "9", svg.y1 := "1", svg.x2 := "9", svg.y2 := "4"),
      svg.line(svg.x1 := "15", svg.y1 := "1", svg.x2 := "15", svg.y2 := "4"),
      svg.line(svg.x1 := "9", svg.y1 := "20", svg.x2 := "9", svg.y2 := "23"),
      svg.line(svg.x1 := "15", svg.y1 := "20", svg.x2 := "15", svg.y2 := "23"),
      svg.line(svg.x1 := "20", svg.y1 := "9", svg.x2 := "23", svg.y2 := "9"),
      svg.line(svg.x1 := "20", svg.y1 := "14", svg.x2 := "23", svg.y2 := "14"),
      svg.line(svg.x1 := "1", svg.y1 := "9", svg.x2 := "4", svg.y2 := "9"),
      svg.line(svg.x1 := "1", svg.y1 := "14", svg.x2 := "4", svg.y2 := "14")
    )

  private def codeIconLarge: SvgElement =
    svg.svg(
      svg.width := "64",
      svg.height := "64",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.lightGray,
      svg.strokeWidth := "1",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.style := "margin: 0 auto; display: block;",
      svg.polyline(svg.points := "16 18 22 12 16 6"),
      svg.polyline(svg.points := "8 6 2 12 8 18")
    )
