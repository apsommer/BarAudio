package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.settings.SettingsDrawer
import com.sommerengineering.baraudio.uitls.backgroundPadding
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit) {

    val messages by viewModel.messages.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val listState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()

    // dark mode
    val isDarkMode = viewModel.isDarkMode
    val backgroundImageId =
        if (isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

    // todo remove
    val quoteState by viewModel.mindfulnessQuoteState.collectAsState()
    val quote = (quoteState as? MindfulnessQuoteState.Success)?.mindfulnessQuote?.quote
    val isShowQuote = viewModel.isShowQuote

    // scroll to new message on arrival todo auto scroll only while user at top
//    LaunchedEffect(messages) {
//        if (messages.isEmpty()) return@LaunchedEffect
//        listState.animateScrollToItem(0)
//    }

    var feedMode by remember { mutableStateOf(FeedMode.Linear) }
    val groups = remember(messages) { groupMessages(messages) }

    // side drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SettingsDrawer(
                    viewModel = viewModel,
                    onSignOut = onSignOut)
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(alpha = 0.5f)) {

        Scaffold(

            // top bar
            topBar = {
                MessagesTopBar(
                    onSettingsClick = {
                        composableScope.launch { drawerState.open() }
                    },
                    onToggleFeedMode = {
                        feedMode = when (feedMode) {
                            FeedMode.Linear -> FeedMode.Grouped
                            FeedMode.Grouped -> FeedMode.Linear
                        }
                    })
            },

            // fab, mute button
            floatingActionButton = {
                MessagesFloatingActionButton(viewModel)
            },

            bottomBar = {
                val areNotificationsEnabled = viewModel.areNotificationsEnabled
                AllowNotificationsBottomBar(areNotificationsEnabled)
            }

        ) { padding ->

            // screen container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) {

                // mindfulness quote
                if (isShowQuote && quote != null) {

                    Text(
                        text = quote,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = backgroundPadding,
                                end = backgroundPadding,
                                top = 64.dp)
                            .align(Alignment.Center))
                }

                // background image
                Image(
                    modifier = Modifier
                        .padding(
                            start = backgroundPadding,
                            end = backgroundPadding,
                            bottom = 64.dp)
                        .align(Alignment.Center),
                    painter = painterResource(backgroundImageId),
                    contentDescription = null)

                // messages list
                LazyColumn(state = listState) { when (feedMode) {

                    FeedMode.Linear -> {

                        items(
                            items = messages,
                            key = { it.timestamp }) { message ->

                            MessageItem(
                                viewModel = viewModel,
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        fadeOutSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        placementSpec = spring(stiffness = Spring.StiffnessVeryLow)),
                                message = message)
                        }
                    }

                    FeedMode.Grouped -> {
                        groups.forEach { (origin, messages) ->
                            val latestMessage = messages.first()
                            item(key = origin) {
                                StreamHeaderItem(
                                    origin = origin,
                                    lastestMessage = latestMessage,
                                    messageCount = messages.size)
                            }
                        }
                    }
                }}
            }
        }
    }
}

private fun groupMessages(messages: List<Message>): Map<String, List<Message>> {
    return messages
        .groupBy { it.origin }
        .mapValues { group ->
            group.value.sortedByDescending { it.timestamp }
        }
}
