package com.joinus.trivagoshowcase

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.collectAsState
import com.google.android.gms.maps.model.LatLng
import com.joinus.trivagoshowcase.features.businesses.BusinessesViewState
import com.joinus.trivagoshowcase.features.details.DetailsViewState
import com.joinus.trivagoshowcase.features.map.MapViewState
import com.joinus.trivagoshowcase.features.search.SearchViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.verification.VerificationMode
import repositories.MainRepository
import services.mappers.Business
import services.mappers.BusinessDetails
import services.network.ApiResponse
import java.lang.Exception

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockedRepository: MainRepository

    private lateinit var viewModel: MainViewModel

    private val businesses = listOf(
        Business(
            id = "",
            name = "",
            imageUrl = "",
            categories = listOf(),
            price = "",
            rating = 0.0f,
            latLng = LatLng(1.2, 1.2)
        )
    )
    private val businessDetails = BusinessDetails(
        id = "",
        name = "",
        imageUrl = "",
        photos = listOf(),
        categories = listOf(),
        price = null,
        rating = 0.0f,
        displayAddress = listOf(),
        displayPhone = ""
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(mockedRepository, testDispatcher)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getBusiness() is called, map isLoading sequence is false, true, false`() {
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Success(listOf())
            )

            val results = mutableListOf<MapViewState>()
            viewModel.mapViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(listOf(false, true, false), results.map { it.isLoading })
        }
    }

    @Test
    fun `when getBusiness() is called, businesses isLoading sequence is false, true, false`() {
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Success(listOf())
            )

            val results = mutableListOf<BusinessesViewState>()
            viewModel.businessesViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(listOf(false, true, false), results.map { it.isLoading })
        }
    }

    @Test
    fun `when getBusiness() is called, Search isLoading sequence is false, true, false`() {
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Success(listOf())
            )

            val results = mutableListOf<SearchViewState>()
            viewModel.searchViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(listOf(false, true, false), results.map { it.isLoading })
        }
    }

    @Test
    fun `when getBusiness() is called, BusinessesViewState receive businesses`() {
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Success(businesses)
            )

            val results = mutableListOf<BusinessesViewState>()
            viewModel.businessesViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(businesses, results.last().businesses)
        }
    }

    @Test
    fun `when getBusiness() is called, MapViewState receive businesses`() {
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Success(businesses)
            )

            val results = mutableListOf<MapViewState>()
            viewModel.mapViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(businesses, results.last().businesses)
        }
    }

    @Test
    fun `when getBusiness() returns Error, mainViewState receive error`() {
        val exception = Exception()
        runTest {
            whenever(mockedRepository.getBusinesses(anyDouble(), anyDouble())).thenReturn(
                ApiResponse.Error(exception)
            )
            val results = mutableListOf<MainViewState>()
            viewModel.mainViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinesses()
            runCurrent()
            assertEquals(listOf(false, true), results.map { it.isError })
        }
    }

    @Test
    fun `when getBusinessDetails() returns Error, mainViewState receive error`() {
        val exception = Exception()
        runTest {
            whenever(mockedRepository.getBusinessDetails(anyString())).thenReturn(
                ApiResponse.Error(exception)
            )
            val results = mutableListOf<MainViewState>()
            viewModel.mainViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinessDetails(businesses.first(), listOf())
            runCurrent()
            assertEquals(true, results.map { it.isError }.last())
        }
    }

    @Test
    fun `when getBusinessDetails() returns BusinessDetails, detailsViewState receive businessDetails`() {
        runTest {
            whenever(mockedRepository.getBusinessDetails(anyString())).thenReturn(
                ApiResponse.Success(businessDetails)
            )
            val results = mutableListOf<DetailsViewState>()
            viewModel.detailsViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.getBusinessDetails(businesses.first(), listOf())
            runCurrent()
            assertEquals(businessDetails, results.last().businessDetails)
        }
    }

    @Test
    fun `when user clicks over map images, businessesFragment receive new position`() {
        runTest {
            val results = mutableListOf<BusinessesViewState>()
            viewModel.businessesViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.onMapOverlayViewClicked(4)
            runCurrent()
            assertEquals(4, results.last().mapViewPosition)
        }
    }

    @Test
    fun `when user scrolls businesses, map receive new snapedViewId`() {
        runTest {
            val results = mutableListOf<MapViewState>()
            viewModel.mapViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.onSnapView("id")
            runCurrent()
            assertEquals("id", results.last().snapedViewId)
        }
    }

    @Test
    fun `when user scrolls over map, refreshButton gets visible`() {
        runTest {
            val results = mutableListOf<SearchViewState>()
            viewModel.searchViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.refreshButton(true)
            runCurrent()
            assertEquals(true, results.last().isRefreshButtonVisible)
        }
    }

    @Test
    fun `when refreshButton() is clicked, mapView gets onRefreshedClicked`() {
        runTest {
            val results = mutableListOf<MapViewState>()
            viewModel.mapViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.onRefreshButtonClicked(true)
            runCurrent()
            assertEquals(true, results.map { it.onRefreshClicked }.last())
        }
    }

    @Test
    fun `when onBackPressed() is clicked, mainViewState gets isGoingBack`() {
        runTest {
            val results = mutableListOf<MainViewState>()
            viewModel.mainViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.onBackPressed()
            runCurrent()
            assertEquals(true, results.map { it.isGoingBack }.last())
        }
    }

    @Test
    fun `when retry() is clicked, MapViewState receives empty businesses`() {
        runTest {
            val results = mutableListOf<MapViewState>()
            viewModel.mapViewState.onEach(results::add).launchIn(
                CoroutineScope(
                    UnconfinedTestDispatcher(testScheduler)
                )
            )
            viewModel.onBackPressed()
            runCurrent()
            assertEquals(true, results.last().businesses.isEmpty())
        }
    }
}

