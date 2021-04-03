package com.rance.aisiapplication.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.rance.aisiapplication.model.Model
import com.rance.aisiapplication.model.PicturesSet

@Dao
interface ModelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(model: Model): Long

    @Query("SELECT * FROM model")
    fun getAllModel(): LiveData<MutableList<Model>>

    @Update
    fun update(model: Model)

    @Query("SELECT * FROM model where url = :url")
    fun findModelLiveDataByUrl(url: String): LiveData<Model>
}