package com.dicoding.dicodingstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.dicodingstory.data.adapter.ListStoryAdapter
import com.dicoding.dicodingstory.data.repository.StoryRepository
import com.dicoding.dicodingstory.data.repository.UserRepository
import com.dicoding.dicodingstory.data.response.ItemStoryResponse
import com.dicoding.dicodingstory.main.MainViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MVMTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var quoteRepository: StoryRepository

    @Mock
    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Ganti dispatcher utama di sini
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest(testDispatcher) {
        val dummyStory = DataDummy.generateDummyQuoteResponse() // Ambil data dummy dari kelas DataDummy
        val data: PagingData<ItemStoryResponse> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ItemStoryResponse>>()
        expectedStory.value = data
        Mockito.`when`(quoteRepository.getQuote()).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(quoteRepository, repository)
        val actualStory: PagingData<ItemStoryResponse> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = testDispatcher,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest(testDispatcher) {
        val data: PagingData<ItemStoryResponse> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ItemStoryResponse>>()
        expectedStory.value = data
        Mockito.`when`(quoteRepository.getQuote()).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(quoteRepository, repository)
        val actualStory: PagingData<ItemStoryResponse> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = testDispatcher,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher utama setelah pengujian selesai
    }
}

class StoryPagingSource : PagingSource<Int, ItemStoryResponse>() {
    companion object {
        fun snapshot(items: List<ItemStoryResponse>): PagingData<ItemStoryResponse> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemStoryResponse>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemStoryResponse> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}