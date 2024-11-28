package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.login.buttonBorderSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun MessagesScreen(
    onSettingsClick: () -> Unit) {

    // init
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    // listen to database writes
    LaunchedEffect(databaseUrl) {

        // todo dev: launch to settings
//        coroutine.launch {
//            delay(100)
//            onSettingsClick.invoke()
//        }

        // mute button, can't wait for tts init (as with other tts params) since icon needed for ui
        viewModel.initMute(context)

        listenToDatabaseWrites(
            messages = messages,
            viewModel = viewModel,
            listState = listState,
            coroutine = coroutine)
    }

    Scaffold(

        topBar = {
            MessagesTopBar(
                onSettingsClick = onSettingsClick,
                messages = messages)
        },

        floatingActionButton = {
            FloatingActionButton (
                modifier = Modifier
                    .size(buttonBorderSize)
                    .border(
                        border = BorderStroke(
                            width = 1.dp, // if (viewModel.isMute) 2.dp else 1.dp,
                            color = viewModel.getFabIconColor()),
                        shape = CircleShape),
                containerColor = viewModel.getFabBackgroundColor(),
                shape = CircleShape,
                onClick = { viewModel.toggleMute(context) }) {

                Icon(
                    modifier = Modifier.size(buttonBorderSize / 2),
                    painter = painterResource(viewModel.getFabIconId()),
                    tint = viewModel.getFabIconColor(),
                    contentDescription = null)
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)) {
                Image(
                    painter = painterResource(R.drawable.background),
                    contentDescription = null)
            }
            LazyColumn(
                state = listState) {
                items(
                    items = messages,
                    key = { it.timestamp }) { message ->
                    SwipeToDismissBox(
                        state = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                swipeToDelete(
                                    messages = messages,
                                    message = message,
                                    position = it)
                            }),
                        modifier = Modifier.animateItem(),
                        backgroundContent = { }) {
                        MessageItem(
                            message = message)
                    }
                }
            }
        }
    }
}
