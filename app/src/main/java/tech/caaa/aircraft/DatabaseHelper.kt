package tech.caaa.aircraft

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class ScoreRecord(val id: Int, val name: String, val score: Int, val time: Long)
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "all_scores"
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_SCORE = "score"
        private const val COLUMN_TIME = "playtime"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_SCORE INTEGER,"
                + "$COLUMN_TIME INTEGER"
                + ")")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addRecord(name: String, score: Int, time: Long): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_SCORE, score)
        contentValues.put(COLUMN_TIME, time)
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun getAllDataModels(): List<ScoreRecord> {
        val dataList: MutableList<ScoreRecord> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase

        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
                    val time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                    val dataModel = ScoreRecord(id, name, score, time)
                    dataList.add(dataModel)
                } while (cursor.moveToNext())
            }
        }

        return dataList.sortedBy { s -> -s.score }
    }

    fun deleteRecord(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }
}