package com.brice.services

import zio.*
import com.brice.domain.*
import com.brice.clients.BlockchainApiClient
import com.brice.repositories.TransactionRepository
import java.time.{Instant, LocalDate, ZoneOffset}

/**
 * Service for ingesting and normalizing blockchain transaction data.
 *
 * Design principles:
 * - Pure functional transformation pipeline
 * - Filter invalid/failed transactions
 * - Normalize addresses and amounts
 * - Enrich with USD prices
 * - Idempotent (can be run multiple times safely)
 */
trait TransactionIngestionService:
  /** Ingest transactions for a specific config and normalize them */
  def ingestTransactions(
    config: IngestionConfig
  ): Task[IngestionResult]

object TransactionIngestionService:

  final class Live(
    apiClient: BlockchainApiClient,
    priceService: PriceService,
    repository: TransactionRepository
  ) extends TransactionIngestionService:

    override def ingestTransactions(
      config: IngestionConfig
    ): Task[IngestionResult] =
      for
        // Step 1: Fetch raw transactions from blockchain API
        rawTxs <- apiClient.fetchTransactions(
          config.network,
          config.walletAddress,
          config.apiKey,
          config.startBlock,
          config.endBlock
        )
        _ <- ZIO.logInfo(s"Fetched ${rawTxs.length} raw transactions from ${config.network.displayName}")

        // Step 2: Filter and normalize transactions
        normalizedTxs <- ZIO.foreach(rawTxs)(tx => normalizeTransaction(tx, config.network))
          .map(_.flatten)  // Remove failed normalizations
        _ <- ZIO.logInfo(s"Normalized ${normalizedTxs.length} valid transactions")

        // Step 3: Enrich with USD prices
        enrichedTxs <- enrichWithPrices(normalizedTxs, config.network)
        _ <- ZIO.logInfo(s"Enriched ${enrichedTxs.length} transactions with USD prices")

        // Step 4: Persist to database
        persistedCount <- repository.upsertBatch(enrichedTxs)
        _ <- ZIO.logInfo(s"Persisted ${persistedCount} transactions to database")

        result = IngestionResult(
          network = config.network,
          totalFetched = rawTxs.length,
          totalNormalized = normalizedTxs.length,
          totalPersisted = persistedCount,
          failures = List.empty  // Track failures if needed
        )
      yield result

    /**
     * Normalize a raw transaction into our canonical format.
     * Returns None if the transaction should be filtered out.
     */
    private def normalizeTransaction(
      raw: RawTransaction,
      network: BlockchainNetwork
    ): Task[Option[TransactionRow]] =
      ZIO.attempt {
        // Filter out failed transactions
        if raw.isError != "0" then
          None
        else
          // Parse timestamp
          val timestamp = Instant.ofEpochSecond(raw.timeStamp.toLong)
          val date = LocalDate.ofInstant(timestamp, ZoneOffset.UTC)

          // Convert wei to native token (e.g., wei to ETH)
          val amountNative = weiToNative(BigInt(raw.value))

          // Filter zero-value transactions (optional - can be configured)
          if amountNative == BigDecimal(0) then
            None
          else
            // Normalize addresses to lowercase
            val fromAddress = raw.from.toLowerCase
            val toAddress = raw.to.toLowerCase

            // Parse gas data
            val gasUsed = Some(raw.gasUsed.toLong)
            val gasPriceGwei = Some(weiToGwei(BigInt(raw.gasPrice)))

            Some(TransactionRow(
              txHash = raw.hash,
              timestamp = timestamp,
              date = date,
              chain = network.displayName,
              fromAddress = fromAddress,
              toAddress = toAddress,
              amountNative = amountNative,
              amountUsd = BigDecimal(0),  // Will be filled in enrichment step
              gasUsed = gasUsed,
              gasPriceGwei = gasPriceGwei
            ))
      }.catchAll { err =>
        ZIO.logWarning(s"Failed to normalize transaction ${raw.hash}: ${err.getMessage}") *>
        ZIO.succeed(None)
      }

    /**
     * Enrich normalized transactions with USD prices.
     */
    private def enrichWithPrices(
      transactions: List[TransactionRow],
      network: BlockchainNetwork
    ): Task[List[TransactionRow]] =
      if transactions.isEmpty then
        ZIO.succeed(List.empty)
      else
        val token = network.nativeToken
        val dates = transactions.map(_.date).distinct

        // Batch fetch prices for all unique dates
        priceService.getPricesUsd(token, dates).flatMap { priceMap =>
          ZIO.foreach(transactions) { tx =>
            priceMap.get(tx.date) match
              case Some(priceUsd) =>
                val amountUsd = tx.amountNative * priceUsd
                ZIO.succeed(tx.copy(amountUsd = amountUsd))
              case None =>
                ZIO.logWarning(s"No price found for $token on ${tx.date}, using 0") *>
                ZIO.succeed(tx)
          }
        }

    /** Convert wei (10^-18) to native token */
    private def weiToNative(wei: BigInt): BigDecimal =
      BigDecimal(wei) / BigDecimal(10).pow(18)

    /** Convert wei to Gwei (10^-9) */
    private def weiToGwei(wei: BigInt): BigDecimal =
      BigDecimal(wei) / BigDecimal(10).pow(9)

  end Live

  /** ZLayer for live implementation */
  val live: ZLayer[BlockchainApiClient & PriceService & TransactionRepository, Nothing, TransactionIngestionService] =
    ZLayer.fromFunction(Live.apply)

  /** Accessor method */
  def ingestTransactions(
    config: IngestionConfig
  ): ZIO[TransactionIngestionService, Throwable, IngestionResult] =
    ZIO.serviceWithZIO[TransactionIngestionService](_.ingestTransactions(config))
