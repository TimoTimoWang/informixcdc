// Autogenerated by jsonschema2klaxon.py. DO NOT EDIT.

package informixcdc

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.JsonObject
import com.beust.klaxon.KlaxonException

private val converters = ArrayList<(Klaxon) -> Converter>()

sealed class Record {

    abstract val seq: Long
    
    abstract val transactionID: Long
    class BeginTx(
        @Json(name = "seq")
        override val seq: Long,
        
        @Json(name = "transaction_id")
        override val transactionID: Long,
        
        @Json(name = "start_at")
        val startAt: String,
        
        @Json(name = "user_id")
        val userID: Long
    ) : Record() {
    
        companion object
    }
    
    class CommitTx(
        @Json(name = "seq")
        override val seq: Long,
        
        @Json(name = "transaction_id")
        override val transactionID: Long,
        
        @Json(name = "start_at")
        val startAt: String
    ) : Record() {
    
        companion object
    }
    
    class RollbackTx(
        @Json(name = "seq")
        override val seq: Long,
        
        @Json(name = "transaction_id")
        override val transactionID: Long
    ) : Record() {
    
        companion object
    }
    
    sealed class RowImage : Record() {
    
        abstract val table: String
        
        abstract val database: String
        
        abstract val owner: String?
        
        abstract val values: Map<String, ColumnValue>
        enum class Type {
            @Json(name = "insert")
            INSERT,
        
            @Json(name = "delete")
            DELETE,
        
            @Json(name = "before_update")
            BEFORE_UPDATE,
        
            @Json(name = "after_update")
            AFTER_UPDATE
        }
        
        class ColumnValue(
            @Json(name = "raw")
            val raw: String,
            
            @Json(name = "decoded")
            val decoded: Any?
        ) {
        
            companion object
        }
        
        class AfterUpdate(
            @Json(name = "seq")
            override val seq: Long,
            
            @Json(name = "transaction_id")
            override val transactionID: Long,
            
            @Json(name = "table")
            override val table: String,
            
            @Json(name = "database")
            override val database: String,
            
            @Json(name = "owner")
            override val owner: String?,
            
            @Json(name = "values")
            override val values: Map<String, ColumnValue>
        ) : RowImage() {
        
            companion object
        }
        
        class BeforeUpdate(
            @Json(name = "seq")
            override val seq: Long,
            
            @Json(name = "transaction_id")
            override val transactionID: Long,
            
            @Json(name = "table")
            override val table: String,
            
            @Json(name = "database")
            override val database: String,
            
            @Json(name = "owner")
            override val owner: String?,
            
            @Json(name = "values")
            override val values: Map<String, ColumnValue>
        ) : RowImage() {
        
            companion object
        }
        
        class Delete(
            @Json(name = "seq")
            override val seq: Long,
            
            @Json(name = "transaction_id")
            override val transactionID: Long,
            
            @Json(name = "table")
            override val table: String,
            
            @Json(name = "database")
            override val database: String,
            
            @Json(name = "owner")
            override val owner: String?,
            
            @Json(name = "values")
            override val values: Map<String, ColumnValue>
        ) : RowImage() {
        
            companion object
        }
        
        class Insert(
            @Json(name = "seq")
            override val seq: Long,
            
            @Json(name = "transaction_id")
            override val transactionID: Long,
            
            @Json(name = "table")
            override val table: String,
            
            @Json(name = "database")
            override val database: String,
            
            @Json(name = "owner")
            override val owner: String?,
            
            @Json(name = "values")
            override val values: Map<String, ColumnValue>
        ) : RowImage() {
        
            companion object
        }
        
        companion object
    }
    
    class Discard(
        @Json(name = "seq")
        override val seq: Long,
        
        @Json(name = "transaction_id")
        override val transactionID: Long
    ) : Record() {
    
        companion object
    }
    
    class Truncate(
        @Json(name = "seq")
        override val seq: Long,
        
        @Json(name = "transaction_id")
        override val transactionID: Long,
        
        @Json(name = "table")
        val table: String,
        
        @Json(name = "database")
        val database: String,
        
        @Json(name = "owner")
        val owner: String?
    ) : Record() {
    
        companion object
    }
    
    companion object {
        init {
            converters.add(this::converter)
        }

        fun converter(klaxon: Klaxon) = object : Converter {
             var enabled = true

            override fun canConvert(cls: Class<*>): Boolean =
                enabled && Record::class.java.isAssignableFrom(cls)

            override fun fromJson(jv: JsonValue): Any? = jv.obj!!["type"].let { v ->
                try { enabled = false; when {
                    v == "begin_tx" ->
                        klaxon.fromJsonObject(jv.obj!!, BeginTx::class.java, BeginTx::class)
                    v == "commit_tx" ->
                        klaxon.fromJsonObject(jv.obj!!, CommitTx::class.java, CommitTx::class)
                    v == "rollback_tx" ->
                        klaxon.fromJsonObject(jv.obj!!, RollbackTx::class.java, RollbackTx::class)
                    v == "after_update" ->
                        klaxon.fromJsonObject(jv.obj!!, RowImage.AfterUpdate::class.java, RowImage.AfterUpdate::class)
                    v == "before_update" ->
                        klaxon.fromJsonObject(jv.obj!!, RowImage.BeforeUpdate::class.java, RowImage.BeforeUpdate::class)
                    v == "delete" ->
                        klaxon.fromJsonObject(jv.obj!!, RowImage.Delete::class.java, RowImage.Delete::class)
                    v == "insert" ->
                        klaxon.fromJsonObject(jv.obj!!, RowImage.Insert::class.java, RowImage.Insert::class)
                    v == "discard" ->
                        klaxon.fromJsonObject(jv.obj!!, Discard::class.java, Discard::class)
                    v == "truncate" ->
                        klaxon.fromJsonObject(jv.obj!!, Truncate::class.java, Truncate::class)
                    else ->
                        throw KlaxonException("value with unexpected value in field \"type\": $jv")
                } } finally { enabled = true }
            }

            override fun toJson(value: Any): String = try { enabled = false; when (value as Record) {
                is BeginTx ->
                        klaxon.toJsonString(value as BeginTx).dropLast(1) + ",\"type\":\"begin_tx\"}"
                is CommitTx ->
                        klaxon.toJsonString(value as CommitTx).dropLast(1) + ",\"type\":\"commit_tx\"}"
                is RollbackTx ->
                        klaxon.toJsonString(value as RollbackTx).dropLast(1) + ",\"type\":\"rollback_tx\"}"
                is RowImage.AfterUpdate ->
                        klaxon.toJsonString(value as RowImage.AfterUpdate).dropLast(1) + ",\"type\":\"after_update\"}"
                is RowImage.BeforeUpdate ->
                        klaxon.toJsonString(value as RowImage.BeforeUpdate).dropLast(1) + ",\"type\":\"before_update\"}"
                is RowImage.Delete ->
                        klaxon.toJsonString(value as RowImage.Delete).dropLast(1) + ",\"type\":\"delete\"}"
                is RowImage.Insert ->
                        klaxon.toJsonString(value as RowImage.Insert).dropLast(1) + ",\"type\":\"insert\"}"
                is Discard ->
                        klaxon.toJsonString(value as Discard).dropLast(1) + ",\"type\":\"discard\"}"
                is Truncate ->
                        klaxon.toJsonString(value as Truncate).dropLast(1) + ",\"type\":\"truncate\"}"
           } } finally { enabled = true }
        }
    }

}

fun Record.Companion.setUpConverters(klaxon: Klaxon) {
    converters.forEach { klaxon.converter(it(klaxon)) }
}
