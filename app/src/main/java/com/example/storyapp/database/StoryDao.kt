package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.model.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(listStoryItem: List<ListStoryItem>)

    @Query("SELECT * FROM listStoryItem order by createdAt DESC")
    fun getListStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM listStoryItem")
    suspend fun deleteAll()
}