package com.todo.todo.viewmodel

import com.todo.data.model.TodoEntity
import com.todo.data.repo.TodoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListingViewModelTest {

    private lateinit var viewModel: TodoListingViewModel
    private val todoRepository: TodoRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = TodoListingViewModel(todoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial UiState`() = runTest {
        val state = viewModel.state.first()
        assertTrue(state.isLoading)
        assertTrue(state.searchQuery.isEmpty())
        assertNull(state.todosList)
        assertFalse(state.isError)
    }

    @Test
    fun `test fetchTodos updates UiState with fetched todos`() = runTest {
        val todos = listOf(TodoEntity(todoDesc = "Task 1"), TodoEntity(todoDesc = "Task 2"))
        coEvery { todoRepository.fetchTodos() } returns flowOf(todos)

        viewModel.fetchTodos()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertFalse(state.isLoading)
        assertEquals(todos, state.todosList)
        assertEquals("",state.searchQuery)
    }

    @Test
    fun `test fetchTodos handles exception and updates isLoading`() = runTest {
        coEvery { todoRepository.fetchTodos() } throws Exception("Fetch failed")

        viewModel.fetchTodos()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertFalse(state.isLoading)
        assertNull(state.todosList)
    }


    @Test
    fun `test onSearchQueryChange updates searchQuery in UiState`() = runTest {
        val query = "Task"
        viewModel.onSearchQueryChange(query)
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(query, state.searchQuery)
    }

    @Test
    fun `test searchQuery debounce filters todos`() = runTest {
        val todos = listOf(TodoEntity(todoDesc = "Task 1"), TodoEntity(todoDesc = "Task 2"))
        coEvery { todoRepository.fetchTodos() } returns flowOf(todos)

        viewModel.fetchTodos()
        viewModel.onSearchQueryChange("Task")
        advanceTimeBy(2000)

        val state = viewModel.state.first()
        assertEquals(todos, state.todosList)
        assertFalse(state.isLoading)
    }

    @Test
    fun `test showErrorPopup updates isError to true`() = runTest {
        viewModel.showErrorPopup()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertTrue(state.isError)
    }

    @Test
    fun `test dismissErrorPopup updates isError to false`() = runTest {
        viewModel.dismissErrorPopup()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertFalse(state.isError)
    }
}