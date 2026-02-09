package com.brice.theme

/**
 * Design System Tokens for Brice.solutions
 *
 * Dark portfolio theme with charcoal background and red accents.
 * Fonts: Space Grotesk (headings) + JetBrains Mono (code/data)
 */
object DesignTokens:

  // ============================================================================
  // Colors - Dark Portfolio Theme
  // ============================================================================

  object Colors:
    val primary = "#d92626"        // Data Red accent
    val secondary = "#ff4444"      // Lighter red for hover states
    val accent = "#0a0a0b"         // Deep charcoal (background)
    val text = "#ffffff"           // White text
    val textLight = "rgba(255, 255, 255, 0.6)"  // Muted text
    val highlights = "#d92626"     // Red highlights

    val white = "#FFFFFF"
    val black = "#0a0a0b"
    val darkGray = "#1a1a1b"       // Card backgrounds
    val mediumGray = "rgba(255, 255, 255, 0.4)"
    val lightGray = "#2a2a2b"      // Borders

    // Semantic colors
    val background = accent
    val cardBackground = darkGray
    val textPrimary = text
    val textSecondary = textLight
    val buttonPrimary = primary
    val buttonSecondary = lightGray

    // Glassmorphic card styles
    val glassBackground = "rgba(255, 255, 255, 0.05)"
    val glassBorder = "rgba(255, 255, 255, 0.1)"

  // ============================================================================
  // Typography - Space Grotesk + JetBrains Mono
  // ============================================================================

  object Typography:
    val fontFamily = "'Space Grotesk', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"
    val monoFamily = "'JetBrains Mono', 'Fira Code', 'SF Mono', monospace"

    object FontSize:
      val xs = "12px"
      val sm = "14px"
      val base = "16px"
      val lg = "18px"
      val xl = "20px"
      val xxl = "28px"
      val xxxl = "42px"
      val hero = "64px"

    object FontWeight:
      val regular = "400"
      val medium = "500"
      val semibold = "600"
      val bold = "700"
      val extrabold = "800"

    object LineHeight:
      val tight = "1.1"
      val normal = "1.5"
      val relaxed = "1.6"

  // ============================================================================
  // Spacing
  // ============================================================================

  object Spacing:
    val xs = "8px"
    val sm = "12px"
    val md = "16px"
    val lg = "24px"
    val xl = "32px"
    val xxl = "48px"
    val xxxl = "64px"
    val section = "100px"
    val sectionMobile = "60px"

  // ============================================================================
  // Border Radius
  // ============================================================================

  object BorderRadius:
    val sm = "4px"
    val md = "8px"
    val lg = "12px"
    val xl = "16px"
    val pill = "9999px"

  // ============================================================================
  // Shadows - Dark theme shadows
  // ============================================================================

  object Shadow:
    val subtle = "0 2px 8px rgba(0, 0, 0, 0.3)"
    val medium = "0 4px 12px rgba(0, 0, 0, 0.4)"
    val large = "0 8px 24px rgba(0, 0, 0, 0.5)"
    val glow = s"0 0 20px rgba(217, 38, 38, 0.3)"

  object Shadows:
    val sm = Shadow.subtle
    val md = Shadow.medium
    val lg = Shadow.large

  // ============================================================================
  // Layout
  // ============================================================================

  object Layout:
    val containerMaxWidth = "1200px"
    val containerPadding = "80px"
    val containerPaddingMobile = "24px"

    val gridGap = "24px"
    val gridColumns4 = "repeat(4, 1fr)"
    val gridColumns2 = "repeat(2, 1fr)"
    val gridColumns1 = "1fr"

  // ============================================================================
  // Breakpoints
  // ============================================================================

  object Breakpoints:
    val mobile = "768px"
    val tablet = "1024px"
    val desktop = "1280px"

    def mobileOnly = s"@media (max-width: ${mobile})"
    def tabletUp = s"@media (min-width: ${mobile})"
    def desktopUp = s"@media (min-width: ${desktop})"

  // ============================================================================
  // Transitions
  // ============================================================================

  object Transition:
    val fast = "0.15s ease"
    val normal = "0.2s ease"
    val slow = "0.3s ease"

  // ============================================================================
  // Component-Specific Tokens
  // ============================================================================

  object Button:
    val paddingPrimary = s"${Spacing.md} ${Spacing.xl}"
    val borderRadius = BorderRadius.md
    val fontWeight = Typography.FontWeight.semibold
    val fontSize = Typography.FontSize.sm
    val transition = Transition.normal

  object Card:
    val padding = Spacing.xl
    val borderRadius = BorderRadius.lg
    val shadow = Shadow.subtle
    val background = Colors.cardBackground

  object Input:
    val padding = Spacing.md
    val borderRadius = BorderRadius.sm
    val background = Colors.lightGray
    val fontSize = Typography.FontSize.sm

  object Icon:
    val small = "24px"
    val medium = "48px"
    val large = "56px"
