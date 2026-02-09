package com.brice.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import io.getquill.extras.LocalDateOps
import com.brice.domain.TransactionRow
import java.time.{Instant, LocalDate}
import javax.sql.DataSource

/**
 * Repository for persisting and querying blockchain transactions.
 *
 * Design principles:
 * - Idempotent inserts (using ON CONFLICT)
 * - Efficient batch operations
 * - Indexed queries for analytics
 */
trait TransactionRepository:
  /** Insert or update a transaction (idempotent) */
  def upsert(tx: TransactionRow): Task[Unit]

  /** Batch insert transactions (idempotent) */
  def upsertBatch(txs: List[TransactionRow]): Task[Int]

  /** Get all transactions (for CSV export) */
  def findAll(): Task[List[TransactionRow]]

  /** Get recent transactions with limit */
  def findRecent(limit: Int): Task[List[TransactionRow]]

  /** Get transactions filtered by date range */
  def findByDateRange(
    startDate: LocalDate,
    endDate: LocalDate
  ): Task[List[TransactionRow]]

  /** Get transactions for a specific chain */
  def findByChain(chain: String): Task[List[TransactionRow]]

  /** Count total transactions */
  def count(): Task[Long]

object TransactionRepository:

  final class Live(quill: Quill.Postgres[SnakeCase]) extends TransactionRepository:
    import quill.*

    // Quill schema definition
    inline def transactionsSchema = quote {
      querySchema[TransactionRow]("blockchain_transactions")
    }

    override def upsert(tx: TransactionRow): Task[Unit] =
      run(
        transactionsSchema
          .insertValue(lift(tx))
          .onConflictUpdate(_.txHash)(
            (t, e) => t.amountUsd -> e.amountUsd,
            (t, e) => t.gasUsed -> e.gasUsed,
            (t, e) => t.gasPriceGwei -> e.gasPriceGwei
          )
      ).unit

    override def upsertBatch(txs: List[TransactionRow]): Task[Int] =
      if txs.isEmpty then
        ZIO.succeed(0)
      else
        // Quill doesn't support batch upsert with ON CONFLICT
        // Use individual upserts for idempotency
        ZIO.foreach(txs)(upsert).map(_ => txs.length)

    override def findAll(): Task[List[TransactionRow]] =
      run(transactionsSchema.sortBy(_.date)(using Ord.desc))

    override def findRecent(limit: Int): Task[List[TransactionRow]] =
      run(transactionsSchema.sortBy(_.date)(using Ord.desc).take(lift(limit)))

    override def findByDateRange(
      startDate: LocalDate,
      endDate: LocalDate
    ): Task[List[TransactionRow]] =
      run(
        transactionsSchema
          .filter(tx => tx.date >= lift(startDate) && tx.date <= lift(endDate))
          .sortBy(_.date)(using Ord.asc)
      )

    override def findByChain(chain: String): Task[List[TransactionRow]] =
      run(
        transactionsSchema
          .filter(_.chain == lift(chain))
          .sortBy(_.date)(using Ord.desc)
      )

    override def count(): Task[Long] =
      run(transactionsSchema.size)

  end Live

  /** ZLayer for live implementation */
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, TransactionRepository] =
    ZLayer.fromFunction(Live.apply)

  /** Accessor methods */
  def upsert(tx: TransactionRow): ZIO[TransactionRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[TransactionRepository](_.upsert(tx))

  def upsertBatch(txs: List[TransactionRow]): ZIO[TransactionRepository, Throwable, Int] =
    ZIO.serviceWithZIO[TransactionRepository](_.upsertBatch(txs))

  def findAll(): ZIO[TransactionRepository, Throwable, List[TransactionRow]] =
    ZIO.serviceWithZIO[TransactionRepository](_.findAll())

  def findRecent(limit: Int): ZIO[TransactionRepository, Throwable, List[TransactionRow]] =
    ZIO.serviceWithZIO[TransactionRepository](_.findRecent(limit))

  def findByDateRange(
    startDate: LocalDate,
    endDate: LocalDate
  ): ZIO[TransactionRepository, Throwable, List[TransactionRow]] =
    ZIO.serviceWithZIO[TransactionRepository](_.findByDateRange(startDate, endDate))

  def findByChain(chain: String): ZIO[TransactionRepository, Throwable, List[TransactionRow]] =
    ZIO.serviceWithZIO[TransactionRepository](_.findByChain(chain))

  def count(): ZIO[TransactionRepository, Throwable, Long] =
    ZIO.serviceWithZIO[TransactionRepository](_.count())
