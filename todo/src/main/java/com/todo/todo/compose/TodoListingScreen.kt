package com.todo.todo.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.todo.todo.R
import com.todo.todo.utils.FAILED_TO_ADD_TODO
import com.todo.todo.viewmodel.TodoListingViewModel


@Composable
fun TodoListingScreen(
    navController: NavHostController,
    viewModel: TodoListingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()//need to check with fc code

    LaunchedEffect(Unit){
        viewModel.fetchTodos()
    }

    TodoContent(
        state = { state },
        navController = navController,
        onDismiss = {
            viewModel.dismissErrorPopup()
        },
        onSearchQueryChange = {viewModel.onSearchQueryChange(it)}
    )
}


@Composable
private fun TodoContent(
    state: () -> TodoListingViewModel.UiState,
    navController: NavHostController,
    onDismiss: () -> Unit,
    onSearchQueryChange:(String)->Unit
) {
    Scaffold(
        modifier = Modifier.background(color = Color.White),
        backgroundColor = Color.White,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.todo_list)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("create_todo")
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_todo),
                )
            }
        },
        content = { it ->
            println(it)
            if (state().todosList.isNullOrEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    text = stringResource(id = R.string.press_the_button_to_add_a_todo_item),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = state().query, onValueChange = {onSearchQueryChange(it)},
                        placeholder = {
                            Text(
                                text = "Search your todos",
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.add_todo),
                            )
                        }
                    )

                    LazyColumn {
                        state().todosList?.let {
                            items(items = it) {item->
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp),
                                    text = item.todoDesc,
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                    if(state().isError){
                        ErrorPopup(onDismiss = onDismiss)
                    }
                }
            }
        })
}

@Composable
private fun ErrorPopup(
    onDismiss:()->Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Error") },
        text = { Text(text = FAILED_TO_ADD_TODO) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "OK")
            }
        }
    )
}

@Preview
@Composable
private fun TodosContentPreview() {
    TodoContent(navController = NavHostController(LocalContext.current),
        state = { TodoListingViewModel.UiState() },
        onDismiss = {},
        onSearchQueryChange = {}
    )
}