package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.routing.{AppRouter, ContactPage, LearnPage}

/** Scale page - guided engineering services */
object ScalePage:

  def apply(): HtmlElement =
    div(
      cls := "scale-page",
      styleAttr := s"min-height: 100vh; background: ${Colors.accent}; color: ${Colors.text};",

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

          // Hero Section
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
              rocketIcon,
              span(
                styleAttr := s"""
                  font-family: ${Typography.monoFamily};
                  font-size: 11px;
                  color: ${Colors.textLight};
                  text-transform: uppercase;
                  letter-spacing: 0.15em;
                """,
                "Scale"
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
              "Guided Engineering",
              span(styleAttr := s"color: ${Colors.primary};", ".")
            ),

            // Subtitle
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xl};
                color: ${Colors.textLight};
                max-width: 700px;
                margin: 0 auto ${Spacing.md};
                line-height: 1.7;
              """,
              "When you need structured development, deliberate architecture, and systems that scale with your goals."
            ),

            // Description
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.base};
                color: ${Colors.mediumGray};
                max-width: 650px;
                margin: 0 auto;
                line-height: 1.7;
              """,
              "Scale offers two engagement models for building modern web applications: fast, focused help for well-defined projects, or structured guidance for more complex systems. ",
              "Both leverage AI-assisted development with human review, clear timelines, and honest scope."
            )
          ),

          // Service Tiers
          div(
            styleAttr := s"margin-bottom: ${Spacing.xxl};",

            h2(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xxl};
                color: ${Colors.text};
                text-align: center;
                margin-bottom: ${Spacing.xl};
              """,
              "Two Ways to Work Together"
            ),

            div(
              styleAttr := s"""
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(min(100%, 400px), 1fr));
                gap: ${Spacing.xl};
              """,

              // Tier 1: Build Assist
              serviceTierCard(
                title = "Build Assist",
                price = "$500 – $1,500",
                tagline = "Fast, focused help for small, well-defined projects",
                includes = List(
                  "Simple websites (landing pages, portfolios, marketing sites)",
                  "Small web applications or MVPs",
                  "Frontend + basic backend integration",
                  "Project setup and deployment guidance",
                  "AI-assisted development with human review"
                ),
                characteristics = "Build Assist is designed for speed and clarity. Projects are short (days to ~2 weeks), tightly scoped, and don't require long-term maintenance contracts. You get working software quickly with the understanding that complexity is deliberately constrained.",
                goodFor = List(
                  "Founders testing an idea",
                  "Teams needing a prototype fast",
                  "Solo projects with clear requirements",
                  "Marketing sites that need to ship"
                )
              ),

              // Tier 2: Guided Build
              serviceTierCard(
                title = "Guided Build",
                price = "$2,000 – $6,000+",
                tagline = "Structured development for applications that matter",
                includes = List(
                  "Full-stack web applications",
                  "Authentication, APIs, data modeling",
                  "Iterative development with checkpoints",
                  "Architecture decisions documented",
                  "AI-assisted coding with deliberate design",
                  "Better structure and maintainability"
                ),
                characteristics = "Guided Build is for systems that will grow. Timelines are longer (2–6+ weeks), planning is collaborative, and decisions prioritize correctness over speed. You get an application designed to handle change, with clear patterns and documented trade-offs.",
                goodFor = List(
                  "Startups building their core product",
                  "Teams without in-house engineering",
                  "Projects requiring authentication or data integrity",
                  "Systems that need to scale beyond MVP"
                ),
                isPrimary = true
              )
            )
          ),

          // How I Work Section
          howIWorkSection,

          // Good Fit Section
          goodFitSection,

          // Technology Section
          technologySection,

          // Process Section
          processSection,

          // CTA Section
          ctaSection
        )
      )
    )

  // Service Tier Card Component
  private def serviceTierCard(
    title: String,
    price: String,
    tagline: String,
    includes: List[String],
    characteristics: String,
    goodFor: List[String],
    isPrimary: Boolean = false
  ): HtmlElement =
    div(
      cls := "glass-card",
      styleAttr := s"""
        padding: ${Spacing.xxl};
        ${if isPrimary then s"border-color: ${Colors.primary};" else ""}
      """,

      // Header
      div(
        styleAttr := s"margin-bottom: ${Spacing.lg};",

        // Price badge
        div(
          styleAttr := s"""
            display: inline-flex;
            padding: ${Spacing.xs} ${Spacing.md};
            background: ${if isPrimary then Colors.primary else "rgba(255, 255, 255, 0.1)"};
            border-radius: ${BorderRadius.pill};
            font-family: ${Typography.monoFamily};
            font-size: 13px;
            font-weight: 600;
            margin-bottom: ${Spacing.md};
          """,
          price
        ),

        h3(
          styleAttr := s"""
            font-size: ${Typography.FontSize.xxxl};
            font-weight: ${Typography.FontWeight.medium};
            color: ${Colors.text};
            margin-bottom: ${Spacing.sm};
          """,
          title
        ),

        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.base};
            color: ${Colors.textLight};
            line-height: 1.6;
          """,
          tagline
        )
      ),

      // What's Included
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
          "What's Included"
        ),
        ul(
          styleAttr := "list-style: none; padding: 0; margin: 0;",
          includes.map { item =>
            li(
              styleAttr := s"""
                display: flex;
                align-items: flex-start;
                gap: ${Spacing.sm};
                color: ${Colors.textLight};
                font-size: ${Typography.FontSize.sm};
                margin-bottom: ${Spacing.xs};
                line-height: 1.6;
              """,
              span(styleAttr := s"color: ${Colors.primary}; font-size: 16px; line-height: 1;", "✓"),
              span(item)
            )
          }
        )
      ),

      // Characteristics
      div(
        styleAttr := s"""
          padding: ${Spacing.md};
          background: rgba(255, 255, 255, 0.03);
          border-radius: ${BorderRadius.md};
          border: 1px solid rgba(255, 255, 255, 0.05);
          margin-bottom: ${Spacing.lg};
        """,
        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.sm};
            color: ${Colors.textLight};
            line-height: 1.7;
            margin: 0;
          """,
          characteristics
        )
      ),

      // Good For
      div(
        p(
          styleAttr := s"""
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            color: ${Colors.mediumGray};
            margin-bottom: ${Spacing.sm};
            font-family: ${Typography.monoFamily};
          """,
          "Good For"
        ),
        ul(
          styleAttr := "list-style: none; padding: 0; margin: 0;",
          goodFor.map { item =>
            li(
              styleAttr := s"""
                display: flex;
                align-items: flex-start;
                gap: ${Spacing.sm};
                color: ${Colors.textLight};
                font-size: ${Typography.FontSize.sm};
                margin-bottom: 6px;
              """,
              span(styleAttr := s"color: ${Colors.primary}; font-size: 14px; line-height: 1.2;", "→"),
              span(item)
            )
          }
        )
      )
    )

  // How I Work Section
  private def howIWorkSection: HtmlElement =
    div(
      cls := "glass-card",
      styleAttr := s"margin-bottom: ${Spacing.xxl}; padding: ${Spacing.xxl};",

      h2(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          color: ${Colors.text};
          margin-bottom: ${Spacing.lg};
        """,
        "AI-Assisted, Human-Reviewed"
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.base};
          color: ${Colors.textLight};
          line-height: 1.8;
          margin-bottom: ${Spacing.md};
        """,
        "I use AI-assisted development (primarily Claude Code) to accelerate syntax, explore alternatives, and iterate quickly. But every architectural decision, security consideration, and deployment choice is reviewed and owned by a human — me."
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.lg};
          color: ${Colors.primary};
          font-weight: 500;
          margin-bottom: ${Spacing.lg};
        """,
        "AI writes code. I write systems."
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.base};
          color: ${Colors.textLight};
          line-height: 1.8;
          margin-bottom: ${Spacing.xl};
        """,
        "This approach means faster iteration without sacrificing judgment. You get the speed of modern tooling with the accountability of deliberate engineering."
      ),

      div(
        styleAttr := s"""
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
          "What This Means for You"
        ),
        ul(
          styleAttr := "list-style: none; padding: 0; margin: 0;",
          List(
            "Faster timelines for well-scoped work",
            "More iterations within budget",
            "Clear explanations of technical decisions",
            "Human ownership of security, architecture, deployment",
            "No \"black box\" development"
          ).map { item =>
            li(
              styleAttr := s"""
                display: flex;
                align-items: flex-start;
                gap: ${Spacing.sm};
                color: ${Colors.textLight};
                margin-bottom: ${Spacing.sm};
              """,
              span(styleAttr := s"color: ${Colors.primary}; font-size: 18px; line-height: 1;", "✓"),
              span(item)
            )
          }
        )
      )
    )

  // Good Fit Section
  private def goodFitSection: HtmlElement =
    div(
      styleAttr := s"margin-bottom: ${Spacing.xxl};",

      h2(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          color: ${Colors.text};
          text-align: center;
          margin-bottom: ${Spacing.xl};
        """,
        "Is This a Good Fit?"
      ),

      div(
        styleAttr := s"""
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(min(100%, 350px), 1fr));
          gap: ${Spacing.xl};
          margin-bottom: ${Spacing.xl};
        """,

        // Good Fit
        div(
          cls := "glass-card",
          styleAttr := s"padding: ${Spacing.xl};",

          h3(
            styleAttr := s"""
              font-size: ${Typography.FontSize.lg};
              color: #10b981;
              margin-bottom: ${Spacing.md};
              display: flex;
              align-items: center;
              gap: ${Spacing.sm};
            """,
            span("✓"),
            span("Good Fit")
          ),

          ul(
            styleAttr := "list-style: none; padding: 0; margin: 0;",
            List(
              "You're building a modern web application and need engineering help without hiring full-time",
              "You value clarity, honest timelines, and systems designed for growth",
              "You're comfortable with Scala-based tooling (or trust me to choose the right stack)"
            ).map { item =>
              li(
                styleAttr := s"""
                  color: ${Colors.textLight};
                  font-size: ${Typography.FontSize.sm};
                  line-height: 1.7;
                  margin-bottom: ${Spacing.md};
                  padding-left: ${Spacing.md};
                  border-left: 2px solid #10b981;
                """,
                item
              )
            }
          )
        ),

        // Not a Good Fit
        div(
          cls := "glass-card",
          styleAttr := s"padding: ${Spacing.xl};",

          h3(
            styleAttr := s"""
              font-size: ${Typography.FontSize.lg};
              color: #f59e0b;
              margin-bottom: ${Spacing.md};
              display: flex;
              align-items: center;
              gap: ${Spacing.sm};
            """,
            span("⚠"),
            span("Not a Good Fit")
          ),

          ul(
            styleAttr := "list-style: none; padding: 0; margin: 0;",
            List(
              "You need a massive enterprise system with compliance requirements",
              "You're looking for the cheapest possible option",
              "You require specific frameworks I don't specialize in"
            ).map { item =>
              li(
                styleAttr := s"""
                  color: ${Colors.textLight};
                  font-size: ${Typography.FontSize.sm};
                  line-height: 1.7;
                  margin-bottom: ${Spacing.md};
                  padding-left: ${Spacing.md};
                  border-left: 2px solid #f59e0b;
                """,
                item
              )
            }
          )
        )
      ),

      // Disclaimer Box
      div(
        styleAttr := s"""
          padding: ${Spacing.lg};
          background: rgba(245, 158, 11, 0.1);
          border: 1px solid rgba(245, 158, 11, 0.3);
          border-radius: ${BorderRadius.md};
          display: flex;
          gap: ${Spacing.md};
          align-items: flex-start;
        """,
        span(styleAttr := "font-size: 24px;", "⚠️"),
        div(
          p(
            styleAttr := s"""
              font-size: 11px;
              text-transform: uppercase;
              letter-spacing: 0.1em;
              color: #f59e0b;
              font-weight: 600;
              margin-bottom: ${Spacing.xs};
              font-family: ${Typography.monoFamily};
            """,
            "Important"
          ),
          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.base};
              color: ${Colors.textLight};
              line-height: 1.7;
              margin: 0;
            """,
            "For highly regulated or mission-critical systems, I may recommend alternative approaches or phased engagement."
          )
        )
      )
    )

  // Technology Section
  private def technologySection: HtmlElement =
    div(
      cls := "glass-card",
      styleAttr := s"margin-bottom: ${Spacing.xxl}; padding: ${Spacing.xxl};",

      h2(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          color: ${Colors.text};
          margin-bottom: ${Spacing.lg};
        """,
        "What I Build With"
      ),

      div(
        styleAttr := s"""
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(min(100%, 250px), 1fr));
          gap: ${Spacing.lg};
          margin-bottom: ${Spacing.lg};
        """,

        techCategory("Primary", List("Scala", "ZIO", "Laminar", "Tapir")),
        techCategory("Frontend", List("Scala.js", "Laminar", "Reactive patterns")),
        techCategory("Backend", List("ZIO HTTP", "PostgreSQL", "Type-safe APIs")),
        techCategory("Deployment", List("Docker", "fly.io", "Modern hosting")),
        techCategory("AI Tools", List("Claude Code", "Assisted development"))
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.mediumGray};
          line-height: 1.7;
          font-style: italic;
        """,
        "I specialize in functional Scala systems. If your project requires a different stack, I'll be honest about fit before taking the work."
      )
    )

  private def techCategory(title: String, items: List[String]): HtmlElement =
    div(
      p(
        styleAttr := s"""
          font-size: 11px;
          text-transform: uppercase;
          letter-spacing: 0.1em;
          color: ${Colors.primary};
          font-weight: 500;
          margin-bottom: ${Spacing.sm};
          font-family: ${Typography.monoFamily};
        """,
        title
      ),
      div(
        styleAttr := s"display: flex; flex-wrap: wrap; gap: ${Spacing.xs};",
        items.map { tech =>
          span(
            styleAttr := s"""
              font-family: ${Typography.monoFamily};
              font-size: 11px;
              padding: 4px 8px;
              background: rgba(255, 255, 255, 0.05);
              color: ${Colors.textLight};
              border-radius: ${BorderRadius.sm};
              border: 1px solid ${Colors.glassBorder};
            """,
            tech
          )
        }
      )
    )

  // Process Section
  private def processSection: HtmlElement =
    div(
      cls := "glass-card",
      styleAttr := s"margin-bottom: ${Spacing.xxl}; padding: ${Spacing.xxl};",

      h2(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          color: ${Colors.text};
          margin-bottom: ${Spacing.lg};
          text-align: center;
        """,
        "How Projects Work"
      ),

      div(
        styleAttr := s"""
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(min(100%, 180px), 1fr));
          gap: ${Spacing.lg};
          margin-bottom: ${Spacing.lg};
        """,

        processStep("1", "Initial Discussion", "We talk scope, goals, and fit. (~30 min, free)"),
        processStep("2", "Proposal", "I send a written scope, timeline, and price estimate."),
        processStep("3", "Agreement", "You review, we clarify, and sign off."),
        processStep("4", "Build", "Regular check-ins, working software in stages."),
        processStep("5", "Handoff", "Code, deployment, documentation, and support guidance.")
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.mediumGray};
          text-align: center;
          line-height: 1.7;
        """,
        "No retainers. No hourly billing surprises. No vague \"ongoing maintenance\" contracts.",
        br(),
        span(styleAttr := s"color: ${Colors.primary};", "You pay for defined work, delivered.")
      )
    )

  private def processStep(number: String, title: String, description: String): HtmlElement =
    div(
      styleAttr := s"""
        text-align: center;
        padding: ${Spacing.lg};
        background: rgba(255, 255, 255, 0.02);
        border-radius: ${BorderRadius.md};
        border: 1px solid rgba(255, 255, 255, 0.05);
      """,
      div(
        styleAttr := s"""
          width: 48px;
          height: 48px;
          margin: 0 auto ${Spacing.md};
          background: rgba(217, 38, 38, 0.1);
          border: 2px solid ${Colors.primary};
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          font-weight: 600;
          color: ${Colors.primary};
        """,
        number
      ),
      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.base};
          font-weight: 600;
          color: ${Colors.text};
          margin-bottom: ${Spacing.xs};
        """,
        title
      ),
      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.textLight};
          line-height: 1.6;
          margin: 0;
        """,
        description
      )
    )

  // CTA Section
  private def ctaSection: HtmlElement =
    div(
      cls := "glass-card",
      styleAttr := s"""
        padding: ${Spacing.xxl};
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

        h2(
          styleAttr := s"""
            font-size: ${Typography.FontSize.xxxl};
            color: ${Colors.text};
            margin-bottom: ${Spacing.md};
            font-weight: ${Typography.FontWeight.medium};
          """,
          "Ready to Build?"
        ),

        p(
          styleAttr := s"""
            font-size: ${Typography.FontSize.base};
            color: ${Colors.textLight};
            margin-bottom: ${Spacing.xl};
            max-width: 500px;
            margin-left: auto;
            margin-right: auto;
            line-height: 1.7;
          """,
          "If you're working on something that needs structured engineering, let's talk. I'll be honest about fit, timeline, and cost — no sales pitch required."
        ),

        // Primary CTA
        button(
          cls := "btn-primary",
          styleAttr := s"""
            background-color: ${Colors.primary};
            color: ${Colors.text};
            padding: ${Spacing.md} ${Spacing.xxl};
            border-radius: ${BorderRadius.md};
            font-weight: ${Typography.FontWeight.semibold};
            font-size: ${Typography.FontSize.base};
            border: none;
            cursor: pointer;
            transition: all 0.2s ease;
            margin-bottom: ${Spacing.md};
          """,
          onClick --> { _ => AppRouter.navigateTo(ContactPage) },
          "Start a Conversation →"
        ),

        // Secondary CTA
        div(
          styleAttr := s"margin-top: ${Spacing.md};",
          a(
            href := "#",
            onClick.preventDefault --> { _ => AppRouter.navigateTo(LearnPage) },
            styleAttr := s"""
              color: ${Colors.textLight};
              text-decoration: none;
              font-size: ${Typography.FontSize.sm};
              display: inline-flex;
              align-items: center;
              gap: ${Spacing.xs};
              transition: color 0.2s;
            """,
            "Prefer to start with learning?",
            span(styleAttr := s"color: ${Colors.primary};", "→ Explore Build")
          )
        )
      )
    )

  // Icons
  private def rocketIcon: SvgElement =
    svg.svg(
      svg.width := "12",
      svg.height := "12",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.path(svg.d := "M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"),
      svg.path(svg.d := "m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"),
      svg.path(svg.d := "M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"),
      svg.path(svg.d := "M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5")
    )
