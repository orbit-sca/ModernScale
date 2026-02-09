package com.brice.components.layout

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js
import com.brice.theme.DesignTokens.*
import com.brice.routing.{AppRouter, HomePage, DevelopmentPage, AnalyticsPage, LearnPage, ScalePage, AboutPage, ContactPage}

object NavBar {

  def apply(): HtmlElement = {
    val mobileMenuOpen = Var(false)
    val projectsDropdownOpen = Var(false)
    val solutionsDropdownOpen = Var(false)

    navTag(
      styleAttr := s"""
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
        background-color: rgba(10, 10, 11, 0.9);
        backdrop-filter: blur(20px);
        border-bottom: 1px solid ${Colors.glassBorder};
        padding: ${Spacing.md} 0;
      """,

      // Auto-close mobile menu on resize to desktop
      onMountCallback { _ =>
        val resizeHandler: js.Function1[dom.Event, Unit] = (_: dom.Event) => {
          if (dom.window.innerWidth > 1200 && mobileMenuOpen.now()) {
            mobileMenuOpen.set(false)
          }
        }
        dom.window.addEventListener("resize", resizeHandler)
      },

      div(
        cls := "container",

        div(
          styleAttr := s"""
            display: flex;
            justify-content: space-between;
            align-items: center;
          """,

          // Logo
          a(
            href := "/",
            onClick.preventDefault --> { _ => AppRouter.navigateTo(HomePage) },
            styleAttr := s"""
              display: flex;
              align-items: center;
              gap: ${Spacing.sm};
              text-decoration: none;
              color: inherit;
              cursor: pointer;
            """,

            // Logo text with terminal style
            div(
              styleAttr := s"display: flex; align-items: center; gap: 4px;",
              span(
                styleAttr := s"color: ${Colors.primary}; font-family: ${Typography.monoFamily}; font-size: 14px;",
                ">"
              ),
              span(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  font-family: ${Typography.fontFamily};
                """,
                "modernscale"
              ),
              span(
                styleAttr := s"color: ${Colors.primary}; font-size: ${Typography.FontSize.xl}; font-weight: ${Typography.FontWeight.bold};",
                ".dev"
              )
            )
          ),

          // Desktop nav links
          div(
            cls := "desktop-nav",
            styleAttr := s"""
              display: flex;
              align-items: center;
              gap: ${Spacing.xl};
            """,

            navLink("Home", () => AppRouter.navigateTo(HomePage)),

            // Projects dropdown
            navDropdown(
              "Projects",
              projectsDropdownOpen,
              List(
                ("Software", () => AppRouter.navigateTo(DevelopmentPage)),
                ("Data & Analytics", () => AppRouter.navigateTo(AnalyticsPage))
              )
            ),

            // Solutions dropdown
            navDropdown(
              "Solutions",
              solutionsDropdownOpen,
              List(
                ("Build", () => AppRouter.navigateTo(LearnPage)),
                ("Scale", () => AppRouter.navigateTo(ScalePage))
              )
            ),

            navLink("Contact", () => AppRouter.navigateTo(ContactPage)),

            // CTA button
            button(
              styleAttr := s"""
                background-color: ${Colors.primary};
                color: ${Colors.text};
                padding: ${Spacing.sm} ${Spacing.lg};
                border-radius: ${BorderRadius.md};
                font-weight: ${Typography.FontWeight.semibold};
                font-size: 13px;
                border: none;
                cursor: pointer;
                transition: all 0.2s ease;
                font-family: ${Typography.fontFamily};
                text-transform: uppercase;
                letter-spacing: 0.5px;
              """,
              onClick --> { _ => AppRouter.navigateTo(ContactPage) },
              "Hire Me"
            )
          ),

          // Mobile hamburger button
          button(
            cls := "mobile-menu-button",
            styleAttr := s"""
              background: none;
              border: 1px solid ${Colors.glassBorder};
              border-radius: ${BorderRadius.sm};
              cursor: pointer;
              padding: ${Spacing.xs};
              align-items: center;
              justify-content: center;
              color: ${Colors.text};
            """,
            onClick --> { _ => mobileMenuOpen.update(!_) },

            child <-- mobileMenuOpen.signal.map { isOpen =>
              span(
                styleAttr := s"font-size: 20px; color: ${Colors.text};",
                if (isOpen) "✕" else "☰"
              )
            }
          )
        ),

        // Mobile menu
        child <-- mobileMenuOpen.signal.map { isOpen =>
          if (isOpen)
            div(
              styleAttr := s"""
                display: flex;
                flex-direction: column;
                gap: ${Spacing.md};
                padding: ${Spacing.lg} 0;
                border-top: 1px solid ${Colors.glassBorder};
                margin-top: ${Spacing.md};
                max-height: calc(100vh - 80px);
                overflow-y: auto;
                overflow-x: hidden;
              """,

              mobileNavLink("Home", () => { AppRouter.navigateTo(HomePage); mobileMenuOpen.set(false) }),

              // Projects section
              div(
                styleAttr := s"color: ${Colors.mediumGray}; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin-top: ${Spacing.sm};",
                "Projects"
              ),
              mobileNavLink("Software", () => { AppRouter.navigateTo(DevelopmentPage); mobileMenuOpen.set(false) }),
              mobileNavLink("Data & Analytics", () => { AppRouter.navigateTo(AnalyticsPage); mobileMenuOpen.set(false) }),

              // Solutions section
              div(
                styleAttr := s"color: ${Colors.mediumGray}; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin-top: ${Spacing.md};",
                "Solutions"
              ),
              mobileNavLink("Build", () => { AppRouter.navigateTo(LearnPage); mobileMenuOpen.set(false) }),
              mobileNavLink("Scale", () => { AppRouter.navigateTo(ScalePage); mobileMenuOpen.set(false) }),

              mobileNavLink("Contact", () => { AppRouter.navigateTo(ContactPage); mobileMenuOpen.set(false) }),

              button(
                styleAttr := s"""
                  background-color: ${Colors.primary};
                  color: ${Colors.text};
                  padding: ${Spacing.md} ${Spacing.lg};
                  border-radius: ${BorderRadius.md};
                  font-weight: ${Typography.FontWeight.semibold};
                  border: none;
                  cursor: pointer;
                  font-family: ${Typography.fontFamily};
                  width: 100%;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                """,
                onClick --> { _ =>
                  AppRouter.navigateTo(ContactPage)
                  mobileMenuOpen.set(false)
                },
                "Hire Me"
              )
            )
          else
            emptyNode
        }
      )
    )
  }

  private def navLink(text: String, action: () => Unit): HtmlElement =
    a(
      href := "#",
      onClick.preventDefault --> { _ => action() },
      styleAttr := s"""
        color: ${Colors.textLight};
        text-decoration: none;
        font-weight: ${Typography.FontWeight.medium};
        font-size: 14px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        transition: color 0.2s ease;
        cursor: pointer;
        position: relative;
      """,
      text
    )

  private def mobileNavLink(text: String, action: () => Unit): HtmlElement =
    a(
      href := "#",
      onClick.preventDefault --> { _ => action() },
      styleAttr := s"""
        color: ${Colors.textLight};
        text-decoration: none;
        font-weight: ${Typography.FontWeight.medium};
        padding: ${Spacing.sm} 0;
        cursor: pointer;
        transition: color 0.2s ease;
      """,
      text
    )

  private def navDropdown(text: String, dropdownOpen: Var[Boolean], items: List[(String, () => Unit)]): HtmlElement =
    div(
      styleAttr := s"position: relative;",
      onMouseEnter --> { _ => dropdownOpen.set(true) },
      onMouseLeave --> { _ => dropdownOpen.set(false) },

      // Dropdown button
      div(
        styleAttr := s"""
          color: ${Colors.textLight};
          font-weight: ${Typography.FontWeight.medium};
          font-size: 14px;
          text-transform: uppercase;
          letter-spacing: 0.5px;
          cursor: pointer;
          position: relative;
          display: flex;
          align-items: center;
          gap: 4px;
        """,
        span(text),
        // Dropdown arrow
        svg.svg(
          svg.width := "12",
          svg.height := "12",
          svg.viewBox := "0 0 24 24",
          svg.fill := "none",
          svg.stroke := Colors.textLight,
          svg.strokeWidth := "2",
          svg.strokeLineCap := "round",
          svg.strokeLineJoin := "round",
          svg.polyline(svg.points := "6 9 12 15 18 9")
        )
      ),

      // Invisible bridge to prevent dropdown from closing
      child <-- dropdownOpen.signal.map { isOpen =>
        if (isOpen)
          div(
            styleAttr := s"""
              position: absolute;
              top: 100%;
              left: -10px;
              right: -10px;
              height: 20px;
              z-index: 999;
            """
          )
        else emptyNode
      },

      // Dropdown menu
      child <-- dropdownOpen.signal.map { isOpen =>
        if (isOpen)
          div(
            styleAttr := s"""
              position: absolute;
              top: calc(100% + 12px);
              left: 0;
              background: rgba(10, 10, 11, 0.95);
              backdrop-filter: blur(20px);
              border: 1px solid ${Colors.glassBorder};
              border-radius: ${BorderRadius.md};
              padding: ${Spacing.sm};
              min-width: 200px;
              box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
              z-index: 1000;
            """,
            items.map { case (label, action) =>
              a(
                cls := "nav-dropdown-item",
                href := "#",
                onClick.preventDefault --> { _ => action(); dropdownOpen.set(false) },
                styleAttr := s"""
                  display: block;
                  color: ${Colors.textLight};
                  text-decoration: none;
                  padding: ${Spacing.sm} ${Spacing.md};
                  border-radius: ${BorderRadius.sm};
                  transition: all 0.2s ease;
                  font-size: 14px;
                  cursor: pointer;
                """,
                label
              )
            }
          )
        else emptyNode
      }
    )
}
