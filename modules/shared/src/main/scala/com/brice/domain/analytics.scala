package com.brice.domain

import zio.json.*
import java.time.{Instant, LocalDate}

/**
 * Canonical domain model for blockchain transaction analytics.
 * Each row represents exactly one blockchain transaction.
 *
 * Design principles:
 * - One row = one transaction (no aggregation)
 * - All normalization happens before persistence
 * - Immutable and serializable for Excel export
 */
final case class TransactionRow(
  txHash: String,              // Unique transaction hash (primary key)
  timestamp: Instant,          // Precise transaction time
  date: LocalDate,             // Date for grouping (derived from timestamp)
  chain: String,               // Normalized chain name (e.g., "Ethereum", "Base")
  fromAddress: String,         // Sender address (lowercase normalized)
  toAddress: String,           // Receiver address (lowercase normalized)
  amountNative: BigDecimal,    // Amount in native token (e.g., ETH)
  amountUsd: BigDecimal,       // USD value at transaction date
  gasUsed: Option[Long] = None,       // Gas used for the transaction
  gasPriceGwei: Option[BigDecimal] = None  // Gas price in Gwei
)

object TransactionRow:
  given JsonCodec[TransactionRow] = DeriveJsonCodec.gen[TransactionRow]

  /** CSV header row for Excel export */
  val csvHeader: String =
    "date,chain,tx_hash,from_address,to_address,amount_native,amount_usd,gas_used,gas_price_gwei"

  /** Convert a transaction row to CSV format */
  extension (tx: TransactionRow)
    def toCsv: String =
      val gasUsedStr = tx.gasUsed.map(_.toString).getOrElse("")
      val gasPriceStr = tx.gasPriceGwei.map(_.toString).getOrElse("")
      s"${tx.date},${tx.chain},${tx.txHash},${tx.fromAddress},${tx.toAddress},${tx.amountNative},${tx.amountUsd},$gasUsedStr,$gasPriceStr"

/**
 * Supported blockchain networks.
 * Each network has an API endpoint and native token.
 */
enum BlockchainNetwork:
  case Ethereum
  case Base
  case Polygon
  case Arbitrum
  case Optimism

object BlockchainNetwork:
  // Custom JSON codec to serialize/deserialize as strings
  given JsonCodec[BlockchainNetwork] = JsonCodec(
    JsonEncoder.string.contramap(_.toString),
    JsonDecoder.string.mapOrFail { str =>
      str match
        case "Ethereum" => Right(Ethereum)
        case "Base"     => Right(Base)
        case "Polygon"  => Right(Polygon)
        case "Arbitrum" => Right(Arbitrum)
        case "Optimism" => Right(Optimism)
        case _          => Left(s"Unknown blockchain network: $str")
    }
  )

  given ordering: Ordering[BlockchainNetwork] = Ordering.by(_.ordinal)

  extension (network: BlockchainNetwork)
    /** Canonical display name for the network */
    def displayName: String = network match
      case Ethereum => "Ethereum"
      case Base     => "Base"
      case Polygon  => "Polygon"
      case Arbitrum => "Arbitrum"
      case Optimism => "Optimism"

    /** Native token symbol */
    def nativeToken: String = network match
      case Ethereum => "ETH"
      case Base     => "ETH"
      case Polygon  => "MATIC"
      case Arbitrum => "ETH"
      case Optimism => "ETH"

/**
 * Raw transaction data from blockchain API (before normalization).
 * This represents the shape of data from Etherscan-style APIs.
 */
final case class RawTransaction(
  hash: String,
  blockNumber: String,
  timeStamp: String,           // Unix timestamp as string
  from: String,
  to: String,
  value: String,               // Value in wei as string
  gas: String,
  gasPrice: String,            // Gas price in wei as string
  gasUsed: String,
  isError: String              // "0" for success, "1" for error
)

object RawTransaction:
  given JsonCodec[RawTransaction] = DeriveJsonCodec.gen[RawTransaction]

/**
 * USD price lookup for a specific date and chain.
 * Used to convert native token amounts to USD.
 */
final case class DailyPrice(
  date: LocalDate,
  chain: String,
  token: String,               // e.g., "ETH", "MATIC"
  priceUsd: BigDecimal
)

object DailyPrice:
  given JsonCodec[DailyPrice] = DeriveJsonCodec.gen[DailyPrice]

/**
 * Configuration for blockchain data ingestion
 */
final case class IngestionConfig(
  network: BlockchainNetwork,
  walletAddress: String,
  apiKey: String,
  startBlock: Option[Long] = None,
  endBlock: Option[Long] = None
)

object IngestionConfig:
  given JsonCodec[IngestionConfig] = DeriveJsonCodec.gen[IngestionConfig]

/**
 * Result of a transaction ingestion batch
 */
final case class IngestionResult(
  network: BlockchainNetwork,
  totalFetched: Int,
  totalNormalized: Int,
  totalPersisted: Int,
  failures: List[String] = List.empty
)

object IngestionResult:
  given JsonCodec[IngestionResult] = DeriveJsonCodec.gen[IngestionResult]

/**
 * Chain-level statistics for analytics dashboard
 */
final case class ChainStats(
  chain: String,
  count: Int,
  volumeUsd: BigDecimal
)

object ChainStats:
  given JsonCodec[ChainStats] = DeriveJsonCodec.gen[ChainStats]

/**
 * Aggregate analytics statistics for dashboard
 */
final case class AnalyticsStats(
  totalTransactions: Long,
  totalVolumeUsd: BigDecimal,
  avgTransactionUsd: BigDecimal,
  uniqueChains: List[String],
  uniqueAddresses: Int,
  earliestDate: LocalDate,
  latestDate: LocalDate,
  chainBreakdown: List[ChainStats]
)

object AnalyticsStats:
  given JsonCodec[AnalyticsStats] = DeriveJsonCodec.gen[AnalyticsStats]

/**
 * Error response for analytics API
 */
final case class AnalyticsError(
  error: String,
  details: Option[String] = None
)

object AnalyticsError:
  given JsonCodec[AnalyticsError] = DeriveJsonCodec.gen[AnalyticsError]
