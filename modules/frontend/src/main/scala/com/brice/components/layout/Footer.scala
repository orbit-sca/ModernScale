package com.brice.components.layout

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.brice.theme.DesignTokens.*
import com.brice.components.common.Directives.*
import com.brice.routing.{AppRouter, HomePage, AboutPage, ContactPage}

object Footer {

  def apply(): HtmlElement =
    footerTag(
      styleAttr := s"""
        background-color: #050506;
        border-top: 1px solid ${Colors.glassBorder};
        padding: ${Spacing.xxxl} 0 ${Spacing.xl};
      """,

      div(
        cls := "container",

        // Main footer grid
        div(
          cls := "footer-grid",
          styleAttr := s"margin-bottom: ${Spacing.xxl};",

          // Brand section
          div(
            // Logo
            a(
              href := "#",
              onClick.preventDefault --> { _ => AppRouter.navigateTo(HomePage) },
              styleAttr := s"""
                display: flex;
                align-items: center;
                gap: 4px;
                margin-bottom: ${Spacing.lg};
                text-decoration: none;
                color: inherit;
              """,
              span(
                styleAttr := s"color: ${Colors.primary}; font-family: ${Typography.monoFamily}; font-size: 14px;",
                ">"
              ),
              span(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xxl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                """,
                "modernscale"
              ),
              span(
                styleAttr := s"color: ${Colors.primary}; font-size: ${Typography.FontSize.xxl}; font-weight: ${Typography.FontWeight.bold};",
                ".dev"
              )
            ),

            // Tagline
            p(
              styleAttr := s"""
                color: ${Colors.textLight};
                max-width: 300px;
                line-height: ${Typography.LineHeight.relaxed};
                font-size: ${Typography.FontSize.sm};
              """,
              "Brice Richmond's portfolio — building modern, scalable solutions with functional programming and cutting-edge technologies."
            ),

            // Tech badges
            div(
              styleAttr := s"display: flex; gap: ${Spacing.xs}; margin-top: ${Spacing.md}; flex-wrap: wrap;",
              footerBadge("Scala"),
              footerBadge("ZIO"),
              footerBadge("Laminar")
            )
          ),

          // Quick Links
          div(
            h4(
              styleAttr := s"""
                font-weight: ${Typography.FontWeight.semibold};
                font-size: ${Typography.FontSize.sm};
                margin-bottom: ${Spacing.md};
                color: ${Colors.text};
                text-transform: uppercase;
                letter-spacing: 1px;
              """,
              "Navigation"
            ),
            div(
              styleAttr := s"""
                display: flex;
                flex-direction: column;
                gap: ${Spacing.sm};
              """,
              footerLink("Home", () => AppRouter.navigateTo(HomePage)),
              footerLink("About me", () => AppRouter.navigateTo(AboutPage)),
              footerLink("Contact", () => AppRouter.navigateTo(ContactPage))
            )
          ),

          // Connect
          div(
            h4(
              styleAttr := s"""
                font-weight: ${Typography.FontWeight.semibold};
                font-size: ${Typography.FontSize.sm};
                margin-bottom: ${Spacing.md};
                color: ${Colors.text};
                text-transform: uppercase;
                letter-spacing: 1px;
              """,
              "Connect"
            ),
            div(
              styleAttr := s"""
                display: flex;
                flex-direction: column;
                gap: ${Spacing.sm};
              """,
              a(
                href := "mailto:build@modernscale.dev",
                styleAttr := s"""
                  color: ${Colors.textLight};
                  text-decoration: none;
                  transition: color 0.2s ease;
                  font-size: ${Typography.FontSize.sm};
                """,
                "build@modernscale.dev"
              ),
              a(
                href := "https://github.com/brice",
                target := "_blank",
                styleAttr := s"""
                  color: ${Colors.textLight};
                  text-decoration: none;
                  transition: color 0.2s ease;
                  font-size: ${Typography.FontSize.sm};
                """,
                "GitHub"
              )
            )
          )
        ),

        // Bottom section
        div(
          styleAttr := s"""
            border-top: 1px solid ${Colors.glassBorder};
            padding-top: ${Spacing.lg};
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: ${Spacing.md};
          """,

          // Copyright
          p(
            styleAttr := s"""
              color: ${Colors.mediumGray};
              font-size: ${Typography.FontSize.xs};
              margin: 0;
              font-family: ${Typography.monoFamily};
            """,
            s"© ${java.time.Year.now().getValue} ModernScale"
          ),

          // Status indicator
          div(
            styleAttr := s"display: flex; align-items: center; gap: ${Spacing.sm};",
            div(
              styleAttr := s"""
                width: 6px;
                height: 6px;
                border-radius: 50%;
                background: ${Colors.primary};
                box-shadow: 0 0 8px rgba(217, 38, 38, 0.5);
              """
            ),
            span(
              styleAttr := s"""
                color: ${Colors.mediumGray};
                font-size: ${Typography.FontSize.xs};
                font-family: ${Typography.monoFamily};
              """,
              "All systems operational"
            )
          )
        )
      )
    )

  private def footerLink(text: String, action: () => Unit): HtmlElement =
    a(
      href := "#",
      onClick.preventDefault --> { _ => action() },
      styleAttr := s"""
        color: ${Colors.textLight};
        text-decoration: none;
        transition: color 0.2s ease;
        font-size: ${Typography.FontSize.sm};
        cursor: pointer;
      """,
      text
    )

  private def footerBadge(text: String): HtmlElement =
    span(
      styleAttr := s"""
        font-family: ${Typography.monoFamily};
        font-size: 10px;
        padding: 4px 8px;
        background: rgba(217, 38, 38, 0.1);
        color: ${Colors.primary};
        border-radius: ${BorderRadius.sm};
        text-transform: uppercase;
        letter-spacing: 0.5px;
      """,
      text
    )
}
