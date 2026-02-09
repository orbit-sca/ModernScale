package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.domain.*

/** Project card component for displaying individual projects */
object ProjectCard:

  /** Status configuration for visual styling */
  private case class StatusConfig(label: String, color: String)

  private val statusConfigs: Map[ProjectStatus, StatusConfig] = Map(
    ProjectStatus.Completed -> StatusConfig("Completed", "#10b981"),
    ProjectStatus.InProgress -> StatusConfig("In Development", "#f59e0b"),
    ProjectStatus.Planned -> StatusConfig("Planned", Colors.mediumGray)
  )

  def apply(project: Project, index: Int = 0): HtmlElement =
    val isDev = project.category == ProjectCategory.Development
    val statusConfig = statusConfigs.getOrElse(project.status, StatusConfig("Unknown", Colors.mediumGray))

    articleTag(
      cls := "project-card",
      styleAttr := s"""
        position: relative;
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        overflow: hidden;
        transition: all 0.3s ease;
        animation: fadeInUp 0.6s ease ${index * 0.1}s both;
      """,

      // Embedded Analytics (for analytics projects with embed URL)
      if !isDev && project.embedUrl.isDefined then
        div(
          styleAttr := s"background: rgba(10, 10, 11, 0.8); border-bottom: 1px solid ${Colors.glassBorder};",

          // Embed header
          div(
            styleAttr := s"""
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: ${Spacing.md};
              background: rgba(255, 255, 255, 0.03);
              border-bottom: 1px solid rgba(255, 255, 255, 0.05);
            """,
            p(
              styleAttr := s"""
                font-size: 11px;
                text-transform: uppercase;
                letter-spacing: 0.1em;
                color: ${Colors.primary};
                font-weight: 500;
                font-family: ${Typography.monoFamily};
              """,
              "Live Dashboard"
            ),
            project.embedUrl.map { url =>
              a(
                href := url,
                target := "_blank",
                rel := "noopener noreferrer",
                styleAttr := s"color: ${Colors.textLight}; transition: color 0.2s;",
                externalLinkIcon
              )
            }
          ),

          // Iframe
          div(
            styleAttr := "aspect-ratio: 16/9; background: rgb(2, 6, 23);",
            iframe(
              src := project.embedUrl.getOrElse(""),
              styleAttr := "width: 100%; height: 100%; border: none;"
            )
          )
        )
      else emptyNode,

      // Project Image (for dev projects with image)
      if isDev && project.imageUrl.isDefined then
        div(
          styleAttr := s"aspect-ratio: 16/9; overflow: hidden; background: ${Colors.lightGray};",
          img(
            src := project.imageUrl.getOrElse(""),
            alt := project.title,
            styleAttr := "width: 100%; height: 100%; object-fit: cover; opacity: 0.8; transition: all 0.5s;"
          )
        )
      else emptyNode,

      // Content
      div(
        styleAttr := s"padding: ${Spacing.xl};",

        // Header with icon and status
        div(
          styleAttr := s"margin-bottom: ${Spacing.lg};",

          div(
            styleAttr := s"display: flex; align-items: center; gap: ${Spacing.sm}; margin-bottom: ${Spacing.sm};",

            // Category icon
            div(
              styleAttr := s"""
                padding: 10px;
                border-radius: ${BorderRadius.md};
                background: ${if isDev then "rgba(59, 130, 246, 0.1)" else "rgba(217, 38, 38, 0.1)"};
              """,
              if isDev then codeIcon else chartIcon
            ),

            // Status indicator
            div(
              styleAttr := s"""
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 13px;
                color: ${statusConfig.color};
              """,
              statusIcon(statusConfig.color),
              span(styleAttr := "font-weight: 500;", statusConfig.label)
            )
          ),

          // Title
          h3(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xxl};
              font-weight: ${Typography.FontWeight.medium};
              color: ${Colors.text};
              letter-spacing: -0.01em;
            """,
            project.title
          )
        ),

        // Summary
        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.base};
            color: ${Colors.textLight};
            line-height: 1.7;
            margin-bottom: ${Spacing.lg};
          """,
          project.summary
        ),

        // Tech Stack
        if project.techStack.nonEmpty then
          div(
            styleAttr := s"display: flex; flex-wrap: wrap; gap: ${Spacing.xs}; margin-bottom: ${Spacing.lg};",
            project.techStack.map(techBadge)
          )
        else emptyNode,

        // Architecture Notes (for dev projects)
        project.architectureNotes.map { notes =>
          div(
            styleAttr := s"""
              margin-bottom: ${Spacing.lg};
              padding: ${Spacing.md};
              background: rgba(255, 255, 255, 0.03);
              border-radius: ${BorderRadius.md};
              border: 1px solid rgba(255, 255, 255, 0.05);
            """,
            p(
              styleAttr := s"""
                font-size: 11px;
                text-transform: uppercase;
                letter-spacing: 0.1em;
                color: ${Colors.mediumGray};
                margin-bottom: ${Spacing.xs};
                font-family: ${Typography.monoFamily};
              """,
              "Architecture"
            ),
            p(
              styleAttr := s"font-size: ${Typography.FontSize.sm}; color: ${Colors.textLight}; line-height: 1.6;",
              notes
            )
          )
        }.getOrElse(emptyNode),

        // Key Insights (for analytics projects)
        if !isDev && project.keyInsights.nonEmpty then
          div(
            styleAttr := s"""
              margin-bottom: ${Spacing.lg};
              padding: ${Spacing.lg};
              background: rgba(217, 38, 38, 0.05);
              border-radius: ${BorderRadius.lg};
              border: 1px solid rgba(217, 38, 38, 0.1);
            """,
            p(
              styleAttr := s"""
                font-size: 11px;
                text-transform: uppercase;
                letter-spacing: 0.1em;
                color: ${Colors.primary};
                font-weight: 500;
                margin-bottom: ${Spacing.md};
                font-family: ${Typography.monoFamily};
              """,
              "Impact & Insights"
            ),
            ul(
              styleAttr := "list-style: none; padding: 0; margin: 0;",
              project.keyInsights.map { insight =>
                li(
                  styleAttr := s"""
                    display: flex;
                    align-items: flex-start;
                    gap: ${Spacing.sm};
                    color: ${Colors.textLight};
                    margin-bottom: ${Spacing.sm};
                  """,
                  span(styleAttr := s"color: ${Colors.primary}; font-size: 18px; line-height: 1;", "→"),
                  span(styleAttr := "line-height: 1.6;", insight)
                )
              }
            )
          )
        else emptyNode,

        // Action buttons
        div(
          styleAttr := s"display: flex; flex-wrap: wrap; gap: ${Spacing.sm};",

          project.githubUrl.map { url =>
            a(
              href := url,
              target := "_blank",
              rel := "noopener noreferrer",
              styleAttr := s"""
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: ${Spacing.sm} ${Spacing.md};
                background: transparent;
                border: 1px solid ${Colors.glassBorder};
                border-radius: ${BorderRadius.md};
                color: ${Colors.textLight};
                font-size: ${Typography.FontSize.sm};
                text-decoration: none;
                transition: all 0.2s;
              """,
              githubIcon,
              span("View Code")
            )
          }.getOrElse(emptyNode),

          project.liveUrl.map { url =>
            a(
              href := url,
              target := "_blank",
              rel := "noopener noreferrer",
              styleAttr := s"""
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: ${Spacing.sm} ${Spacing.md};
                background: ${Colors.primary};
                border: none;
                border-radius: ${BorderRadius.md};
                color: ${Colors.text};
                font-size: ${Typography.FontSize.sm};
                text-decoration: none;
                transition: all 0.2s;
              """,
              externalLinkIcon,
              span(if isDev then "Live App" else "Open Dashboard")
            )
          }.getOrElse(emptyNode)
        )
      )
    )

  // Helper components
  private def techBadge(tech: String): HtmlElement =
    span(
      styleAttr := s"""
        font-family: ${Typography.monoFamily};
        font-size: 11px;
        padding: 6px 10px;
        background: rgba(255, 255, 255, 0.05);
        color: ${Colors.textLight};
        border-radius: ${BorderRadius.sm};
        border: 1px solid ${Colors.glassBorder};
        text-transform: uppercase;
        letter-spacing: 0.5px;
      """,
      tech
    )

  // SVG Icons
  private def codeIcon: SvgElement =
    svg.svg(
      svg.width := "20",
      svg.height := "20",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "#3b82f6",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.polyline(svg.points := "16 18 22 12 16 6"),
      svg.polyline(svg.points := "8 6 2 12 8 18")
    )

  private def chartIcon: SvgElement =
    svg.svg(
      svg.width := "20",
      svg.height := "20",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.line(svg.x1 := "18", svg.y1 := "20", svg.x2 := "18", svg.y2 := "10"),
      svg.line(svg.x1 := "12", svg.y1 := "20", svg.x2 := "12", svg.y2 := "4"),
      svg.line(svg.x1 := "6", svg.y1 := "20", svg.x2 := "6", svg.y2 := "14")
    )

  private def statusIcon(color: String): SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := color,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M22 11.08V12a10 10 0 1 1-5.93-9.14"),
      svg.polyline(svg.points := "22 4 12 14.01 9 11.01")
    )

  private def githubIcon: SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22")
    )

  private def externalLinkIcon: SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"),
      svg.polyline(svg.points := "15 3 21 3 21 9"),
      svg.line(svg.x1 := "10", svg.y1 := "14", svg.x2 := "21", svg.y2 := "3")
    )
