package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.settings.SettingsDrawer
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.uitls.fabPadding
import com.sommerengineering.baraudio.uitls.fabSize
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit) {

    // lazy column of messages
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // setting drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val composableScope = rememberCoroutineScope()

    // feed mode: linear, or grouped
    val feedMode = viewModel.feedMode
    val groups = remember(messages) { groupMessages(messages) }
    val expandedGroups = remember { mutableStateMapOf<MessageOrigin, Boolean>() }

    // toggle background image with dark mode
    val backgroundImageId =
        if (viewModel.isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { ModalDrawerSheet { SettingsDrawer(viewModel, onSignOut) } },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(alpha = 0.5f)) {

        Scaffold(
            topBar = { MessagesTopBar(
                feedMode = feedMode,
                onSettingsClick = { composableScope.launch { drawerState.open() } },
                onToggleFeedMode = { viewModel.toggleFeedMode() })},
            floatingActionButton = { MessagesFloatingActionButton(viewModel) },
            bottomBar = { AllowNotificationsBottomBar(viewModel.areNotificationsEnabled) }) { padding ->

            Box(Modifier.fillMaxSize().padding(padding)) {

                // background image
                Image(
                    painter = painterResource(backgroundImageId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 2 * fabPadding))

                // messages list
                LazyColumn(state = listState) { when (feedMode) {

                    FeedMode.Linear -> {
                        items(
                            items = messages,
                            key = { it.timestamp }) { message ->
                            MessageItem(
                                viewModel = viewModel,
                                message = message,
                                modifier = Modifier // todo remove/simplify
                                    .animateItem(
                                        fadeInSpec = null,
                                        fadeOutSpec = null,
                                        placementSpec = spring(stiffness = Spring.StiffnessLow))) }}

                    FeedMode.Grouped -> {
                        groups.forEach { (origin, messages) ->
                            val isExpanded = expandedGroups[origin] == true
                            item(origin.key) {
                                GroupHeaderItem(
                                    viewModel = viewModel,
                                    origin = origin,
                                    messageCount = messages.size,
                                    isExpanded = isExpanded,
                                    onExpand = { expandedGroups[origin] = !isExpanded })
                            }
                            if (isExpanded) {
                                items(
                                    items = messages,
                                    key = { origin.key + it.timestamp }) { message ->
                                    MessageItem(
                                        viewModel = viewModel,
                                        message = message,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                            .animateItem( // todo remove
                                                fadeInSpec = null,
                                                fadeOutSpec = null,
                                                placementSpec = spring(stiffness = Spring.StiffnessLow)))
                                }
                            }
                        }
                    }
                }}
            }
        }
    }
}

private fun groupMessages(allMessages: List<Message>) =
    allMessages.groupBy { resolveMessageOrigin(it) } // Map<MessageOrigin, List<Message>>
        .toList() // List<Pair<MessageOrigin, List<Message>>>
        .sortedBy { (origin, messages) -> origin.order } // List<Pair<MessageOrigin, List<Message>>> sorted by origin order
        .associate { (origin, messages) ->
            origin to messages.sortedByDescending { it.timestamp } // LinkedHashMap<MessageOrigin, List<Message>> sorted by origin and timestamp
        }