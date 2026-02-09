package com.brice.components.sections

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.components.common.Directives.*

object ProcessSection {

  def apply(): HtmlElement =
    // Process section with dark background
    div(
        styleAttr := s"""
          position: relative;
          background-color: ${Colors.primary};
          padding: ${Spacing.xxxl} 0;
          margin-bottom: ${Spacing.xxxl};
          overflow: hidden;
          border-radius: 0;
        """,

        // Decorative blobs
        div(styleAttr := "position: absolute; top: 0; right: 0; width: 384px; height: 384px; background: rgba(122, 158, 126, 0.2); border-radius: 50%; filter: blur(80px); transform: translateY(-50%);"),
        div(styleAttr := "position: absolute; bottom: 0; left: 0; width: 288px; height: 288px; background: rgba(196, 169, 98, 0.1); border-radius: 50%; filter: blur(80px); transform: translateY(50%);"),

        // Content wrapper with container
        div(
          cls := "container",
          styleAttr := "position: relative; z-index: 1;",

          h2(
            styleAttr := s"""
              text-align: center;
              color: ${Colors.white};
              font-size: ${Typography.FontSize.xxxl};
              font-weight: ${Typography.FontWeight.bold};
              margin-bottom: ${Spacing.md};
            """,
            "How It Works"
          ),

          p(
            styleAttr := s"""
              text-align: center;
              color: rgba(255, 255, 255, 0.7);
              margin-bottom: ${Spacing.xxxl};
              font-size: ${Typography.FontSize.xl};
              max-width: 600px;
              margin-left: auto;
              margin-right: auto;
            """,
            "Partnering with us is easier than ordering takeout. Probably healthier for your wallet too."
          ),

          // Connecting line decoration (hidden on mobile via CSS)
          div(
            cls := "process-connecting-line",
            styleAttr := s"""
              position: relative;
              height: 0;
              margin-bottom: ${Spacing.xxxl};
            """,
            // Horizontal line
            div(
              styleAttr := s"""
                position: absolute;
                top: 140px;
                left: 10%;
                right: 10%;
                height: 2px;
                background: linear-gradient(90deg,
                  transparent 0%,
                  rgba(196, 169, 98, 0.3) 10%,
                  rgba(196, 169, 98, 0.5) 50%,
                  rgba(196, 169, 98, 0.3) 90%,
                  transparent 100%);
                z-index: 0;
              """
            ),
            // Decorative dots at connection points
            div(
              styleAttr := s"""
                position: absolute;
                top: 134px;
                left: 22%;
                width: 14px;
                height: 14px;
                background-color: ${Colors.highlights};
                border-radius: 50%;
                box-shadow: 0 0 0 4px rgba(196, 169, 98, 0.2);
                z-index: 0;
              """
            ),
            div(
              styleAttr := s"""
                position: absolute;
                top: 134px;
                left: 46%;
                width: 14px;
                height: 14px;
                background-color: ${Colors.highlights};
                border-radius: 50%;
                box-shadow: 0 0 0 4px rgba(196, 169, 98, 0.2);
                z-index: 0;
              """
            ),
            div(
              styleAttr := s"""
                position: absolute;
                top: 134px;
                left: 70%;
                width: 14px;
                height: 14px;
                background-color: ${Colors.highlights};
                border-radius: 50%;
                box-shadow: 0 0 0 4px rgba(196, 169, 98, 0.2);
                z-index: 0;
              """
            ),
            div(
              styleAttr := s"""
                position: absolute;
                top: 134px;
                left: 86%;
                width: 14px;
                height: 14px;
                background-color: ${Colors.highlights};
                border-radius: 50%;
                box-shadow: 0 0 0 4px rgba(196, 169, 98, 0.2);
                z-index: 0;
              """
            )
          ),

          grid(4)(
            // Step 1
            div(
              cls := "process-card",
              animateOnScroll(delaySeconds = 0, initialYOffsetPx = 30),
              styleAttr := s"""
                position: relative;
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                border: 1px solid rgba(255, 255, 255, 0.1);
                transition: all 0.3s ease;
                min-height: 280px;
                display: flex;
                flex-direction: column;
              """,

              // Large background number
              span(
                styleAttr := s"""
                  position: absolute;
                  top: ${Spacing.sm};
                  right: ${Spacing.md};
                  font-size: 96px;
                  font-weight: ${Typography.FontWeight.extrabold};
                  color: rgba(255, 255, 255, 0.08);
                  z-index: 0;
                  line-height: 1;
                """,
                "01"
              ),

              // Content
              div(
                styleAttr := "position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column;",

                div(
                  styleAttr := s"""
                    width: 48px;
                    height: 48px;
                    background-color: ${Colors.highlights};
                    border-radius: ${BorderRadius.md};
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: ${Spacing.md};
                  """,
                  span(styleAttr := "font-size: 20px;", "💬")
                ),

                h3(
                  styleAttr := s"""
                    font-size: ${Typography.FontSize.lg};
                    font-weight: ${Typography.FontWeight.bold};
                    color: ${Colors.white};
                    margin-bottom: ${Spacing.sm};
                  """,
                  "Let's Chat"
                ),

                p(
                  styleAttr := s"""
                    color: rgba(255, 255, 255, 0.8);
                    line-height: 1.5;
                    font-size: ${Typography.FontSize.sm};
                  """,
                  "Reach out and tell us about your property. We're all ears and zero judgment."
                )
              )
            ),

            // Step 2
            div(
              cls := "process-card",
              animateOnScroll(delaySeconds = 0.15, initialYOffsetPx = 30),
              styleAttr := s"""
                position: relative;
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                border: 1px solid rgba(255, 255, 255, 0.1);
                transition: all 0.3s ease;
                min-height: 280px;
                display: flex;
                flex-direction: column;
              """,

              span(
                styleAttr := s"""
                  position: absolute;
                  top: ${Spacing.sm};
                  right: ${Spacing.md};
                  font-size: 96px;
                  font-weight: ${Typography.FontWeight.extrabold};
                  color: rgba(255, 255, 255, 0.08);
                  z-index: 0;
                  line-height: 1;
                """,
                "02"
              ),

              div(
                styleAttr := "position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column;",

                div(
                  styleAttr := s"""
                    width: 48px;
                    height: 48px;
                    background-color: ${Colors.highlights};
                    border-radius: ${BorderRadius.md};
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: ${Spacing.md};
                  """,
                  span(styleAttr := "font-size: 20px;", "✓")
                ),

                h3(
                  styleAttr := s"""
                    font-size: ${Typography.FontSize.lg};
                    font-weight: ${Typography.FontWeight.bold};
                    color: ${Colors.white};
                    margin-bottom: ${Spacing.sm};
                  """,
                  "We'll Evaluate"
                ),

                p(
                  styleAttr := s"""
                    color: rgba(255, 255, 255, 0.8);
                    line-height: 1.5;
                    font-size: ${Typography.FontSize.sm};
                  """,
                  "We'll check out your space and crunch the numbers. If it's a fit, we'll make you an offer you'll actually like."
                )
              )
            ),

            // Step 3
            div(
              cls := "process-card",
              animateOnScroll(delaySeconds = 0.3, initialYOffsetPx = 30),
              styleAttr := s"""
                position: relative;
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                border: 1px solid rgba(255, 255, 255, 0.1);
                transition: all 0.3s ease;
                min-height: 280px;
                display: flex;
                flex-direction: column;
              """,

              span(
                styleAttr := s"""
                  position: absolute;
                  top: ${Spacing.sm};
                  right: ${Spacing.md};
                  font-size: 96px;
                  font-weight: ${Typography.FontWeight.extrabold};
                  color: rgba(255, 255, 255, 0.08);
                  z-index: 0;
                  line-height: 1;
                """,
                "03"
              ),

              div(
                styleAttr := "position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column;",

                div(
                  styleAttr := s"""
                    width: 48px;
                    height: 48px;
                    background-color: ${Colors.highlights};
                    border-radius: ${BorderRadius.md};
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: ${Spacing.md};
                  """,
                  span(styleAttr := "font-size: 20px;", "✨")
                ),

                h3(
                  styleAttr := s"""
                    font-size: ${Typography.FontSize.lg};
                    font-weight: ${Typography.FontWeight.bold};
                    color: ${Colors.white};
                    margin-bottom: ${Spacing.sm};
                  """,
                  "We Handle Everything"
                ),

                p(
                  styleAttr := s"""
                    color: rgba(255, 255, 255, 0.8);
                    line-height: 1.5;
                    font-size: ${Typography.FontSize.sm};
                  """,
                  "From professional photos to guest communications, cleaning to maintenance. You just... chill."
                )
              )
            ),

            // Step 4
            div(
              cls := "process-card",
              animateOnScroll(delaySeconds = 0.45, initialYOffsetPx = 30),
              styleAttr := s"""
                position: relative;
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(10px);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                border: 1px solid rgba(255, 255, 255, 0.1);
                transition: all 0.3s ease;
                min-height: 280px;
                display: flex;
                flex-direction: column;
              """,

              span(
                styleAttr := s"""
                  position: absolute;
                  top: ${Spacing.sm};
                  right: ${Spacing.md};
                  font-size: 96px;
                  font-weight: ${Typography.FontWeight.extrabold};
                  color: rgba(255, 255, 255, 0.08);
                  z-index: 0;
                  line-height: 1;
                """,
                "04"
              ),

              div(
                styleAttr := "position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column;",

                div(
                  styleAttr := s"""
                    width: 48px;
                    height: 48px;
                    background-color: ${Colors.highlights};
                    border-radius: ${BorderRadius.md};
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: ${Spacing.md};
                  """,
                  span(styleAttr := "font-size: 20px;", "💰")
                ),

                h3(
                  styleAttr := s"""
                    font-size: ${Typography.FontSize.lg};
                    font-weight: ${Typography.FontWeight.bold};
                    color: ${Colors.white};
                    margin-bottom: ${Spacing.sm};
                  """,
                  "Get Paid"
                ),

                p(
                  styleAttr := s"""
                    color: rgba(255, 255, 255, 0.8);
                    line-height: 1.5;
                    font-size: ${Typography.FontSize.sm};
                  """,
                  "Watch the deposits hit your account like clockwork. That's the Kember way."
                )
              )
            )
          )
        )
      )            
}

