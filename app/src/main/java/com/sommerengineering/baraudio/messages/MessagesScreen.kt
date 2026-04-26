package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.message.GroupHeaderItem
import com.sommerengineering.baraudio.message.MessageItem
import com.sommerengineering.baraudio.settings.SettingsDrawer
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.source.MessageGroup
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.uitls.logMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    onLaunchWebhookOnboarding: () -> Unit
) {

    // lazy column of messages
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // setting drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // special cards: notifications disabled, user signal empty state
    val areNotificationsEnabled = viewModel.areNotificationsEnabled
    val isEmptyState = viewModel.isEmptyState

    // feed mode: linear, or grouped
    val feedMode = viewModel.feedMode
    val groups = remember(messages) { groupMessages(messages) }
    val expandedGroups = remember(feedMode) { mutableStateMapOf<MessageOrigin, Boolean>() }

    // scroll to latest when user at top of list
    LaunchedEffect(messages.size) {
        if (listState.firstVisibleItemIndex > 1) return@LaunchedEffect
        snapshotFlow { listState.isScrollInProgress }.first { !it } // wait for compose internal scroll
        listState.scrollToItem(0)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SettingsDrawer(
                    viewModel = viewModel,
                    onSignOut = onSignOut,
                    onLaunchSetupOnboarding = onLaunchWebhookOnboarding
                )
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(alpha = 0.5f)
    ) {

        Scaffold(
            topBar = {
                MessagesTopBar(
                    viewModel = viewModel,
                    onSettingsClick = { coroutineScope.launch { drawerState.open() } },
                    onToggleFeedMode = { viewModel.toggleFeedMode() },
                    onToggleMute = { viewModel.toggleMute() })
            }
        ) { padding ->

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                // messages
                LazyColumn(state = listState) {

                    // notification permission
                    if (!areNotificationsEnabled) {
                        item {
                            NotificationsDisabledCard()
                        }
                    }

                    // user signal empty state
                    if (isEmptyState) {
                        item {
                            EmptyStateCard(
                                onLaunchWebhookOnboarding = { onLaunchWebhookOnboarding() },
                                onDismiss = { viewModel.updateEmptyState(false) })
                        }
                    }

                    when (feedMode) {

                        // all messages by timestamp
                        FeedMode.Linear -> {
                            itemsIndexed(
                                items = messages,
                                key = { _, it -> it.timestamp }) { index, message ->
                                logMessage(message.message)
                                MessageItem(
                                    viewModel = viewModel,
                                    message = message,
                                    isShowDivider = index != messages.lastIndex
                                )
                            }
                        }

                        // grouped messages by origin, then by timestamp
                        FeedMode.Grouped -> {
                            groups.forEachIndexed { groupIndex, (origin, messages) ->
                                val isExpanded = expandedGroups[origin] == true
                                item(origin.key) {
                                    GroupHeaderItem(
                                        origin = origin,
                                        messageCount = messages.size,
                                        isExpanded = isExpanded,
                                        isShowDivider = groupIndex != groups.size - 1,
                                        onExpand = { expandedGroups[origin] = !isExpanded })
                                }
                                if (isExpanded) {
                                    itemsIndexed(
                                        items = messages,
                                        key = { _, it -> origin.key + it.timestamp }) { index, message ->
                                        MessageItem(
                                            viewModel = viewModel,
                                            message = message,
                                            isShowDivider = !(groupIndex == groups.lastIndex && index == messages.lastIndex)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // pulse icon with last signal timestamp
                LastSignalPulse(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    timestamp = messages.firstOrNull()?.timestamp ?: System.currentTimeMillis()
                        .toString()
                )
            }
        }
    }
}

private fun groupMessages(allMessages: List<Message>): List<MessageGroup> =
    allMessages.groupBy { resolveMessageOrigin(it) } // Map<MessageOrigin, List<Message>>
        .map { (origin, messages) -> MessageGroup(origin, messages) } // List<MessageGroup>
        .sortedBy { it.origin.order } // List<MessageGroup> sorted by origin (already timestamp descending)
