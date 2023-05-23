package informixcdc.main

import com.beust.klaxon.Klaxon
import com.gbasedbt.jdbcx.IfxDataSource
import informixcdc.InformixConnection
import informixcdc.Records
import informixcdc.RecordsMessage
import informixcdc.RecordsRequest
import informixcdc.TableDescription
import informixcdc.asInformix
import informixcdc.main.config.heartbeatInterval

import informixcdc.setUpConverters
import java.lang.Thread.interrupted
import java.lang.Thread.sleep
import java.time.Duration
import java.time.Instant
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

val klaxon = Klaxon().apply {
    RecordsRequest.setUpConverters(this)
    RecordsMessage.setUpConverters(this)
}

object config {
    val heartbeatInterval =
        System.getenv()["INFORMIXCDC_HEARTBEAT_INTERVAL"]?.let { Duration.parse(it) } ?: Duration.ofSeconds(5)
//    val request =
//        System.getenv("INFORMIXCDC_REQUEST").let { klaxon.parse<RecordsRequest>(it) }!!
//dbc:gbasedbt-sqli://82.157.25.145:9088/sysmaster:DB_LOCALE=zh_CN.57372;CLIENT_LOCALE=zh_CN.57372;
    object informix {
        val host = "****"
        val port = 1111
        val serverName = "gbaseserver"
        val user = "****"
        val password = "*****"
    }

//    val log = System.getenv("HENCELOG_FROM").let {
//        val parts = klaxon.parseArray<Any>(it)!!
//        Triple(parts[0] as String, parts[1] as String, parts[2] as Integer)
//    }
    val log = Triple("a", "b", 1)
}

fun main() {
//    Thread.setDefaultUncaughtExceptionHandler { _, e ->
//        log("exception" to e.toString() + "\n" + e.stackTraceString())
//        System.exit(0) // TODO: Do proper cancelling
//    }
//    tables = request.tables.map {
//        TableDescription(
//            name = it.name,
//            database = it.database,
//            owner = it.owner,
//            columns = it.columns
//        )
//    },
    Records(
        getConn = ::getConn,
        server = config.informix.serverName,
        fromSeq = 47311429656,
        tables = listOf(TableDescription("aaa","dp_test","dp_test"),TableDescription("cccc","dp_test","dp_test")),
        logFunc = ::log
    ).use { records ->
        for (message in records.iterator().withHeartbeats(heartbeatInterval)) {
            println(klaxon.toJsonString(message))
        }
    }
}

internal sealed class TimedIteratorItem<out T>
internal data class Item<out T>(val item: T) : TimedIteratorItem<T>()
internal class Timeout<T> : TimedIteratorItem<T>()

internal fun <T, I : Iterator<T>> I.timed(timeout: Duration): Iterator<TimedIteratorItem<T>> = let { wrapped ->
    iterator {
        val done = AtomicBoolean(false)
        val items = LinkedTransferQueue<TimedIteratorItem<T>>()

        val timer = thread(start = true) {
            while (!done.get()) {
                try {
                    sleep(timeout.toMillis())
                    items.put(Timeout())
                } catch (e: InterruptedException) {
                    interrupted()
                    continue
                }
            }
        }

        thread(start = true) {
            try {
                for (item in wrapped) {
                    timer.interrupt()
                    items.transfer(Item(item))
                }
            } finally {
                items.transfer(null)
            }
        }

        while (true) {
            val item = items.take()
            if (item != null) {
                yield(item)
            } else {
                break
            }
        }

        done.set(true)
    }
}

internal fun Iterator<RecordsMessage.Record.Record>.withHeartbeats(interval: Duration): Iterator<RecordsMessage> =
    iterator {
        timed(interval).forEach { timedItem ->
            when (timedItem) {
                is Item -> yield(RecordsMessage.Record(timedItem.item))
                is Timeout -> yield(RecordsMessage.Heartbeat())
            }
        }
    }

internal fun getConn(database: String): InformixConnection =
    with(
        IfxDataSource().apply {
            ifxIFXHOST = config.informix.host
            portNumber = config.informix.port
            serverName = config.informix.serverName
            user = config.informix.user
            password = config.informix.password
            databaseName = database
            ifxJDBCTEMP
        }
    ) {
        log(
            "informix_connection" to "connecting",
            "informix_address" to "${config.informix.host}:${config.informix.port}",
            "informix_database" to "$database@${config.informix.serverName}"
        )
        val t = Instant.now()
        val conn = getConnection()
        log(
            "informix_connection" to "connected",
            "elapsed" to Duration.between(t, Instant.now())
        )

        object : InformixConnection by conn.asInformix() {
            override fun close() = try {
                log("informix_connection" to "stopping")
                conn.close()
            } finally {
                log("informix_connection" to "stopped")
            }
        }
    }

val atomicSerial = AtomicLong(config.log.third.toLong())

fun log(vararg kvs: Pair<String, Any?>) {
    val (group, thread, _) = config.log
    val kvList = ArrayList<Any>(kvs.map { (k, v) -> listOf(k, v) })
    kvList.add(listOf("t", Instant.now()))
    System.err.println(
        Klaxon().toJsonString(
            listOf(
                listOf(
                    listOf(
                        listOf(),
                        listOf(
                            group,
                            thread,
                            atomicSerial.addAndGet(1)
                        )
                    )
                ),
                kvList
            )
        )
    )
}
