package info.codementor.meusgastos.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import info.codementor.meusgastos.entity.FinancialRecord
import info.codementor.meusgastos.entity.FinancialRecordType
import java.time.Instant

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val TABLE_NAME = "financial_records"
        private const val DATABASE_VERSION = 1

        private const val COD = 0
        private const val AMOUNT = 1
        private const val DESCRIPTION = 2
        private const val DATE = 3
        private const val TYPE = 4
        private const val CREATED_AT = 5
        private const val UPDATED_AT = 6
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "amount REAL, " +
                    "description TEXT," +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insert(entity: FinancialRecord) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("date", entity.date.toString())
        values.put("description", entity.description)
        values.put("amount", entity.amount)
        values.put("type", entity.type.toString())
        values.put("createdAt", entity.createdAt.toString())
        values.put("updatedAt", entity.createdAt.toString())
        db.insert(TABLE_NAME, null, values)
    }

    fun update(entity: FinancialRecord) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("date", entity.date.toString())
        values.put("description", entity.description)
        values.put("amount", entity.amount)
        values.put("type", entity.type.toString())
        values.put("updatedAt", entity.createdAt.toString())
        db.update(TABLE_NAME, values, "_id = ${entity._id}", null)
    }

    fun find(id: Int): FinancialRecord? {
        val db = this.writableDatabase
        val cursor = db.query(
            TABLE_NAME, null,
            "_id=${id}",
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToNext()) {

            val id = id
            val amount = cursor.getString(AMOUNT).toDoubleOrNull() ?: 0.0
            val description = cursor.getString(DESCRIPTION)
            val date = Instant.parse(cursor.getString(DATE))
            val type = FinancialRecordType.valueOf(cursor.getString(TYPE))
            val createdAt = Instant.parse(cursor.getString(CREATED_AT))
            val updatedAt = Instant.parse(cursor.getString(UPDATED_AT))

            return FinancialRecord.load(id, date, description, amount, type, createdAt, updatedAt)

        }
        return null
    }

    fun delete(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "_id = $id", null)
    }

    fun findAll(): MutableList<FinancialRecord> {
        val db = this.writableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val list = mutableListOf<FinancialRecord>()
        while (cursor.moveToNext()) {

            val id = cursor.getInt(COD)
            val amount = cursor.getString(AMOUNT).toDoubleOrNull() ?: 0.0
            val description = cursor.getString(DESCRIPTION)
            val date = Instant.parse(cursor.getString(DATE))
            val type = FinancialRecordType.valueOf(cursor.getString(TYPE))
            val createdAt = Instant.parse(cursor.getString(CREATED_AT))
            val updatedAt = Instant.parse(cursor.getString(UPDATED_AT))
            list.add(
                FinancialRecord.load(
                    id,
                    date,
                    description,
                    amount,
                    type,
                    createdAt,
                    updatedAt
                )
            )
        }
        return list
    }
}

