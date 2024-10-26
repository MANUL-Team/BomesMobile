//package com.MANUL.Bomes.presentation.createChat
//
//import CreatingChatViewModel
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.ui.unit.dp
//import com.MANUL.Bomes.ui.theme.PurpleGrey80
//
//@Composable
//fun AccountScreen(viewModel: CreatingChatViewModel) {
//    Column(
//        modifier = Modifier.fillMaxSize()
//            .background(PurpleGrey80),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        var login: String by rememberSaveable { mutableStateOf("") }
//        TextField(
//            value = login,
//            onValueChange = {
//                login = it
//            },
//            label = { Text("Login") }
//        )
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        var password: String by rememberSaveable { mutableStateOf("") }
//        TextField(
//            value = password,
//            onValueChange = {
//                password = it
//            },
//            label = { Text("Password") }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = {
//            //viewModel.login(login, password)
//        }) {
//            Text("Enter ")
//        }
//        //
//    }
//}
//
//@Preview
//@Composable
//fun showView() {
//    AccountScreen(CreatingChatViewModel())
//}
