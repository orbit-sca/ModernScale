package com.brice.services

import zio.*
import com.brice.domain.{BlockchainNetwork, DailyPrice}
import java.time.LocalDate

/**
 * Service for retrieving historical cryptocurrency prices.
 *
 * Design principles:
 * - Effect-safe abstraction for price lookups
 * - Cacheable by (date, token) pair
 * - Extensible to multiple price data providers
 */
trait PriceService:
  /** Get USD price for a token on a specific date */
  def getPriceUsd(
    token: String,
    date: LocalDate
  ): Task[Option[BigDecimal]]

  /** Get prices for multiple dates at once (batch optimization) */
  def getPricesUsd(
    token: String,
    dates: List[LocalDate]
  ): Task[Map[LocalDate, BigDecimal]]

object PriceService:

  /**
   * In-memory implementation with hardcoded/mock price data.
   * This is a placeholder - in production, you'd integrate with:
   * - CoinGecko API
   * - CoinMarketCap API
   * - Your own price database
   */
  final class InMemory extends PriceService:

    // Mock daily prices for ETH (simplified for demonstration)
    // In production, this would be fetched from an external API or database
    private val mockPrices: Map[String, BigDecimal] = Map(
      "ETH" -> BigDecimal("2000.00"),
      "MATIC" -> BigDecimal("0.80")
    )

    override def getPriceUsd(
      token: String,
      date: LocalDate
    ): Task[Option[BigDecimal]] =
      ZIO.succeed(mockPrices.get(token))
        .tap(price => ZIO.logDebug(s"Price lookup for $token on $date: $price"))

    override def getPricesUsd(
      token: String,
      dates: List[LocalDate]
    ): Task[Map[LocalDate, BigDecimal]] =
      mockPrices.get(token) match
        case Some(price) =>
          // Return the same price for all dates (simplified)
          ZIO.succeed(dates.map(d => d -> price).toMap)
        case None =>
          ZIO.succeed(Map.empty)

  end InMemory

  /**
   * CoinGecko implementation (stub for future implementation).
   * CoinGecko provides free historical price data via their API.
   */
  final class CoinGecko(apiKey: Option[String] = None) extends PriceService:
    // TODO: Implement CoinGecko API integration
    // https://www.coingecko.com/en/api/documentation

    override def getPriceUsd(
      token: String,
      date: LocalDate
    ): Task[Option[BigDecimal]] =
      ZIO.fail(new NotImplementedError("CoinGecko integration not yet implemented"))

    override def getPricesUsd(
      token: String,
      dates: List[LocalDate]
    ): Task[Map[LocalDate, BigDecimal]] =
      ZIO.fail(new NotImplementedError("CoinGecko integration not yet implemented"))

  end CoinGecko

  /** ZLayer for in-memory implementation */
  val inMemory: ULayer[PriceService] =
    ZLayer.succeed(new InMemory)

  /** ZLayer for CoinGecko implementation */
  def coinGecko(apiKey: Option[String] = None): ULayer[PriceService] =
    ZLayer.succeed(new CoinGecko(apiKey))

  /** Accessor methods */
  def getPriceUsd(
    token: String,
    date: LocalDate
  ): ZIO[PriceService, Throwable, Option[BigDecimal]] =
    ZIO.serviceWithZIO[PriceService](_.getPriceUsd(token, date))

  def getPricesUsd(
    token: String,
    dates: List[LocalDate]
  ): ZIO[PriceService, Throwable, Map[LocalDate, BigDecimal]] =
    ZIO.serviceWithZIO[PriceService](_.getPricesUsd(token, dates))
