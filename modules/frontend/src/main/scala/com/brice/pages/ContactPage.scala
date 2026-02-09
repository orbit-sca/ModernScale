package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.api.ContactApiClient
import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.timers.setTimeout

object ContactPage {

  // Toast notification component
  private def toast(isSuccess: Boolean, message: String, onDismiss: () => Unit): HtmlElement = {
    // Auto-dismiss after 5 seconds for success messages
    if (isSuccess) {
      setTimeout(5000) { onDismiss() }
    }

    div(
      styleAttr := s"""
        position: fixed;
        top: 24px;
        right: 24px;
        max-width: 400px;
        padding: ${Spacing.lg};
        border-radius: ${BorderRadius.lg};
        background-color: ${if (isSuccess) "#10b981" else "#ef4444"};
        color: white;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        z-index: 9999;
        animation: slideIn 0.3s ease-out;
        display: flex;
        align-items: flex-start;
        gap: ${Spacing.md};
      """,
      // Icon
      span(
        styleAttr := "font-size: 24px;",
        if (isSuccess) "✓" else "✕"
      ),
      // Content
      div(
        styleAttr := "flex: 1;",
        div(
          styleAttr := s"""
            font-weight: ${Typography.FontWeight.semibold};
            font-size: ${Typography.FontSize.base};
            margin-bottom: 4px;
          """,
          if (isSuccess) "Success!" else "Error"
        ),
        div(
          styleAttr := s"font-size: ${Typography.FontSize.sm}; opacity: 0.95;",
          message
        )
      ),
      // Close button
      button(
        styleAttr := s"""
          background: transparent;
          border: none;
          color: white;
          cursor: pointer;
          font-size: 18px;
          padding: 0;
          opacity: 0.7;
          transition: opacity 0.2s;
        """,
        onClick --> { _ => onDismiss() },
        "×"
      )
    )
  }

  // CSS keyframes for animations (injected once)
  private def animationStyles(): HtmlElement = styleTag(
    """
      @keyframes slideIn {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
      @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
      }
    """
  )

  // Loading spinner component
  private def loadingSpinner(): HtmlElement =
    span(
      styleAttr := s"""
        display: inline-block;
        width: 20px;
        height: 20px;
        border: 2px solid rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        border-top-color: white;
        animation: spin 0.8s linear infinite;
        margin-right: ${Spacing.sm};
      """
    )

