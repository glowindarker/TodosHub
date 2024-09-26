package com.todo.todo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.todo.data.model.TodoEntity
import com.todo.data.repo.TodoRepository
import com.todo.todo.utils.ERROR
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTodoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CreateTodoViewModel
    private val todoRepository: TodoRepository = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateTodoViewModel(todoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() = runTest {
        val initialState = viewModel.state.value
        assertFalse(initialState.isLoading)
        assertTrue(initialState.enteredTodo.isEmpty())
        assertFalse(initialState.isError)
    }


    @Test
    fun `test onTodoValueChange updates state`() = runTest {
        val input = "Task 1"
        viewModel.onTodoValueChange(input)

        val result = viewModel.state.first().enteredTodo
        assertEquals(input, result)
    }

    @Test
    fun `test addTodo with empty input sets isError to true`() = runTest {
        viewModel.onTodoValueChange("")
        viewModel.addTodo({}, {})
        advanceUntilIdle()
        val result = viewModel.state.first().isError
        assertTrue(result)
    }

    @Test
    fun `test addTodo with error input throws exception`() = runTest {
        viewModel.onTodoValueChange(ERROR)

        var failureInvoked = false
        viewModel.addTodo(onSuccess = {}, onFailure = {
            failureInvoked = true
        })
        advanceUntilIdle()
        assertTrue(failureInvoked)
    }

    @Test
    fun `test addTodo with valid input inserts todo and invokes onSuccess`() = runTest {
        val input = "Task 2"
        viewModel.onTodoValueChange(input)

        var successInvoked = false
        viewModel.addTodo(onSuccess = {
            successInvoked = true
        }, onFailure = {})
        advanceUntilIdle()
        val state = viewModel.state.first()
        assertFalse(state.isError)
        assertFalse(state.isLoading)
        coVerify { todoRepository.insertTodo(TodoEntity(todoDesc = input)) }
        assertTrue(successInvoked)
    }

    @Test
    fun `test addTodo handles exception and invokes onFailure`() = runTest {
        viewModel.onTodoValueChange("valid todo")
        coEvery { todoRepository.insertTodo(any()) } throws Exception("Insertion failed")

        var failureInvoked = false
        viewModel.addTodo(onSuccess = {}, onFailure = {
            failureInvoked = true
        })
        advanceUntilIdle()
        assertTrue(failureInvoked)
    }
}

