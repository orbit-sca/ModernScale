package com.brice.components.sections

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.brice.theme.DesignTokens.*
import com.brice.routing.{AppRouter, ContactPage}

object CallToActionSection {

    def apply(): HtmlElement =
      // Call to Action section
      div(
        styleAttr := s"""
          position: relative;
          padding: ${Spacing.xxxl} 0;
          background-color: ${Colors.accent};
          overflow: hidden;
        """,

        // Large decorative background blob
        div(
          styleAttr := s"""
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 800px;
            height: 800px;
            background-color: rgba(45, 90, 71, 0.05);
            border-radius: 50%;
            filter: blur(80px);
          """
        ),

        // Content container
        div(
          cls := "container",
          styleAttr := s"""
            position: relative;
            text-align: center;
            max-width: 1024px;
            margin: 0 auto;
          """,

          // Badge with location icon
          div(
            styleAttr := s"""
              display: inline-flex;
              align-items: center;
              gap: ${Spacing.xs};
              padding: ${Spacing.xs} ${Spacing.md};
              background-color: ${Colors.white};
              border-radius: ${BorderRadius.pill};
              box-shadow: ${Card.shadow};
              margin-bottom: ${Spacing.xl};
            """,
            span(
              styleAttr := s"""
                color: ${Colors.primary};
                font-size: ${Typography.FontSize.sm};
              """,
              "📍"
            ),
            span(
              styleAttr := s"""
                font-size: ${Typography.FontSize.sm};
                font-weight: ${Typography.FontWeight.medium};
                color: ${Colors.text};
              """,
              "Serving Property Owners Everywhere"
            )
          ),

          // Main heading
          h2(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xxxl};
              font-weight: ${Typography.FontWeight.bold};
              color: ${Colors.text};
              margin-bottom: ${Spacing.lg};
              line-height: ${Typography.LineHeight.tight};
            """,
            "Ready to Make Your Property",
            br(),
            span(
              styleAttr := s"color: ${Colors.primary};",
              "Work Smarter?"
            )
          ),

          // Subtext
          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.xl};
              color: rgba(44, 44, 44, 0.6);
              margin-bottom: ${Spacing.xl};
              max-width: 672px;
              margin-left: auto;
              margin-right: auto;
            """,
            "Whether you've got one unit or twenty, we're here to turn your property into a money-making machine. No stress, all success."
          ),

          // CTA Button
          div(
            styleAttr := s"margin-bottom: ${Spacing.lg};",
            button(
              styleAttr := s"""
                background-color: ${Colors.primary};
                color: ${Colors.white};
                padding: ${Spacing.lg} ${Spacing.xxl};
                border-radius: ${BorderRadius.pill};
                font-weight: ${Typography.FontWeight.semibold};
                font-size: ${Typography.FontSize.lg};
                border: none;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: ${Spacing.sm};
                font-family: ${Typography.fontFamily};
                box-shadow: 0 8px 24px rgba(45, 90, 71, 0.2);
              """,
              onClick --> { _ => AppRouter.navigateTo(ContactPage) },
              span("Start the Conversation"),
              span(
                styleAttr := "transition: transform 0.3s ease;",
                "→"
              )
            )
          ),

          // Fine print
          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.sm};
              color: rgba(44, 44, 44, 0.4);
              margin-top: ${Spacing.lg};
            """,
            "No commitment. No awkward sales pitch. Just good vibes."
          )
        )
      )        
}


