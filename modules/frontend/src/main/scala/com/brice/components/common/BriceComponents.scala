package com.brice.components.common

import com.raquo.laminar.api.L.{*, given}
import com.brice.theme.DesignTokens.*

/**
 * Reusable Laminar components for Brice.solutions
 *
 * These demonstrate how to use the design tokens in your Laminar components.
 */
object BriceComponents:

  // ==========================================================================
  // Button Components
  // ==========================================================================

  def primaryButton(text: String, clickHandler: () => Unit = () => ()): HtmlElement =
    button(
      text,
      cls := "brice-btn-primary",
      styleAttr := s"""
        background-color: ${Colors.buttonPrimary};
        color: ${Colors.white};
        padding: ${Button.paddingPrimary};
        border-radius: ${Button.borderRadius};
        font-weight: ${Button.fontWeight};
        font-size: ${Button.fontSize};
        border: none;
        cursor: pointer;
        transition: ${Button.transition};
        font-family: ${Typography.fontFamily};
      """,
      onClick --> { _ => clickHandler() }
    )

  def secondaryButton(text: String, clickHandler: () => Unit = () => ()): HtmlElement =
    button(
      text,
      cls := "brice-btn-secondary",
      styleAttr := s"""
        background-color: transparent;
        color: ${Colors.text};
        padding: ${Button.paddingPrimary};
        border-radius: ${Button.borderRadius};
        font-weight: ${Button.fontWeight};
        font-size: ${Button.fontSize};
        border: 2px solid ${Colors.text};
        cursor: pointer;
        transition: ${Button.transition};
        font-family: ${Typography.fontFamily};
      """,
      onClick --> { _ => clickHandler() }
    )

  // ==========================================================================
  // Badge/Pill Component
  // ==========================================================================

  def badge(text: String, icon: Option[String] = None): HtmlElement =
    div(
      cls := "brice-badge",
      styleAttr := s"""
        display: inline-flex;
        align-items: center;
        gap: ${Spacing.sm};
        background-color: ${Colors.white};
        padding: ${Spacing.sm} ${Spacing.lg};
        border-radius: ${BorderRadius.pill};
        font-size: ${Typography.FontSize.sm};
        border: 1px solid ${Colors.lightGray};
      """,
      icon.map(iconSvg => span(iconSvg)).toList,
      span(text)
    )

  // ==========================================================================
  // Feature Card Component
  // ==========================================================================

  def featureCard(
    icon: String,
    title: String,
    description: String
  ): HtmlElement =
    div(
      cls := "brice-feature-card",
      styleAttr := s"""
        background-color: ${Card.background};
        padding: ${Card.padding};
        border-radius: ${Card.borderRadius};
        box-shadow: ${Card.shadow};
      """,
      div(
        cls := "icon",
        styleAttr := s"""
          width: ${Icon.medium};
          height: ${Icon.medium};
          margin-bottom: ${Spacing.md};
          color: ${Colors.primary};
        """,
        icon // SVG or icon element
      ),
      h3(
        title,
        styleAttr := s"""
          font-size: ${Typography.FontSize.xl};
          font-weight: ${Typography.FontWeight.bold};
          color: ${Colors.textPrimary};
          margin-bottom: ${Spacing.sm};
        """
      ),
      p(
        description,
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.textSecondary};
          line-height: ${Typography.LineHeight.relaxed};
        """
      )
    )

  // ==========================================================================
  // Process Card Component (with number)
  // ==========================================================================

  def processCard(
    number: String,
    icon: String,
    title: String,
    description: String
  ): HtmlElement =
    div(
      cls := "brice-process-card",
      styleAttr := s"""
        position: relative;
        background-color: rgba(45, 90, 71, 0.7);
        padding: ${Card.padding};
        border-radius: ${Card.borderRadius};
        overflow: hidden;
      """,
      // Large background number
      div(
        number,
        styleAttr := s"""
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          font-size: 120px;
          font-weight: ${Typography.FontWeight.extrabold};
          color: rgba(255, 255, 255, 0.05);
          z-index: 0;
        """
      ),
      // Content
      div(
        styleAttr := "position: relative; z-index: 1;",
        // Icon container
        div(
          styleAttr := s"""
            width: ${Icon.large};
            height: ${Icon.large};
            background-color: ${Colors.highlights};
            border-radius: ${BorderRadius.md};
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: ${Spacing.md};
          """,
          icon
        ),
        h3(
          title,
          styleAttr := s"""
            font-size: ${Typography.FontSize.xl};
            font-weight: ${Typography.FontWeight.bold};
            color: ${Colors.white};
            margin-bottom: ${Spacing.sm};
          """
        ),
        p(
          description,
          styleAttr := s"""
            font-size: ${Typography.FontSize.sm};
            color: rgba(255, 255, 255, 0.8);
            line-height: ${Typography.LineHeight.relaxed};
          """
        )
      )
    )

  // ==========================================================================
  // Form Input Component
  // ==========================================================================

  def textInput(
    placeholderText: String,
    inputType: String = "text"
  ): HtmlElement =
    input(
      typ := inputType,
      cls := "brice-input",
      placeholder := placeholderText,
      styleAttr := s"""
        width: 100%;
        padding: ${Input.padding};
        border-radius: ${Input.borderRadius};
        background-color: ${Input.background};
        border: 1px solid transparent;
        font-size: ${Input.fontSize};
        font-family: ${Typography.fontFamily};
        transition: ${Transition.normal};
      """
    )

  def textAreaInput(
    placeholderText: String,
    numRows: Int = 4
  ): HtmlElement =
    textArea(
      cls := "brice-textarea",
      placeholder := placeholderText,
      rows := numRows,
      styleAttr := s"""
        width: 100%;
        padding: ${Input.padding};
        border-radius: ${Input.borderRadius};
        background-color: ${Input.background};
        border: 1px solid transparent;
        font-size: ${Input.fontSize};
        font-family: ${Typography.fontFamily};
        resize: vertical;
        transition: ${Transition.normal};
      """
    )

  // ==========================================================================
  // Section Container
  // ==========================================================================

  def section(children: HtmlElement*): HtmlElement =
    div(
      cls := "brice-section",
      styleAttr := s"""
        max-width: ${Layout.containerMaxWidth};
        margin: 0 auto;
        padding: ${Spacing.section} ${Layout.containerPadding};
      """,
      children
    )

  // ==========================================================================
  // Grid Layout (Responsive)
  // ==========================================================================

  def grid(columns: Int = 4)(children: HtmlElement*): HtmlElement =
    val gridClass = columns match
      case 4 => "responsive-grid-4"
      case _ => "kember-grid"

    val gridTemplate = columns match
      case 1 => Layout.gridColumns1
      case 2 => Layout.gridColumns2
      case _ => Layout.gridColumns4

    div(
      cls := gridClass,
      // Only add inline styles for non-4-column grids (4-column uses CSS class)
      if (columns != 4) styleAttr := s"""
        display: grid;
        grid-template-columns: $gridTemplate;
        gap: ${Layout.gridGap};
      """ else emptyMod,
      children
    )