  def apply(): HtmlElement = {

    // Form state
    val nameVar = Var("")
    val emailVar = Var("")
    val messageVar = Var("")
    val submittingVar = Var(false)
    val statusMessageVar = Var[Option[(Boolean, String)]](None) // (isSuccess, message)

    div(
      // Inject CSS animations
      animationStyles(),

      // Toast notification
      child.maybe <-- statusMessageVar.signal.map { statusOpt =>
        statusOpt.map { case (isSuccess, message) =>
          toast(isSuccess, message, () => statusMessageVar.set(None))
        }
      },

      // Hero section
      div(
        styleAttr := s"""
          background: ${Colors.accent};
          padding: 160px 0 ${Spacing.xxxl};
          position: relative;
        """,
        // Grid background
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
          div(
            styleAttr := s"font-family: ${Typography.monoFamily}; font-size: 14px; color: ${Colors.primary}; margin-bottom: ${Spacing.md}; text-align: center;",
            "> ./contact --new"
          ),
          h1(
            styleAttr := s"""
              font-size: ${Typography.FontSize.hero};
              font-weight: ${Typography.FontWeight.bold};
              color: ${Colors.text};
              margin-bottom: ${Spacing.md};
              text-align: center;
            """,
            "Get In ",
            span(styleAttr := s"color: ${Colors.primary};", "Touch")
          ),
          p(
            styleAttr := s"""
              font-size: ${Typography.FontSize.lg};
              color: ${Colors.textLight};
              text-align: center;
              max-width: 600px;
              margin: 0 auto;
            """,
            "Have a project in mind? Let's discuss how I can help bring your ideas to life."
          )
        )
      ),

      // Contact form section
      div(
        styleAttr := s"padding: ${Spacing.xxxl} 0; background: #0d0d0e;",
        div(
          cls := "container",
          div(
            styleAttr := "max-width: 600px; margin: 0 auto;",

            // Contact form
            form(
              styleAttr := s"""
                background: ${Colors.cardBackground};
                border: 1px solid ${Colors.glassBorder};
                padding: ${Spacing.xxl};
                border-radius: ${BorderRadius.lg};
              """,

              onSubmit.preventDefault --> { _ =>
                if (!submittingVar.now()) {
                  submittingVar.set(true)
                  statusMessageVar.set(None)

                  ContactApiClient.submitContact(
                    nameVar.now(),
                    emailVar.now(),
                    messageVar.now()
                  ).foreach { result =>
                    result match {
                      case Right(response) =>
                        statusMessageVar.set(Some((true, response.message)))
                        nameVar.set("")
                        emailVar.set("")
                        messageVar.set("")
                      case Left(error) =>
                        statusMessageVar.set(Some((false, error.error + error.details.map(d => s": $d").getOrElse(""))))
                    }
                    submittingVar.set(false)
                  }
                }
              },

              // Name field
              div(
                styleAttr := s"margin-bottom: ${Spacing.lg};",
                label(
                  styleAttr := s"""
                    display: block;
                    font-weight: ${Typography.FontWeight.semibold};
                    color: ${Colors.text};
                    margin-bottom: ${Spacing.xs};
                  """,
                  "Name"
                ),
                input(
                  typ := "text",
                  required := true,
                  placeholder := "Your name",
                  styleAttr := s"""
                    width: 100%;
                    padding: ${Spacing.md};
                    background: ${Colors.lightGray};
                    border: 1px solid ${Colors.glassBorder};
                    border-radius: ${BorderRadius.md};
                    font-size: ${Typography.FontSize.base};
                    font-family: ${Typography.fontFamily};
                    color: ${Colors.text};
                    transition: border-color 0.2s ease;
                  """,
                  onInput.mapToValue --> nameVar,
                  value <-- nameVar.signal
                )
              ),

              // Email field
              div(
                styleAttr := s"margin-bottom: ${Spacing.lg};",
                label(
                  styleAttr := s"""
                    display: block;
                    font-weight: ${Typography.FontWeight.semibold};
                    color: ${Colors.text};
                    margin-bottom: ${Spacing.xs};
                  """,
                  "Email"
                ),
                input(
                  typ := "email",
                  required := true,
                  placeholder := "your.email@example.com",
                  styleAttr := s"""
                    width: 100%;
                    padding: ${Spacing.md};
                    background: ${Colors.lightGray};
                    border: 1px solid ${Colors.glassBorder};
                    border-radius: ${BorderRadius.md};
                    font-size: ${Typography.FontSize.base};
                    font-family: ${Typography.fontFamily};
                    color: ${Colors.text};
                    transition: border-color 0.2s ease;
                  """,
                  onInput.mapToValue --> emailVar,
                  value <-- emailVar.signal
                )
              ),

              // Message field
              div(
                styleAttr := s"margin-bottom: ${Spacing.xl};",
                label(
                  styleAttr := s"""
                    display: block;
                    font-weight: ${Typography.FontWeight.semibold};
                    color: ${Colors.text};
                    margin-bottom: ${Spacing.xs};
                  """,
                  "Message"
                ),
                textArea(
                  required := true,
                  placeholder := "Tell me about your project...",
                  rows := 6,
                  styleAttr := s"""
                    width: 100%;
                    padding: ${Spacing.md};
                    background: ${Colors.lightGray};
                    border: 1px solid ${Colors.glassBorder};
                    border-radius: ${BorderRadius.md};
                    font-size: ${Typography.FontSize.base};
                    font-family: ${Typography.fontFamily};
                    color: ${Colors.text};
                    resize: vertical;
                    transition: border-color 0.2s ease;
                  """,
                  onInput.mapToValue --> messageVar,
                  value <-- messageVar.signal
                )
              ),

              // Submit button
              button(
                typ := "submit",
                styleAttr := s"""
                  width: 100%;
                  background-color: ${Colors.primary};
                  color: ${Colors.text};
                  padding: ${Spacing.md} ${Spacing.xl};
                  border-radius: ${BorderRadius.md};
                  font-weight: ${Typography.FontWeight.semibold};
                  font-size: ${Typography.FontSize.base};
                  border: none;
                  cursor: pointer;
                  transition: all 0.2s ease;
                  font-family: ${Typography.fontFamily};
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                """,
                disabled <-- submittingVar.signal,
                child <-- submittingVar.signal.map { submitting =>
                  if (submitting) {
                    span(
                      loadingSpinner(),
                      "Sending..."
                    )
                  } else {
                    span("Send Message")
                  }
                }
              )
            ),

            // Contact info
            div(
              styleAttr := s"""
                margin-top: ${Spacing.xxl};
                text-align: center;
              """,
              p(
                styleAttr := s"margin-bottom: ${Spacing.sm}; color: ${Colors.textLight};",
                "Or reach me directly:"
              ),
              a(
                href := "mailto:build@modernscale.dev",
                styleAttr := s"""
                  font-size: ${Typography.FontSize.lg};
                  font-weight: ${Typography.FontWeight.semibold};
                  color: ${Colors.primary};
                  text-decoration: none;
                """,
                "build@modernscale.dev"
              )
            )
          )
        )
      )
    )
  }
}