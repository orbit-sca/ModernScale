package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.routing.{AppRouter, HomePage}

object NotFoundPage {

  def apply(): HtmlElement =
    div(
      styleAttr := s"""
        min-height: 80vh;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        text-align: center;
        padding: ${Spacing.xxxl};
        background: linear-gradient(135deg, ${Colors.accent} 0%, ${Colors.white} 100%);
      """,

      // 404 number
      div(
        styleAttr := s"""
          font-size: 120px;
          font-weight: ${Typography.FontWeight.bold};
          color: ${Colors.primary};
          line-height: 1;
          margin-bottom: ${Spacing.lg};
          opacity: 0.9;
        """,
        "404"
      ),

      // Main heading
      h1(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxxl};
          font-weight: ${Typography.FontWeight.bold};
          color: ${Colors.text};
          margin-bottom: ${Spacing.md};
        """,
        "Page Not Found"
      ),

      // Description
      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.lg};
          color: ${Colors.textSecondary};
          max-width: 500px;
          margin-bottom: ${Spacing.xl};
          line-height: ${Typography.LineHeight.relaxed};
        """,
        "Oops! The page you're looking for doesn't exist or has been moved. Let's get you back on track."
      ),

      // Back to Home button
      button(
        styleAttr := s"""
          background-color: ${Colors.primary};
          color: ${Colors.white};
          padding: ${Spacing.md} ${Spacing.xxl};
          border-radius: ${BorderRadius.pill};
          font-weight: ${Typography.FontWeight.semibold};
          font-size: ${Typography.FontSize.base};
          border: none;
          cursor: pointer;
          transition: all 0.3s ease;
          font-family: ${Typography.fontFamily};
          display: inline-flex;
          align-items: center;
          gap: ${Spacing.sm};
        """,
        onClick --> { _ => AppRouter.navigateTo(HomePage) },
        span("Back to Home"),
        span("→")
      ),

      // Additional helpful links
      div(
        styleAttr := s"""
          margin-top: ${Spacing.xxl};
          display: flex;
          gap: ${Spacing.lg};
        """,
        a(
          href := "/about",
          styleAttr := s"""
            color: ${Colors.primary};
            text-decoration: none;
            font-weight: ${Typography.FontWeight.medium};
            transition: color 0.3s ease;
          """,
          onClick.preventDefault --> { _ => AppRouter.navigateTo(com.brice.routing.AboutPage) },
          "About Us"
        ),
        a(
          href := "/contact",
          styleAttr := s"""
            color: ${Colors.primary};
            text-decoration: none;
            font-weight: ${Typography.FontWeight.medium};
            transition: color 0.3s ease;
          """,
          onClick.preventDefault --> { _ => AppRouter.navigateTo(com.brice.routing.ContactPage) },
          "Contact Us"
        )
      )
    )
}
