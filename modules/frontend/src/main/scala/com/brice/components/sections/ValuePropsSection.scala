package com.brice.components.sections

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.brice.theme.DesignTokens.*
import com.brice.components.common.BriceComponents.*
import com.brice.components.common.Directives.*


object ValuePropsSection {
   
    def apply(): HtmlElement =
        // Value Propositions section
      div(
        styleAttr := s"""
          position: relative;
          padding: ${Spacing.xxxl} 0;
          background-color: ${Colors.white};
          overflow: hidden;
        """,

        // Subtle background texture (radial dots)
        div(
          styleAttr := s"""
            position: absolute;
            inset: 0;
            opacity: 0.02;
            background-image: radial-gradient(${Colors.primary} 1px, transparent 1px);
            background-size: 30px 30px;
          """
        ),

        // Content container
        div(
          cls := "container",

          // Header section
          div(
            styleAttr := s"""
              text-align: center;
              margin-bottom: ${Spacing.xxxl};
            """,
            h2(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xxxl};
                font-weight: ${Typography.FontWeight.bold};
                color: ${Colors.text};
                margin-bottom: ${Spacing.md};
              """,
              "Why Partner With ",
              span(
                styleAttr := s"color: ${Colors.primary};",
                "Kember"
              ),
              "?"
            ),
            p(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xl};
                color: rgba(44, 44, 44, 0.6);
                max-width: 700px;
                margin: 0 auto;
              """,
              "We're not just another rental company. We're your property's biggest fan."
            )
          ),

          // Value cards grid
          grid(4)(
            // Guaranteed Rent
            div(
              animateOnScroll(delaySeconds = 0.1, initialYOffsetPx = 20),
              styleAttr := s"""
                background-color: rgba(245, 241, 235, 0.5);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                height: 100%;
                border: 1px solid transparent;
                transition: all 0.3s ease;
              """,
              div(
                styleAttr := s"""
                  width: 56px;
                  height: 56px;
                  border-radius: ${BorderRadius.lg};
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin-bottom: ${Spacing.lg};
                  background-color: rgba(45, 90, 71, 0.08);
                  transition: transform 0.3s ease;
                """,
                span(styleAttr := s"font-size: 28px; color: ${Colors.primary};", "🛡️")
              ),
              h3(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.sm};
                """,
                "Guaranteed Rent"
              ),
              p(
                styleAttr := s"""
                  color: rgba(44, 44, 44, 0.6);
                  line-height: ${Typography.LineHeight.relaxed};
                """,
                "We pay you consistently, whether the unit is booked or not. Sleep easy knowing your income is secure."
              )
            ),

            // Property Care
            div(
              animateOnScroll(delaySeconds = 0.2, initialYOffsetPx = 20),
              styleAttr := s"""
                background-color: rgba(245, 241, 235, 0.5);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                height: 100%;
                border: 1px solid transparent;
                transition: all 0.3s ease;
              """,
              div(
                styleAttr := s"""
                  width: 56px;
                  height: 56px;
                  border-radius: ${BorderRadius.lg};
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin-bottom: ${Spacing.lg};
                  background-color: rgba(122, 158, 126, 0.08);
                  transition: transform 0.3s ease;
                """,
                span(styleAttr := s"font-size: 28px; color: ${Colors.secondary};", "🏠")
              ),
              h3(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.sm};
                """,
                "Property Care"
              ),
              p(
                styleAttr := s"""
                  color: rgba(44, 44, 44, 0.6);
                  line-height: ${Typography.LineHeight.relaxed};
                """,
                "Your property is our priority. Professional cleaning, maintenance, and guests who respect the space."
              )
            ),

            // Higher Returns
            div(
              animateOnScroll(delaySeconds = 0.3, initialYOffsetPx = 20),
              styleAttr := s"""
                background-color: rgba(245, 241, 235, 0.5);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                height: 100%;
                border: 1px solid transparent;
                transition: all 0.3s ease;
              """,
              div(
                styleAttr := s"""
                  width: 56px;
                  height: 56px;
                  border-radius: ${BorderRadius.lg};
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin-bottom: ${Spacing.lg};
                  background-color: rgba(196, 169, 98, 0.08);
                  transition: transform 0.3s ease;
                """,
                span(styleAttr := s"font-size: 28px; color: ${Colors.highlights};", "📈")
              ),
              h3(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.sm};
                """,
                "Higher Returns"
              ),
              p(
                styleAttr := s"""
                  color: rgba(44, 44, 44, 0.6);
                  line-height: ${Typography.LineHeight.relaxed};
                """,
                "Short-term rentals can outperform traditional leases. We make it happen without you lifting a finger."
              )
            ),

            // Always Available
            div(
              animateOnScroll(delaySeconds = 0.4, initialYOffsetPx = 20),
              styleAttr := s"""
                background-color: rgba(245, 241, 235, 0.5);
                border-radius: ${BorderRadius.xl};
                padding: ${Spacing.lg};
                height: 100%;
                border: 1px solid transparent;
                transition: all 0.3s ease;
              """,
              div(
                styleAttr := s"""
                  width: 56px;
                  height: 56px;
                  border-radius: ${BorderRadius.lg};
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  margin-bottom: ${Spacing.lg};
                  background-color: rgba(45, 90, 71, 0.08);
                  transition: transform 0.3s ease;
                """,
                span(styleAttr := s"font-size: 28px; color: ${Colors.primary};", "🎧")
              ),
              h3(
                styleAttr := s"""
                  font-size: ${Typography.FontSize.xl};
                  font-weight: ${Typography.FontWeight.bold};
                  color: ${Colors.text};
                  margin-bottom: ${Spacing.sm};
                """,
                "Always Available"
              ),
              p(
                styleAttr := s"""
                  color: rgba(44, 44, 44, 0.6);
                  line-height: ${Typography.LineHeight.relaxed};
                """,
                "Got questions? We're here. Seriously. No automated bots, just real humans who actually care."
              )
            )
          )
        )
      )

}

