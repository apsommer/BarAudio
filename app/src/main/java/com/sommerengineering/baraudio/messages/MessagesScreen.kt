package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.login.buttonBorderSize
import com.sommerengineering.baraudio.settings.SettingsScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MessagesScreen(
    onSignOut: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)
    val messages = remember { mutableStateListOf<Message>() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SettingsScreen(onSignOut = onSignOut)
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(
            alpha = 0.5f)) {

        // listen to database
        LaunchedEffect(Unit) {
            listenToDatabase(
                messages,
                viewModel,
                listState,
                coroutine)
        }

        Scaffold(

            // top bar
            topBar = {
                MessagesTopBar(
                    onSettingsClick = {
                        coroutine.launch {
                            drawerState.open()
                        }
                    },
                    messages = messages)
            },

            // mute button
            floatingActionButton = {
                FloatingActionButton (
                    modifier = Modifier
                        .size(buttonBorderSize)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = viewModel.getFabIconColor()
                            ),
                            shape = CircleShape
                        ),
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

                // background image
                Image(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.logo_bars),
                    alpha = 0.5f,
                    contentDescription = null)

                // message list
                LazyColumn(
                    state = listState) {
                    items(
                        items = messages,
                        key = { it.timestamp }) { message ->

                        // highlight recent messages
                        var isRecent by remember { mutableStateOf(true) }
                        isRecent  = 1000 * 60 > System.currentTimeMillis() - message.timestamp.toLong()

                        MessageItem(
                            viewModel = viewModel,
                            isRecent = isRecent,
                            modifier = Modifier
                                .animateItem(),
                            message = message)
                    }
                }
            }
        }
    }
}
