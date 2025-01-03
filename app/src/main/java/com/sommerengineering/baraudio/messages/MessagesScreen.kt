package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.getDatabaseReference
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.settings.SettingsScreen
import com.sommerengineering.baraudio.theme.uiModeFadeTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onSignOut: () -> Unit) {

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
                SettingsScreen(
                    onSignOut = onSignOut)
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(
            alpha = 0.5f)) {

        // listen to database
        LaunchedEffect(Unit) {
            listenToDatabase(
                messages,
                listState,
                coroutine)
        }

        Scaffold(

            // top bar
            topBar = {
                MessagesTopBar(
                    messages = messages,
                    onSettingsClick = {
                        coroutine.launch {
                            drawerState.open()
                        }
                    })
            },

            // fab, mute button
            floatingActionButton = {
                MessagesFloatingActionButton(
                    context = context,
                    viewModel = viewModel)
            }

        ) { padding ->

            // screen container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) {

                // background image
                Image(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 64.dp)
                        .align(Alignment.Center),
                    painter = painterResource(viewModel.getBackgroundId()),
                    contentDescription = null,

                    // fade to invisible after 5 messages
                    alpha = animateFloatAsState(
                        targetValue =
                            if (messages.isEmpty()) { 1f }
                            else { (1 - 0.2 * messages.size).toFloat() },
                        animationSpec =
                            tween(uiModeFadeTimeMillis),
                        label = "")
                        .value)

                var isRefreshing by remember { mutableStateOf(false) }
                val pullToRefreshState = rememberPullToRefreshState()

                // pull to refresh
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    onRefresh = {

                        // start spinner
                        isRefreshing = true

                        // remove listener
                        getDatabaseReference(messagesNode)
                            .removeEventListener(dbListener)

                        // clear list
                        messages.clear()

                        // reattach listener
                        listenToDatabase(
                            messages,
                            listState,
                            coroutine)

                        // dismiss indicator
                        coroutine.launch {
                            delay(1000)
                            isRefreshing = false
                        }
                    },
                    indicator = {
                        Indicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter),
                            isRefreshing = isRefreshing,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            state = pullToRefreshState)
                    }) {

                    // message list
                    LazyColumn(
                        state = listState) {
                        items(
                            items = messages.reversed(),
                            key = { it.timestamp }) { message ->

                            // highlight recent messages
                            var isRecent by remember { mutableStateOf(true) }
                            isRecent =
                                1000 * 60 > System.currentTimeMillis() - message.timestamp.toLong()

                            MessageItem(
                                viewModel = viewModel,
                                isRecent = isRecent,
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        fadeOutSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        placementSpec = spring(stiffness = Spring.StiffnessVeryLow)),
                                message = message)
                        }
                    }
                }
            }
        }
    }
}
