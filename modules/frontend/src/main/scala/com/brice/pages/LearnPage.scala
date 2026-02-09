package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.sections.CourseCard
import com.brice.api.CourseApiClient
import com.brice.domain.*
import scala.concurrent.ExecutionContext.Implicits.global

/** Learn page - showcases available courses */
object LearnPage:

  def apply(): HtmlElement =
    // State for courses and loading
    val coursesVar = Var[List[Course]](List.empty)
    val isLoading = Var(true)
    val errorMessage = Var(Option.empty[String])

    // Fetch courses on mount
    val fetchCourses = Observer[Unit] { _ =>
      isLoading.set(true)
      errorMessage.set(None)

      CourseApiClient.listCourses().foreach {
        case Right(courses) =>
          coursesVar.set(courses)
          isLoading.set(false)
        case Left(error) =>
          errorMessage.set(Some(error.error))
          isLoading.set(false)
      }
    }

    div(
      cls := "learn-page",
      styleAttr := s"min-height: 100vh; background: ${Colors.accent}; color: ${Colors.text};",

      // Trigger fetch on mount
      onMountCallback(_ => fetchCourses.onNext(())),

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
          left: 50%;
          transform: translateX(-50%);
          width: 1000px;
          height: 600px;
          background: radial-gradient(ellipse, rgba(217, 38, 38, 0.15) 0%, transparent 70%);
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
            styleAttr := s"margin-bottom: ${Spacing.xxl}; text-align: center;",

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
              graduationIcon,
              span(
                styleAttr := s"""
                  font-family: ${Typography.monoFamily};
                  font-size: 11px;
                  color: ${Colors.textLight};
                  text-transform: uppercase;
                  letter-spacing: 0.15em;
                """,
                "Build"
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
              "Build Modern Skills",
              span(styleAttr := s"color: ${Colors.primary};", ".")
            ),

            // Description
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xl};
                color: ${Colors.textLight};
                max-width: 600px;
                margin: 0 auto;
                line-height: 1.7;
              """,
              "Level up your engineering capabilities. Master Scala development workflows ",
              "and blockchain literacy through practical, production-ready coursework."
            )
          ),

          // Courses grid
          div(
            cls := "courses-grid",
            styleAttr := s"""
              display: grid;
              grid-template-columns: repeat(auto-fit, minmax(min(100%, 360px), 1fr));
              gap: ${Spacing.xl};
            """,

            // Loading state
            child <-- isLoading.signal.map { loading =>
              if loading then
                div(
                  styleAttr := "text-align: center; padding: 80px 0; grid-column: 1 / -1;",
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
                    grid-column: 1 / -1;
                  """,
                  bookIconLarge,
                  p(
                    styleAttr := s"font-size: ${Typography.FontSize.lg}; margin-top: ${Spacing.md};",
                    s"Error loading courses: $error"
                  )
                )
              case None => emptyNode
            },

            // Courses
            children <-- coursesVar.signal.combineWith(isLoading.signal).map { case (courses, loading) =>
              if !loading && courses.isEmpty then
                List(
                  div(
                    styleAttr := s"""
                      text-align: center;
                      padding: 80px 0;
                      grid-column: 1 / -1;
                    """,
                    bookIconLarge,
                    p(
                      styleAttr := s"font-size: ${Typography.FontSize.lg}; color: ${Colors.textLight}; margin-top: ${Spacing.md};",
                      "Courses coming soon!"
                    )
                  )
                )
              else
                courses.zipWithIndex.map { case (course, index) =>
                  CourseCard(course, index)
                }
            }
          ),

          // Donation notice
          div(
            cls := "glass-card",
            styleAttr := s"""
              margin-top: ${Spacing.xxl};
              padding: ${Spacing.lg} ${Spacing.xl};
              text-align: center;
              border-left: 3px solid ${Colors.primary};
            """,
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.sm};
                color: ${Colors.textLight};
                line-height: 1.7;
                margin: 0;
              """,
              "ModernScale plans to donate 10% of course proceeds to a dedicated ModernScaleSCF donation fund, intended to support Scala language development and the broader Scala ecosystem."
            )
          ),

          // Bottom CTA
          div(
            cls := "glass-card",
            styleAttr := s"""
              margin-top: ${Spacing.xxl};
              padding: ${Spacing.xl} ${Spacing.xxl};
              text-align: center;
              position: relative;
              overflow: hidden;
            """,

            // Subtle gradient background
            div(
              styleAttr := """
                position: absolute;
                inset: 0;
                background: radial-gradient(ellipse at center, rgba(217, 38, 38, 0.08) 0%, transparent 70%);
                pointer-events: none;
              """
            ),

            div(
              styleAttr := "position: relative; z-index: 1;",
              h3(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xxl};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.sm};
                  font-weight: ${Typography.FontWeight.medium};
                """,
                "Want a custom course for your team?"
              ),
              p(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.base};
                  color: ${Colors.textLight};
                  margin-bottom: ${Spacing.xl};
                  max-width: 500px;
                  margin-left: auto;
                  margin-right: auto;
                """,
                "I offer tailored training programs for organizations. Let's discuss your needs."
              ),
              a(
                cls := "btn-secondary",
                href := "/contact",
                styleAttr := s"""
                  display: inline-flex;
                  align-items: center;
                  gap: 8px;
                """,
                "Get in Touch",
                arrowRightIcon
              )
            )
          )
        )
      )
    )

  // Icons
  private def graduationIcon: SvgElement =
    svg.svg(
      svg.width := "12",
      svg.height := "12",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M22 10v6M2 10l10-5 10 5-10 5z"),
      svg.path(svg.d := "M6 12v5c3 3 9 3 12 0v-5")
    )

  private def bookIconLarge: SvgElement =
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
      svg.path(svg.d := "M4 19.5A2.5 2.5 0 0 1 6.5 17H20"),
      svg.path(svg.d := "M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z")
    )

  private def arrowRightIcon: SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.line(svg.x1 := "5", svg.y1 := "12", svg.x2 := "19", svg.y2 := "12"),
      svg.polyline(svg.points := "12 5 19 12 12 19")
    )
