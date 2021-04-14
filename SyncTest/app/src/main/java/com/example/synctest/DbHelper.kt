package com.example.synctest

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper : SQLiteOpenHelper {

    private var DATABASE_VERSION: Int = 1
    var CREATE_TABLE: String =
        "create table " + DbContract().TABLE_NAME + " (id integer primary key autoincrement, " + DbContract().NAME + " text, " + DbContract().SYNC_STATUS + " integer);"
    var DROP_TABLE = "drop table if exists " + DbContract().TABLE_NAME

    constructor(mContext: Context) : super(
        mContext,
        DbContract().DATABASE_NAME,
        null,
        1
    ) {

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }

    public fun saveToLocalDatabase(name: String, sync_status: Int, database: SQLiteDatabase) {
        var contentValues = ContentValues()
        contentValues.put(DbContract().NAME, name)
        contentValues.put(DbContract().SYNC_STATUS, sync_status)
        database.insert(DbContract().TABLE_NAME, null, contentValues)
    }

    public fun readFromLocalDatabase(database: SQLiteDatabase): Cursor {
        val projection = arrayOf<String>(DbContract().NAME, DbContract().SYNC_STATUS)

        return (database.query(DbContract().TABLE_NAME, projection, null, null, null, null, null))
    }

    public fun updateLocalDatabase(name: String, sync_status: Int, database: SQLiteDatabase) {
        var contentValues = ContentValues()
        contentValues.put(DbContract().SYNC_STATUS, sync_status)
        var selection: String = DbContract().NAME + " LIKE ?"
        val selection_args = arrayOf<String>(name)
        database.update(DbContract().TABLE_NAME, contentValues, selection, selection_args)

    }
}