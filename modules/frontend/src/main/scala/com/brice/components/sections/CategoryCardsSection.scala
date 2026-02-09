package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveElement
import com.brice.theme.DesignTokens.*
import com.brice.routing.{AppRouter, DevelopmentPage, AnalyticsPage}

/** CTA cards section for navigating to Development and Analytics pages */
object CategoryCardsSection:

  def apply(): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.xxxl} 0;
        background: ${Colors.accent};
        position: relative;
      """,

      // Content container
      div(
        styleAttr := s"""
          max-width: ${Layout.containerMaxWidth};
          margin: 0 auto;
          padding: 0 ${Spacing.lg};
        """,

        // Section header
        div(
          styleAttr := s"text-align: center; margin-bottom: ${Spacing.xxl};",

          div(
            styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
            "> explore ./categories"
          ),

          h2(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xxxl};
              font-weight: ${Typography.FontWeight.bold};
              color: ${Colors.text};
              margin-bottom: ${Spacing.md};
            """,
            "What I ",
            span(styleAttr := s"color: ${Colors.primary};", "Do")
          ),

          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.lg};
              color: ${Colors.textLight};
              max-width: 600px;
              margin: 0 auto;
            """,
            "From building robust software systems to extracting actionable insights from data."
          )
        ),

        // CTA Cards Grid
        div(
          styleAttr := s"""
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: ${Spacing.lg};
            max-width: 900px;
            margin: 0 auto;
          """,

          // Development Card
          categoryCard(
            title = "Development",
            description = "Scala-based systems, microservices, and full-stack engineering projects",
            icon = codeIcon,
            action = "Explore Projects",
            onClickAction = () => AppRouter.navigateTo(DevelopmentPage)
          ),

          // Analytics Card
          categoryCard(
            title = "Data & Analytics",
            description = "Dashboards, pipelines, and data-driven insights with live embeds",
            icon = chartIcon,
            action = "View Analytics",
            onClickAction = () => AppRouter.navigateTo(AnalyticsPage)
          )
        )
      )
    )

  private def categoryCard(
    title: String,
    description: String,
    icon: ReactiveElement.Base,
    action: String,
    onClickAction: () => Unit
  ): HtmlElement =
    div(
      cls := "category-card",
      styleAttr := s"""
        position: relative;
        padding: ${Spacing.xl};
        background: linear-gradient(135deg, ${Colors.cardBackground} 0%, rgba(10, 10, 11, 1) 100%);
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        cursor: pointer;
        transition: all 0.5s ease;
        overflow: hidden;
      """,
      onClick --> { _ => onClickAction() },

      // Hover glow effect
      div(
        styleAttr := s"""
          position: absolute;
          top: 0;
          right: 0;
          width: 128px;
          height: 128px;
          background: radial-gradient(circle, rgba(217, 38, 38, 0.1) 0%, transparent 70%);
          border-radius: 50%;
          filter: blur(40px);
          transition: all 0.5s ease;
          pointer-events: none;
        """
      ),

      // Content
      div(
        styleAttr := "position: relative; z-index: 10;",

        // Icon
        div(
          styleAttr := s"""
            margin-bottom: ${Spacing.md};
            transition: transform 0.3s ease;
          """,
          icon
        ),

        // Title
        h3(
          styleAttr := s"""
            font-size: ${Typography.FontSize.xxl};
            font-weight: ${Typography.FontWeight.medium};
            color: ${Colors.text};
            margin-bottom: ${Spacing.sm};
            letter-spacing: -0.01em;
          """,
          title
        ),

        // Description
        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.base};
            color: ${Colors.textLight};
            line-height: 1.6;
            margin-bottom: ${Spacing.md};
          """,
          description
        ),

        // Action link
        div(
          styleAttr := s"""
            display: flex;
            align-items: center;
            gap: ${Spacing.xs};
            color: ${Colors.primary};
            font-size: ${Typography.FontSize.sm};
            font-weight: ${Typography.FontWeight.medium};
          """,
          span(action),
          arrowIcon
        )
      )
    )

  // Icons
  private def codeIcon: SvgElement =
    svg.svg(
      svg.width := "40",
      svg.height := "40",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.polyline(svg.points := "16 18 22 12 16 6"),
      svg.polyline(svg.points := "8 6 2 12 8 18")
    )

  private def chartIcon: SvgElement =
    svg.svg(
      svg.width := "40",
      svg.height := "40",
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

  private def arrowIcon: SvgElement =
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
