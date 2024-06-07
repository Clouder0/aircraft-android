package tech.caaa.aircraft

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class ScoreRecord(val id: Int, val name: String, val score: Int, val time: Long, val difficulty: Int)
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "all_scores"
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_SCORE = "score"
        private const val COLUMN_TIME = "playtime"
        private const val COLUMN_DIFFICULTY = "difficulty"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_SCORE INTEGER,"
                + "$COLUMN_TIME INTEGER,"
                + "$COLUMN_DIFFICULTY INTEGER"
                + ")")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addRecord(name: String, score: Int, time: Long, difficulty: Int): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_SCORE, score)
        contentValues.put(COLUMN_TIME, time)
        contentValues.put(COLUMN_DIFFICULTY, difficulty)
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun getAllDataModels(difficulty: Int): List<ScoreRecord> {
        val dataList: MutableList<ScoreRecord> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_NAME "
        val db = this.readableDatabase


        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
                    val time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                    val dif = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY))
                    val dataModel = ScoreRecord(id, name, score, time, dif)
                    if(dif == difficulty)
                        dataList.add(dataModel)
                } while (cursor.moveToNext())
            }
        }

        return dataList.sortedByDescending { x -> x.score }
    }

    fun deleteRecord(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }
}