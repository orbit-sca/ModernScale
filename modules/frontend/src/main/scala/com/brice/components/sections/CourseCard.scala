package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.domain.*

/** Course card component for displaying individual courses */
object CourseCard:

  /** Level configuration for visual styling */
  private case class LevelConfig(label: String, color: String)

  private val levelConfigs: Map[CourseLevel, LevelConfig] = Map(
    CourseLevel.Beginner -> LevelConfig("Beginner", "#10b981"),
    CourseLevel.Intermediate -> LevelConfig("Intermediate", "#f59e0b"),
    CourseLevel.Advanced -> LevelConfig("Advanced", Colors.primary)
  )

  def apply(course: Course, index: Int = 0): HtmlElement =
    val levelConfig = levelConfigs.getOrElse(course.level, LevelConfig("All Levels", Colors.mediumGray))
    val hasDiscount = course.originalPrice.isDefined

    articleTag(
      cls := "course-card",
      styleAttr := s"""
        animation: fadeInUp 0.6s ease ${index * 0.15}s both;
      """,

      // Course image placeholder with gradient
      div(
        cls := "course-card-image",
        styleAttr := s"""
          height: 200px;
          background: linear-gradient(135deg, rgba(217, 38, 38, 0.2) 0%, rgba(59, 130, 246, 0.1) 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          border-bottom: 1px solid ${Colors.glassBorder};
          position: relative;
        """,

        // Course icon
        playCircleIcon,

        // Level badge
        div(
          styleAttr := s"""
            position: absolute;
            top: ${Spacing.md};
            left: ${Spacing.md};
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            background: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(8px);
            border-radius: ${BorderRadius.pill};
            font-size: 12px;
            color: ${levelConfig.color};
            font-weight: 500;
          """,
          levelIcon(levelConfig.color),
          levelConfig.label
        ),

        // Price badge
        div(
          styleAttr := s"""
            position: absolute;
            top: ${Spacing.md};
            right: ${Spacing.md};
            padding: 8px 16px;
            background: ${Colors.primary};
            border-radius: ${BorderRadius.md};
            font-weight: 600;
          """,
          if hasDiscount then
            div(
              styleAttr := "display: flex; align-items: center; gap: 8px;",
              span(
                styleAttr := "text-decoration: line-through; opacity: 0.7; font-size: 14px;",
                s"$$${course.originalPrice.get.toInt}"
              ),
              span(
                styleAttr := "font-size: 18px;",
                s"$$${course.price.toInt}"
              )
            )
          else
            span(
              styleAttr := "font-size: 18px;",
              s"$$${course.price.toInt}"
            )
        )
      ),

      // Content
      div(
        styleAttr := s"padding: ${Spacing.xl};",

        // Title
        h3(
          styleAttr := s"""
            font-size: ${Typography.FontSize.xxl};
            font-weight: ${Typography.FontWeight.medium};
            color: ${Colors.text};
            letter-spacing: -0.01em;
            margin-bottom: ${Spacing.xs};
          """,
          course.title
        ),

        // Subtitle
        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.base};
            color: ${Colors.primary};
            margin-bottom: ${Spacing.md};
          """,
          course.subtitle
        ),

        // Description
        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.sm};
            color: ${Colors.textLight};
            line-height: 1.7;
            margin-bottom: ${Spacing.lg};
          """,
          course.description.take(200) + (if course.description.length > 200 then "..." else "")
        ),

        // Course meta (duration, lessons)
        div(
          styleAttr := s"""
            display: flex;
            gap: ${Spacing.lg};
            margin-bottom: ${Spacing.lg};
            padding-bottom: ${Spacing.md};
            border-bottom: 1px solid ${Colors.glassBorder};
          """,
          course.duration.map { duration =>
            div(
              styleAttr := s"display: flex; align-items: center; gap: 8px; color: ${Colors.textLight}; font-size: ${Typography.FontSize.sm};",
              clockIcon,
              span(duration)
            )
          }.getOrElse(emptyNode),
          course.lessons.map { lessons =>
            div(
              styleAttr := s"display: flex; align-items: center; gap: 8px; color: ${Colors.textLight}; font-size: ${Typography.FontSize.sm};",
              bookIcon,
              span(s"$lessons lessons")
            )
          }.getOrElse(emptyNode)
        ),

        // Topics
        if course.topics.nonEmpty then
          div(
            styleAttr := s"margin-bottom: ${Spacing.lg};",
            p(
              styleAttr := s"""
                font-size: 11px;
                text-transform: uppercase;
                letter-spacing: 0.1em;
                color: ${Colors.mediumGray};
                margin-bottom: ${Spacing.sm};
                font-family: ${Typography.monoFamily};
              """,
              "What you'll learn"
            ),
            ul(
              styleAttr := "list-style: none; padding: 0; margin: 0;",
              course.topics.take(4).map { topic =>
                li(
                  styleAttr := s"""
                    display: flex;
                    align-items: flex-start;
                    gap: ${Spacing.xs};
                    color: ${Colors.textLight};
                    font-size: ${Typography.FontSize.sm};
                    margin-bottom: 6px;
                  """,
                  checkIcon,
                  span(topic)
                )
              },
              if course.topics.length > 4 then
                li(
                  styleAttr := s"""
                    color: ${Colors.primary};
                    font-size: ${Typography.FontSize.sm};
                    margin-top: ${Spacing.xs};
                  """,
                  s"+ ${course.topics.length - 4} more topics"
                )
              else emptyNode
            )
          )
        else emptyNode,

        // Coming Soon button
        div(
          cls := "coming-soon-btn",
          styleAttr := s"""
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            width: 100%;
            padding: ${Spacing.md} ${Spacing.lg};
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid ${Colors.glassBorder};
            border-radius: ${BorderRadius.md};
            color: ${Colors.textLight};
            font-size: ${Typography.FontSize.base};
            font-weight: 500;
            text-decoration: none;
            cursor: not-allowed;
            opacity: 0.7;
          """,
          span("Coming Soon")
        )
      )
    )

  // SVG Icons
  private def playCircleIcon: SvgElement =
    svg.svg(
      svg.width := "80",
      svg.height := "80",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "rgba(255, 255, 255, 0.2)",
      svg.strokeWidth := "1",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.circle(svg.cx := "12", svg.cy := "12", svg.r := "10"),
      svg.polygon(svg.points := "10 8 16 12 10 16 10 8")
    )

  private def levelIcon(color: String): SvgElement =
    svg.svg(
      svg.width := "14",
      svg.height := "14",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := color,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.line(svg.x1 := "18", svg.y1 := "20", svg.x2 := "18", svg.y2 := "10"),
      svg.line(svg.x1 := "12", svg.y1 := "20", svg.x2 := "12", svg.y2 := "4"),
      svg.line(svg.x1 := "6", svg.y1 := "20", svg.x2 := "6", svg.y2 := "14")
    )

  private def clockIcon: SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.circle(svg.cx := "12", svg.cy := "12", svg.r := "10"),
      svg.polyline(svg.points := "12 6 12 12 16 14")
    )

  private def bookIcon: SvgElement =
    svg.svg(
      svg.width := "16",
      svg.height := "16",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M4 19.5A2.5 2.5 0 0 1 6.5 17H20"),
      svg.path(svg.d := "M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z")
    )

  private def checkIcon: SvgElement =
    svg.svg(
      svg.width := "14",
      svg.height := "14",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.polyline(svg.points := "20 6 9 17 4 12")
    )

  private def arrowRightIcon: SvgElement =
    svg.svg(
      svg.width := "18",
      svg.height := "18",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.line(svg.x1 := "5", svg.y1 := "12", svg.x2 := "19", svg.y2 := "12"),
      svg.polyline(svg.points := "12 5 19 12 12 19")
    )
