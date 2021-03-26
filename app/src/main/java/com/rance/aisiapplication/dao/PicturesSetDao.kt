package com.rance.aisiapplication.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.rance.aisiapplication.model.PicturesSet

@Dao
interface PicturesSetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(picturesSet: PicturesSet): Long

    @Query("SELECT * FROM picturesSet")
    fun getAllPicturesSet(): LiveData<MutableList<PicturesSet>>

    @Query("SELECT * FROM picturesSet where url = :url")
    fun findPicturesSetByUrl(url: String): PicturesSet

    @Query("SELECT * FROM picturesSet where url = :url")
    fun findPicturesSetLiveDataByUrl(url: String): LiveData<PicturesSet>

    @Update
    fun update(picturesSet: PicturesSet)
}