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

    @Query("SELECT * FROM picturesSet")
    fun getAllPicturesSet2(): MutableList<PicturesSet>

    @Query("SELECT * FROM picturesSet where url = :url")
    fun findPicturesSetByUrl(url: String): PicturesSet

    @Query("SELECT * FROM picturesSet where url = :url")
    fun findPicturesSetLiveDataByUrl(url: String): LiveData<PicturesSet>

    @Update
    fun update(picturesSet: PicturesSet)

    @Query("DELETE FROM picturesset where url = :url")
    fun deleteByUrl(url:String)
//
//    @Query("UPDATE picturesSet set lastWatchPosition = :position where url = :url")
//    fun updateLastWatchPosition(position: Int,url: String)
}