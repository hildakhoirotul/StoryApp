package com.example.storyapp.data

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.api.ApiService
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.model.*
import com.example.storyapp.utils.Constanta
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class QuoteRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    private val token = Constanta.token

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb, mockApi, token
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(), null, PagingConfig(10), 10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override fun requestLogin(email: String, password: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun requestRegister(
        name: String,
        email: String,
        password: String,
    ): Call<RegisterResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(token: String, page: Int, size: Int): StoriesResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl $i", "createAt $i", "name $i", "desc $i", i.toString(), "lon $i", "lat $i"
            )
            items.add(story)
        }

        return StoriesResponse(
            items.subList((page - 1) * size, (page - 1) * size + size), false or true, ""
        )
    }

    override fun getStoryDetail(token: String, id: String): Call<DetailResponse> {
        TODO("Not yet implemented")
    }

    override fun getStoryLocationDetail(token: String, id: String): Call<DetailResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
    ): Call<AddStoryResponse> {
        TODO("Not yet implemented")
    }

    override fun getStoryListLocation(token: String, size: Int): Call<StoriesResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadStoryWithLocation(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody,
    ): Call<AddStoryResponse> {
        TODO("Not yet implemented")
    }

}