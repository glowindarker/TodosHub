package com.todo.todo.compose

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.todo.todo.R

@Composable
fun CreateTodoScreen(
    navHostController: NavHostController
) {
    CreateTodoContent(navHostController)
}

@Composable
private fun CreateTodoContent(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.create_todo)) })
        },
        content = { it ->

        })
}