package util.exposed

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException

suspend fun <T> newSuspendedTransaction(repetitions: Int = 5, statement: org.jetbrains.exposed.sql.Transaction.() -> T): T {
    var lastException: Exception? = null
    for (i in 1..repetitions) {
        try {
            return org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction(Dispatchers.IO) {
                statement.invoke(this)
            }
        } catch (e: ExposedSQLException) {
            lastException = e
        }
    }
    throw lastException ?: RuntimeException("This should never happen")
}