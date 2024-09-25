package com.todo.todoshub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.todo.todo.compose.CreateTodoScreen
import com.todo.todo.compose.TodoListingScreen

@Composable
fun TodoNavigation(modifier: Modifier, navController: NavHostController) {
    NavHost(modifier = modifier,navController = navController, startDestination = "todo_list") {
        composable("todo_list") { TodoListingScreen(navController) }
        composable("create_todo") { CreateTodoScreen(navController) }
    }
}
