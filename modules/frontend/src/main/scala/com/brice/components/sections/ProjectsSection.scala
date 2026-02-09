package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.components.common.Directives.*

object ProjectsSection {

  case class Project(
    title: String,
    description: String,
    tags: List[String],
    link: Option[String] = None
  )

  val projects = List(
    Project(
      "Kember.co",
      "Real estate startup website built with Scala.js, Laminar, and ZIO HTTP. Features contact form with email integration, deployed on Railway.",
      List("Scala.js", "Laminar", "ZIO", "Railway"),
      link = Some("https://kember.co")
    ),
    Project(
      "Brice.solutions",
      "This portfolio site - a dark-themed, Data Vader inspired showcase built with functional Scala and modern web tech.",
      List("Scala.js", "Laminar", "CSS3", "Resend")
    ),
    Project(
      "Project Alpha",
      "A full-stack application demonstrating functional programming patterns with ZIO and Tapir for type-safe APIs.",
      List("ZIO", "Tapir", "PostgreSQL", "Docker")
    )
  )

  def apply(): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.xxxl} 0;
        background: ${Colors.accent};
        position: relative;
      """,

      // Subtle grid background
      div(
        styleAttr := s"""
          position: absolute;
          inset: 0;
          background-image:
            linear-gradient(rgba(217, 38, 38, 0.02) 1px, transparent 1px),
            linear-gradient(90deg, rgba(217, 38, 38, 0.02) 1px, transparent 1px);
          background-size: 60px 60px;
          pointer-events: none;
        """
      ),

      section(
        div(
          styleAttr := "position: relative; z-index: 1;",

          // Section header
          div(
            styleAttr := s"text-align: center; margin-bottom: ${Spacing.xxl};",

            // Terminal style header
            div(
              styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
              "> ls ./projects"
            ),

            h2(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xxxl};
                font-weight: ${Typography.FontWeight.bold};
                color: ${Colors.text};
                margin-bottom: ${Spacing.md};
              """,
              "Featured ",
              span(styleAttr := s"color: ${Colors.primary};", "Projects")
            ),

            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.lg};
                color: ${Colors.textLight};
                max-width: 600px;
                margin: 0 auto;
              """,
              "A selection of work showcasing functional programming, clean architecture, and modern web development."
            )
          ),

          // Projects grid
          div(
            cls := "projects-grid",
            styleAttr := s"""
              display: grid;
              grid-template-columns: 1fr;
              gap: ${Spacing.lg};
            """,

            projects.zipWithIndex.map { case (project, index) =>
              projectCard(project, index * 0.1)
            }
          )
        )
      )
    )

  private def projectCard(project: Project, delay: Double): HtmlElement =
    div(
      cls := "project-card glass-card",
      animateOnScroll(delaySeconds = delay, initialYOffsetPx = 30),
      styleAttr := s"""
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        padding: ${Spacing.xl};
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
      """,

      // Top red line accent (appears on hover via CSS)
      div(
        styleAttr := s"""
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, transparent, ${Colors.primary}, transparent);
          opacity: 0;
          transition: opacity 0.3s ease;
        """
      ),

      // Project number
      div(
        styleAttr := s"""
          font-family: ${Typography.monoFamily};
          font-size: 12px;
          color: ${Colors.mediumGray};
          margin-bottom: ${Spacing.md};
        """,
        s"// project_0${projects.indexOf(project) + 1}"
      ),

      // Title
      h3(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xl};
          font-weight: ${Typography.FontWeight.bold};
          color: ${Colors.text};
          margin-bottom: ${Spacing.sm};
        """,
        project.title
      ),

      // Description
      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.textLight};
          line-height: 1.7;
          margin-bottom: ${Spacing.lg};
        """,
        project.description
      ),

      // Tags
      div(
        styleAttr := s"display: flex; flex-wrap: wrap; gap: ${Spacing.xs}; margin-bottom: ${Spacing.lg};",
        project.tags.map(tag =>
          span(
            styleAttr := s"""
              font-family: ${Typography.monoFamily};
              font-size: 11px;
              padding: 4px 8px;
              background: rgba(217, 38, 38, 0.1);
              color: ${Colors.primary};
              border-radius: ${BorderRadius.sm};
              text-transform: uppercase;
              letter-spacing: 0.5px;
            """,
            tag
          )
        )
      ),

      // View Site button (only if link exists)
      project.link.map { url =>
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
          span("Visit Site →")
        )
      }.getOrElse(emptyNode)
    )
}
