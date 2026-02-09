package com.brice.components.sections

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.routing.{AppRouter, ContactPage}

object ContactCTASection {

  def apply(): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.xxxl} 0;
        background: ${Colors.accent};
        position: relative;
        overflow: hidden;
      """,

      // Red glow decoration
      div(styleAttr := s"position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 600px; height: 600px; background: radial-gradient(circle, rgba(217, 38, 38, 0.1) 0%, transparent 70%); pointer-events: none;"),

      section(
        div(
          styleAttr := s"""
            text-align: center;
            position: relative;
            z-index: 1;
            max-width: 800px;
            margin: 0 auto;
          """,

          // Terminal prompt
          div(
            styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md};",
            "> ./contact --init"
          ),

          h2(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xxxl};
              font-weight: ${Typography.FontWeight.bold};
              color: ${Colors.text};
              margin-bottom: ${Spacing.lg};
            """,
            "Let's Build ",
            span(styleAttr := s"color: ${Colors.primary}; text-shadow: 0 0 20px rgba(217, 38, 38, 0.5);", "Something"),
            br(),
            span(styleAttr := s"color: ${Colors.text};", "Together")
          ),

          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.lg};
              color: ${Colors.textLight};
              margin-bottom: ${Spacing.xl};
              line-height: 1.7;
            """,
            "Have a project in mind? Looking for a developer who speaks fluent Scala and thinks in types? Let's connect."
          ),

          // CTA Button
          button(
            styleAttr := s"""
              background: ${Colors.primary};
              color: ${Colors.text};
              padding: ${Spacing.md} ${Spacing.lg};
              border-radius: ${BorderRadius.md};
              font-family: ${Typography.fontFamily};
              font-weight: ${Typography.FontWeight.semibold};
              font-size: 16px;
              border: none;
              cursor: pointer;
              transition: all 0.2s ease;
              text-transform: uppercase;
              letter-spacing: 0.5px;
              display: inline-flex;
              align-items: center;
              gap: ${Spacing.sm};
            """,
            onClick --> { _ => AppRouter.navigateTo(ContactPage) },
            "Start a Conversation",
            span(styleAttr := "transition: transform 0.2s ease;", " →")
          )
        )
      )
    )
}
