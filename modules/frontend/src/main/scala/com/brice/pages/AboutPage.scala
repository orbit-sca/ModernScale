package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.sections.{ProjectsSection, SkillsSection, ContactCTASection}

object AboutPage {

  def apply(): HtmlElement =
    div(
      // Page header
      div(
        styleAttr := s"""
          background: ${Colors.accent};
          padding: 160px 0 ${Spacing.xxxl};
          position: relative;
          overflow: hidden;
        """,

        // Data grid background
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

        div(
          cls := "container",
          styleAttr := "position: relative; z-index: 1;",

          // Terminal prompt
          div(
            styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
            "> whoami"
          ),

          h1(
            styleAttr := s"""
              font-size: ${Typography.FontSize.hero};
              font-weight: ${Typography.FontWeight.bold};
              color: ${Colors.text};
              margin-bottom: ${Spacing.lg};
            """,
            "About ",
            span(styleAttr := s"color: ${Colors.primary};", "Me")
          ),

          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xl};
              color: ${Colors.textLight};
              max-width: 700px;
              line-height: 1.7;
            """,
            "Full-stack developer with a passion for functional programming, type safety, and building elegant solutions to complex problems."
          )
        )
      ),

      // Bio section
      div(
        styleAttr := s"padding: ${Spacing.xxxl} 0; background: #0d0d0e;",

        div(
          cls := "container",

          div(
            cls := "about-grid",
            styleAttr := s"""
              display: grid;
              grid-template-columns: 2fr 1fr;
              gap: ${Spacing.xxl};
              align-items: start;
            """,

            // Main bio
            div(
              h2(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xxl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.lg};
                """,
                "The ",
                span(styleAttr := s"color: ${Colors.primary};", "Story")
              ),

              p(
                styleAttr := s"color: ${Colors.textLight}; line-height: 1.8; margin-bottom: ${Spacing.md};",
                "I started my journey in software development with a fascination for how things work under the hood. That curiosity led me to functional programming and Scala, where I found a way of thinking about code that just clicked."
              ),

              p(
                styleAttr := s"color: ${Colors.textLight}; line-height: 1.8; margin-bottom: ${Spacing.md};",
                "Today, I specialize in building full-stack applications using modern functional programming patterns. ZIO for effect management, Laminar for reactive frontends, and Tapir for type-safe APIs - these tools help me create software that's both robust and maintainable."
              ),

              p(
                styleAttr := s"color: ${Colors.textLight}; line-height: 1.8;",
                "When I'm not coding, you'll find me exploring new technologies, contributing to open source, or helping others learn the elegance of functional programming."
              )
            ),

            // Quick stats
            div(
              styleAttr := s"""
                background: ${Colors.cardBackground};
                border: 1px solid ${Colors.glassBorder};
                border-radius: ${BorderRadius.lg};
                padding: ${Spacing.xl};
              """,

              h3(
                styleAttr := s"""
                  font-family: ${Typography.monoFamily};
                  font-size: ${Typography.FontSize.sm};
                  color: ${Colors.primary};
                  margin-bottom: ${Spacing.lg};
                """,
                "// quick_stats.json"
              ),

              statItem("Years Coding", "5+"),
              statItem("Projects Completed", "20+"),
              statItem("Cups of Coffee", "∞"),
              statItem("Favorite Language", "Scala")
            )
          )
        )
      ),

      // Projects and skills
      ProjectsSection(),
      SkillsSection(),
      ContactCTASection()
    )

  private def statItem(label: String, value: String): HtmlElement =
    div(
      styleAttr := s"""
        display: flex;
        justify-content: space-between;
        padding: ${Spacing.sm} 0;
        border-bottom: 1px solid ${Colors.glassBorder};
      """,
      span(styleAttr := s"color: ${Colors.textLight}; font-size: ${Typography.FontSize.sm};", label),
      span(styleAttr := s"color: ${Colors.text}; font-family: ${Typography.monoFamily}; font-size: ${Typography.FontSize.sm};", value)
    )
}
