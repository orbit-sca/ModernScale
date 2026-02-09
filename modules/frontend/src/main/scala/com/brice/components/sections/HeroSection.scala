package com.brice.components.sections

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.routing.{AppRouter, ContactPage, DevelopmentPage}

object HeroSection {

  def apply(): HtmlElement =
    div(
      cls := "hero-section-wrapper",
      styleAttr :=
        s"""
        position: relative;
        min-height: 100vh;
        display: flex;
        align-items: center;
        overflow: hidden;
        background: ${Colors.accent};
        padding-top: 80px;
      """,

      // Data grid background
      div(
        styleAttr :=
          s"""
          position: absolute;
          inset: 0;
          background-image:
            linear-gradient(rgba(217, 38, 38, 0.03) 1px, transparent 1px),
            linear-gradient(90deg, rgba(217, 38, 38, 0.03) 1px, transparent 1px);
          background-size: 60px 60px;
          mask-image: radial-gradient(ellipse at center, black 30%, transparent 80%);
          pointer-events: none;
        """
      ),

      // Glowing orb decoration
      div(styleAttr := s"position: absolute; top: 20%; right: 10%; width: 400px; height: 400px; background: radial-gradient(circle, rgba(217, 38, 38, 0.15) 0%, transparent 70%); pointer-events: none;"),
      div(styleAttr := s"position: absolute; bottom: 10%; left: 5%; width: 300px; height: 300px; background: radial-gradient(circle, rgba(217, 38, 38, 0.1) 0%, transparent 70%); pointer-events: none;"),

      // Content
      section(
        div(
          cls := "hero-grid",
          styleAttr := "position: relative; z-index: 1;",

          // Left column - Main content
          div(
            // Status indicator
            div(
              styleAttr :=
                s"""
                display: inline-flex;
                align-items: center;
                gap: ${Spacing.sm};
                padding: ${Spacing.xs} ${Spacing.md};
                background: ${Colors.glassBackground};
                border: 1px solid ${Colors.glassBorder};
                border-radius: ${BorderRadius.pill};
                margin-bottom: ${Spacing.lg};
              """,
              // Pulsing dot
              div(
                cls := "status-dot",
                styleAttr :=
                  s"""
                  width: 8px;
                  height: 8px;
                  border-radius: 50%;
                  background: ${Colors.primary};
                  box-shadow: 0 0 10px rgba(217, 38, 38, 0.5);
                  animation: pulse 2s ease-in-out infinite;
                """
              ),
              span(
                styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 12px; color: ${Colors.textLight}; text-transform: uppercase; letter-spacing: 1px;",
                "Available for Work"
              )
            ),

            // Location tag
            div(
              styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
              "> Tampa, FL"
            ),

            // Main headline
            h1(
              styleAttr :=
                s"""
                font-size: ${Typography.FontSize.hero};
                font-weight: ${Typography.FontWeight.bold};
                line-height: ${Typography.LineHeight.tight};
                margin-bottom: ${Spacing.lg};
                letter-spacing: -0.02em;
              """,
              span(styleAttr := s"color: ${Colors.text};", "Brice "),
              span(styleAttr := s"color: ${Colors.primary}; text-shadow: 0 0 30px rgba(217, 38, 38, 0.5);", "Richmond"),
              br(),
              span(styleAttr := s"color: ${Colors.textLight};", "Data & "),
              span(styleAttr := s"color: ${Colors.text};", "Software Engineer")
            ),

            // Description - honest and technical
            p(
              styleAttr :=
                s"""
                font-size: ${Typography.FontSize.lg};
                color: ${Colors.textLight};
                max-width: 520px;
                margin-bottom: ${Spacing.xl};
                line-height: 1.7;
              """,
              "Building full-stack applications with Scala, ZIO, and functional programming. B.S. Data Analytics student focused on turning raw data into actionable systems."
            ),

            // CTA Buttons
            div(
              cls := "hero-buttons",
              styleAttr := s"display: flex; gap: ${Spacing.md}; flex-wrap: wrap;",
              // Primary button
              button(
                cls := "btn-primary",
                styleAttr :=
                  s"""
                  background: ${Colors.primary};
                  color: ${Colors.text};
                  padding: ${Spacing.md} ${Spacing.xl};
                  border-radius: ${BorderRadius.md};
                  font-family: ${Typography.fontFamily};
                  font-weight: ${Typography.FontWeight.semibold};
                  font-size: 14px;
                  border: none;
                  cursor: pointer;
                  transition: all 0.2s ease;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                """,
                onClick --> { _ => AppRouter.navigateTo(ContactPage) },
                "Get In Touch"
              ),
              // Secondary button
              button(
                cls := "btn-secondary",
                styleAttr :=
                  s"""
                  background: transparent;
                  color: ${Colors.text};
                  padding: ${Spacing.md} ${Spacing.xl};
                  border-radius: ${BorderRadius.md};
                  font-family: ${Typography.fontFamily};
                  font-weight: ${Typography.FontWeight.semibold};
                  font-size: 14px;
                  border: 1px solid ${Colors.glassBorder};
                  cursor: pointer;
                  transition: all 0.2s ease;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                """,
                onClick --> { _ => AppRouter.navigateTo(DevelopmentPage) },
                "View Projects"
              ),
              // GitHub link
              a(
                href := "https://github.com/orbit-sca",
                target := "_blank",
                rel := "noopener noreferrer",
                styleAttr :=
                  s"""
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  width: 48px;
                  height: 48px;
                  background: transparent;
                  border: 1px solid ${Colors.glassBorder};
                  border-radius: ${BorderRadius.md};
                  color: ${Colors.textLight};
                  transition: all 0.2s ease;
                """,
                githubIcon
              )
            ),

            // Tech stack badges - only verifiable skills
            div(
              styleAttr := s"margin-top: ${Spacing.xxl}; display: flex; flex-wrap: wrap; gap: ${Spacing.sm};",
              techBadge("Scala"),
              techBadge("ZIO"),
              techBadge("Laminar"),
              techBadge("SQL"),
              techBadge("PostgreSQL")
            )
          ),

          // Right column - Profile Picture
          div(
            cls := "hero-image-column",
            styleAttr := s"position: relative; display: flex; justify-content: center; align-items: center;",

            // Profile picture container with glow effect
            div(
              cls := "profile-picture-wrapper",
              styleAttr :=
                s"""
                position: relative;
                width: 380px;
                height: 380px;
              """,

              // Outer glow ring
              div(
                styleAttr :=
                  s"""
                  position: absolute;
                  inset: -4px;
                  border-radius: 50%;
                  background: conic-gradient(
                    from 0deg,
                    transparent 0deg,
                    ${Colors.primary} 60deg,
                    transparent 120deg,
                    ${Colors.primary} 180deg,
                    transparent 240deg,
                    ${Colors.primary} 300deg,
                    transparent 360deg
                  );
                  animation: spin 8s linear infinite;
                  opacity: 0.6;
                """
              ),

              // Inner glow
              div(
                styleAttr :=
                  s"""
                  position: absolute;
                  inset: 0;
                  border-radius: 50%;
                  box-shadow:
                    0 0 60px rgba(217, 38, 38, 0.3),
                    0 0 100px rgba(217, 38, 38, 0.2),
                    inset 0 0 60px rgba(217, 38, 38, 0.1);
                """
              ),

              // Profile image container
              div(
                styleAttr :=
                  s"""
                  position: absolute;
                  inset: 6px;
                  border-radius: 50%;
                  overflow: hidden;
                  border: 3px solid ${Colors.glassBorder};
                  background: ${Colors.cardBackground};
                """,

                img(
                  src := "/public/brice1.png",
                  alt := "Brice Richmond",
                  styleAttr :=
                    s"""
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    object-position: center top;
                    filter: grayscale(10%) contrast(1.05);
                    transition: all 0.3s ease;
                  """
                )
              ),

              // Status badge overlay
              div(
                styleAttr :=
                  s"""
                  position: absolute;
                  bottom: 20px;
                  right: 20px;
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  padding: 8px 16px;
                  background: rgba(10, 10, 11, 0.9);
                  backdrop-filter: blur(10px);
                  border: 1px solid ${Colors.glassBorder};
                  border-radius: ${BorderRadius.pill};
                  z-index: 10;
                """,
                div(
                  styleAttr :=
                    s"""
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    background: #10b981;
                    box-shadow: 0 0 10px rgba(16, 185, 129, 0.5);
                    animation: pulse 2s ease-in-out infinite;
                  """
                ),
                span(
                  styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 11px; color: ${Colors.textLight}; text-transform: uppercase; letter-spacing: 1px;",
                  "WGU '27"
                )
              )
            )
          )
        )
      )
    )

  private def techBadge(text: String): HtmlElement =
    span(
      styleAttr :=
        s"""
        font-family: ${Typography.monoFamily};
        font-size: 11px;
        padding: 6px 12px;
        background: rgba(217, 38, 38, 0.1);
        color: ${Colors.primary};
        border-radius: ${BorderRadius.sm};
        text-transform: uppercase;
        letter-spacing: 0.5px;
        border: 1px solid rgba(217, 38, 38, 0.2);
      """,
      text
    )

  private def githubIcon: SvgElement =
    svg.svg(
      svg.width := "20",
      svg.height := "20",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := "currentColor",
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22")
    )
}
