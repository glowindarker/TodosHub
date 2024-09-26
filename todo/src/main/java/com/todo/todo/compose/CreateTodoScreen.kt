package com.todo.todo.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.todo.todo.R
import com.todo.todo.utils.LoadingIndicator
import com.todo.todo.utils.RESULT
import com.todo.todo.viewmodel.CreateTodoViewModel

@Composable
fun CreateTodoScreen(
    navController: NavHostController,
    viewModel: CreateTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    CreateTodoContent(
        state = { state },
        onTodoValueChange = {
            viewModel.onTodoValueChange(it)
        },
        onAddBtnClick = {
            viewModel.addTodo(
                onSuccess = { navController.popBackStack() },
                onFailure = {
                    //navigate to listing screen
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(RESULT, true)
                    navController.popBackStack()
                })
        }
    )
}

@Composable
private fun CreateTodoContent(
    state: () -> CreateTodoViewModel.UiState,
    onTodoValueChange: (String) -> Unit,
    onAddBtnClick: () -> Unit
) {
    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.create_todo)) })
        },
        content = { it ->
            if (state().isLoading) {
                LoadingIndicator()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(it),
                    value = state().enteredTodo,
                    onValueChange = { onTodoValueChange(it) },
                    placeholder = { Text(text = stringResource(R.string.enter_your_todo)) },
                    singleLine = true,
                    isError = state().isError,
                )
                if (state().isError) {
                    Text(
                        text = stringResource(R.string.please_enter_a_valid_todo),
                        color = Color.Red,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                    )
                }
                Button(
                    onClick = {
                        onAddBtnClick()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                ) {
                    Text(text = stringResource(id = R.string.add_todo))
                }
            }
        }
    )
}

@Preview
@Composable
private fun CreateTodoContentPreview(){
    CreateTodoContent(state = {
        CreateTodoViewModel.UiState(
            isError = false,
            isLoading = false
        )
    }, onTodoValueChange = {}, onAddBtnClick = {})
}