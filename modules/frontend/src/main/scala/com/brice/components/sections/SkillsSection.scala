package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.components.common.Directives.*

object SkillsSection {

  case class Skill(name: String, level: Int) // level out of 100

  // Skills verified through GitHub projects and coursework
  val skills = List(
    ("Languages", List(
      Skill("Scala", 85),
      Skill("SQL", 80),
      Skill("Julia", 60),
      Skill("Python", 65)
    )),
    ("Frameworks", List(
      Skill("ZIO", 80),
      Skill("Laminar", 85),
      Skill("Tapir", 80),
      Skill("ScalaJS", 75)
    )),
    ("Data & Tools", List(
      Skill("PostgreSQL", 75),
      Skill("Git", 85),
      Skill("Quill ORM", 70),
      Skill("Vite", 75)
    ))
  )

  def apply(): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.xxxl} 0;
        background: #0d0d0e;
        position: relative;
      """,

      section(
        div(
          styleAttr := "position: relative; z-index: 1;",

          // Section header
          div(
            styleAttr := s"text-align: center; margin-bottom: ${Spacing.xxl};",

            div(
              styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
              "> cat ./skills.json"
            ),

            h2(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xxxl};
                font-weight: ${Typography.FontWeight.bold};
                color: ${Colors.text};
                margin-bottom: ${Spacing.md};
              """,
              "Technical ",
              span(styleAttr := s"color: ${Colors.primary};", "Skills")
            )
          ),

          // Skills grid
          div(
            styleAttr := s"""
              display: grid;
              grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
              gap: ${Spacing.xl};
            """,

            skills.zipWithIndex.map { case ((category, categorySkills), index) =>
              skillCategory(category, categorySkills, index * 0.15)
            }
          )
        )
      )
    )

  private def skillCategory(name: String, categorySkills: List[Skill], delay: Double): HtmlElement =
    div(
      animateOnScroll(delaySeconds = delay, initialYOffsetPx = 30),
      styleAttr := s"""
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        padding: ${Spacing.xl};
      """,

      h3(
        styleAttr := s"""
          font-size: ${Typography.FontSize.lg};
          font-weight: ${Typography.FontWeight.bold};
          color: ${Colors.text};
          margin-bottom: ${Spacing.lg};
          font-family: ${Typography.monoFamily};
        """,
        s"// $name"
      ),

      div(
        styleAttr := s"display: flex; flex-direction: column; gap: ${Spacing.md};",
        categorySkills.map(skillBar)
      )
    )

  private def skillBar(skill: Skill): HtmlElement =
    div(
      // Skill name and percentage
      div(
        styleAttr := s"display: flex; justify-content: space-between; margin-bottom: ${Spacing.xs};",
        span(
          styleAttr := s"font-size: ${Typography.FontSize.sm}; color: ${Colors.text};",
          skill.name
        ),
        span(
          styleAttr := s"font-family: ${Typography.monoFamily}; font-size: ${Typography.FontSize.xs}; color: ${Colors.primary};",
          s"${skill.level}%"
        )
      ),

      // Progress bar
      div(
        styleAttr := s"""
          height: 4px;
          background: ${Colors.lightGray};
          border-radius: 2px;
          overflow: hidden;
        """,
        div(
          styleAttr := s"""
            height: 100%;
            width: ${skill.level}%;
            background: linear-gradient(90deg, ${Colors.primary}, #ff4444);
            border-radius: 2px;
            transition: width 1s ease-out;
          """
        )
      )
    )
}
