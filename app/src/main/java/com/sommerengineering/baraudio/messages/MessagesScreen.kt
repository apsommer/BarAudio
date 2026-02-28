package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.blur
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
    val isDarkMode = viewModel.isDarkMode
    val backgroundImageId =
        if (isDarkMode) R.drawable.background_skyline_dark
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

                BackgroundImage(backgroundImageId, isDarkMode)

                // messages
                LazyColumn(state = listState) { when (feedMode) {

                    // all messages by timestamp
                    FeedMode.Linear -> {
                        itemsIndexed(
                            items = messages,
                            key = { _, it -> it.timestamp }) { index, message ->
                            MessageItem(
                                viewModel = viewModel,
                                message = message,
                                isShowDivider = index != messages.lastIndex,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = spring(stiffness = Spring.StiffnessLow))) }}

                    // grouped messages by origin, then by timestamp
                    FeedMode.Grouped -> {
                        groups.forEachIndexed { groupIndex, (origin, messages) ->

                            // group header
                            val isExpanded = expandedGroups[origin] == true
                            item(origin.key) {
                                GroupHeaderItem(
                                    viewModel = viewModel,
                                    origin = origin,
                                    messageCount = messages.size,
                                    isExpanded = isExpanded,
                                    isShowDivider = groupIndex != groups.size - 1,
                                    onExpand = { expandedGroups[origin] = !isExpanded })
                            }

                            // messages in group
                            if (isExpanded) {
                                itemsIndexed(
                                    items = messages,
                                    key = { _, it -> origin.key + it.timestamp }) { index, message ->
                                    MessageItem(
                                        viewModel = viewModel,
                                        message = message,
                                        isShowDivider = !(groupIndex == groups.lastIndex && index == messages.lastIndex),
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

private fun groupMessages(allMessages: List<Message>): List<MessageGroup> =
    allMessages.groupBy { resolveMessageOrigin(it) } // Map<MessageOrigin, List<Message>>
        .map { (origin, messages) -> MessageGroup(origin, messages) } // List<MessageGroup>
        .sortedBy { it.origin.order } // List<MessageGroup> sorted by origin (already timestamp descending)
