package com.rance.aisiapplication.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rance.aisiapplication.dao.PicturesSetDao
import com.rance.aisiapplication.model.PicturesSet

@Database(entities = [PicturesSet::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPicturesSetDao(): PicturesSetDao

}