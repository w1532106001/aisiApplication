package com.rance.aisiapplication.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rance.aisiapplication.model.PicturesSet

@Dao
interface PicturesSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picturesSet: PicturesSet): Long

    @Query("SELECT * FROM picturesSet")
    fun getAllPicturesSet(): LiveData<MutableList<PicturesSet>>

    @Query("SELECT * FROM picturesSet where url = :url")
    fun findPicturesSetByUrl(url:String):PicturesSet
}