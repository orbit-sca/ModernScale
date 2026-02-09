package com.brice.pages

import com.raquo.laminar.api.L._
import com.brice.theme.DesignTokens.*
import com.brice.api.AnalyticsApiClient
import com.brice.domain.*
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.format.DateTimeFormatter

/** Blockchain transaction analytics dashboard */
object BlockchainAnalyticsPage:

  def apply(): HtmlElement =
    // State management
    val transactionsVar = Var[List[TransactionRow]](List.empty)
    val statsVar = Var(Option.empty[AnalyticsStats])
    val isLoading = Var(true)
    val errorMessage = Var(Option.empty[String])

    // Fetch data on mount
    val fetchData = Observer[Unit] { _ =>
      isLoading.set(true)
      errorMessage.set(None)

      // Fetch transactions
      AnalyticsApiClient.listTransactions(limit = Some(50)).foreach {
        case Right(txs) =>
          transactionsVar.set(txs)
          isLoading.set(false)
        case Left(error) =>
          errorMessage.set(Some(error.error))
          isLoading.set(false)
      }

      // Fetch stats
      AnalyticsApiClient.getStats().foreach {
        case Right(stats) =>
          statsVar.set(Some(stats))
        case Left(_) => ()
      }
    }

    div(
      cls := "blockchain-analytics-page",
      styleAttr := s"min-height: 100vh; background: ${Colors.accent};",
      onMountCallback(_ => fetchData.onNext(())),

      // Background decorations
      backgroundDecorations(),

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

          // Header
          header(),

          // Stats cards
          child <-- statsVar.signal.map {
            case Some(stats) => statsCardsSection(stats)
            case None => emptyNode
          },

          // Transactions list
          div(
            styleAttr := s"margin-top: ${Spacing.xxl};",

            h2(
              styleAttr := s"""
                font-size: ${Typography.FontSize.xl};
                font-weight: ${Typography.FontWeight.medium};
                color: ${Colors.text};
                margin-bottom: ${Spacing.lg};
              """,
              "Recent Transactions"
            ),

            div(
              styleAttr := s"display: grid; gap: ${Spacing.lg};",

              // Loading state
              child <-- isLoading.signal.map { loading =>
                if loading then loadingSpinner() else emptyNode
              },

              // Error state
              child <-- errorMessage.signal.map {
                case Some(error) => errorDisplay(error)
                case None => emptyNode
              },

              // Transaction cards
              children <-- transactionsVar.signal.combineWith(isLoading.signal).map {
                case (txs, loading) =>
                  if !loading && txs.isEmpty then
                    List(emptyState())
                  else
                    txs.zipWithIndex.map { case (tx, index) =>
                      transactionCard(tx, index)
                    }
              }
            )
          )
        )
      )
    )

  // ========== UI Components ==========

  private def backgroundDecorations(): HtmlElement =
    div(
      // Grid pattern
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

      // Glowing orb
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
      )
    )

  private def header(): HtmlElement =
    div(
      styleAttr := s"margin-bottom: ${Spacing.xxl};",

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
        chartIcon,
        span(
          styleAttr := s"""
            font-family: ${Typography.monoFamily};
            font-size: 11px;
            color: ${Colors.textLight};
            text-transform: uppercase;
            letter-spacing: 0.15em;
          """,
          "Live Data"
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
        "Blockchain Analytics",
        span(styleAttr := s"color: ${Colors.primary};", ".")
      ),

      // Description
      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.lg};
          color: ${Colors.textLight};
          max-width: 700px;
          line-height: 1.7;
        """,
        "Real-time blockchain transaction data and insights from multiple networks."
      )
    )

  private def statsCardsSection(stats: AnalyticsStats): HtmlElement =
    div(
      styleAttr := s"""
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(min(100%, 250px), 1fr));
        gap: ${Spacing.lg};
        margin-bottom: ${Spacing.xxl};
      """,

      statCard(
        "Total Transactions",
        stats.totalTransactions.toString,
        "All networks combined"
      ),

      statCard(
        "Total Volume",
        f"$$${stats.totalVolumeUsd}%.0f",
        "USD equivalent"
      ),

      statCard(
        "Average Transaction",
        f"$$${stats.avgTransactionUsd}%.2f",
        "Mean transaction value"
      ),

      statCard(
        "Date Range",
        s"${stats.earliestDate.getYear} - ${stats.latestDate.getYear}",
        s"${stats.chainBreakdown.length} chains"
      )
    )

  private def statCard(title: String, value: String, subtitle: String): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.lg};
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        transition: transform 0.2s ease, border-color 0.2s ease;
        animation: fadeInUp 0.6s ease both;
      """,

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.sm};
          color: ${Colors.textLight};
          margin: 0 0 ${Spacing.sm} 0;
          text-transform: uppercase;
          letter-spacing: 0.05em;
        """,
        title
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xxl};
          font-weight: ${Typography.FontWeight.medium};
          color: ${Colors.text};
          margin: 0 0 ${Spacing.xs} 0;
        """,
        value
      ),

      p(
        styleAttr := s"""
          font-size: ${Typography.FontSize.xs};
          color: ${Colors.textLight};
          margin: 0;
        """,
        subtitle
      )
    )

  private def transactionCard(tx: TransactionRow, index: Int): HtmlElement =
    div(
      styleAttr := s"""
        padding: ${Spacing.lg};
        background: ${Colors.cardBackground};
        border: 1px solid ${Colors.glassBorder};
        border-radius: ${BorderRadius.lg};
        transition: all 0.2s ease;
        animation: fadeInUp 0.6s ease ${index * 0.03}s both;
        &:hover {
          border-color: rgba(217, 38, 38, 0.5);
          transform: translateY(-2px);
        }
      """,

      // Row 1: Hash + Chain
      div(
        styleAttr := "display: flex; justify-content: space-between; align-items: start; margin-bottom: 12px;",

        span(
          styleAttr := s"""
            font-family: ${Typography.monoFamily};
            font-size: 12px;
            color: ${Colors.textLight};
            word-break: break-all;
          """,
          tx.txHash.take(16) + "..."
        ),

        span(
          styleAttr := s"""
            background: ${Colors.primary};
            color: white;
            padding: 4px 12px;
            border-radius: ${BorderRadius.sm};
            font-size: 12px;
            font-weight: ${Typography.FontWeight.medium};
            white-space: nowrap;
          """,
          tx.chain
        )
      ),

      // Row 2: Amount + USD
      div(
        styleAttr := "margin-bottom: 8px;",

        span(
          styleAttr := s"""
            color: ${Colors.text};
            font-size: ${Typography.FontSize.lg};
            font-weight: ${Typography.FontWeight.medium};
          """,
          f"${tx.amountNative}%.4f ETH"
        ),

        span(
          styleAttr := s"""
            color: ${Colors.textLight};
            margin-left: ${Spacing.sm};
            font-size: ${Typography.FontSize.base};
          """,
          f"≈ $$${tx.amountUsd}%.2f"
        )
      ),

      // Row 3: Date + Gas
      div(
        styleAttr := s"display: flex; gap: ${Spacing.md}; font-size: ${Typography.FontSize.sm}; color: ${Colors.textLight};",

        span(tx.date.toString),

        tx.gasUsed.map { gas =>
          span(s"Gas: $gas")
        }.getOrElse(emptyNode),

        tx.gasPriceGwei.map { price =>
          span(f"$price%.2f Gwei")
        }.getOrElse(emptyNode)
      )
    )

  private def loadingSpinner(): HtmlElement =
    div(
      styleAttr := "text-align: center; padding: 80px 0;",
      div(
        styleAttr := s"""
          display: inline-block;
          width: 32px;
          height: 32px;
          border: 2px solid rgba(217, 38, 38, 0.2);
          border-top-color: ${Colors.primary};
          border-radius: 50%;
          animation: spin 1s linear infinite;
        """
      )
    )

  private def errorDisplay(error: String): HtmlElement =
    div(
      styleAttr := s"""
        text-align: center;
        padding: 80px 0;
        color: ${Colors.textLight};
      """,
      p(
        styleAttr := s"font-size: ${Typography.FontSize.lg};",
        s"Error: $error"
      )
    )

  private def emptyState(): HtmlElement =
    div(
      styleAttr := s"""
        text-align: center;
        padding: 80px 0;
        color: ${Colors.textLight};
      """,
      chartIconLarge,
      p(
        styleAttr := s"font-size: ${Typography.FontSize.lg}; margin-top: ${Spacing.md};",
        "No transactions found"
      )
    )

  // ========== Icons ==========

  private def chartIcon: SvgElement =
    svg.svg(
      svg.width := "12",
      svg.height := "12",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.primary,
      svg.strokeWidth := "2",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.line(svg.x1 := "18", svg.y1 := "20", svg.x2 := "18", svg.y2 := "10"),
      svg.line(svg.x1 := "12", svg.y1 := "20", svg.x2 := "12", svg.y2 := "4"),
      svg.line(svg.x1 := "6", svg.y1 := "20", svg.x2 := "6", svg.y2 := "14")
    )

  private def chartIconLarge: SvgElement =
    svg.svg(
      svg.width := "64",
      svg.height := "64",
      svg.viewBox := "0 0 24 24",
      svg.fill := "none",
      svg.stroke := Colors.lightGray,
      svg.strokeWidth := "1",
      svg.strokeLineCap := "round",
      svg.strokeLineJoin := "round",
      svg.style := "margin: 0 auto; display: block;",
      svg.line(svg.x1 := "18", svg.y1 := "20", svg.x2 := "18", svg.y2 := "10"),
      svg.line(svg.x1 := "12", svg.y1 := "20", svg.x2 := "12", svg.y2 := "4"),
      svg.line(svg.x1 := "6", svg.y1 := "20", svg.x2 := "6", svg.y2 := "14")
    )
