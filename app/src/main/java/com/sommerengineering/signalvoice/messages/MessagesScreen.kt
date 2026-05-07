package com.sommerengineering.signalvoice.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.message.GroupHeaderItem
import com.sommerengineering.signalvoice.message.MessageItem
import com.sommerengineering.signalvoice.settings.SettingsDrawer
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.source.MessageGroup
import com.sommerengineering.signalvoice.source.MessageOrigin
import com.sommerengineering.signalvoice.source.resolveMessageOrigin
import com.sommerengineering.signalvoice.speak.ForegroundSpeechService
import com.sommerengineering.signalvoice.uitls.emptyStateSubtitle
import com.sommerengineering.signalvoice.uitls.emptyStateTitle
import com.sommerengineering.signalvoice.uitls.guestEmptyStateSubtitle
import com.sommerengineering.signalvoice.uitls.notificationsDisabledSubtitle
import com.sommerengineering.signalvoice.uitls.notificationsDisabledTitle
import kotlinx.coroutines.flow.first

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    onCustomSignalClick: () -> Unit,
) {

    val context = LocalContext.current

    // lazy column of messages
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // setting drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // special cards: notifications disabled, user signal empty state
    val areNotificationsEnabled = viewModel.areNotificationsEnabled
    val isEmptyState = viewModel.isEmptyState

    // feed mode: linear, or grouped
    val feedMode = viewModel.feedMode
    val groups = remember(messages) { groupMessages(messages) }
    val expandedGroups = remember(feedMode) { mutableStateMapOf<MessageOrigin, Boolean>() }

    // session
    val session = viewModel.session

    // start/stop speech service
    LaunchedEffect(Unit) {
        viewModel.isListening.collect { enabled ->
            if (enabled) ForegroundSpeechService.start(context)
            else ForegroundSpeechService.stop(context)
        }
    }

    // scroll to latest: user at top of list or inline card appears
    LaunchedEffect(messages.size, areNotificationsEnabled, isEmptyState) {

        val isCardVisible = !areNotificationsEnabled || isEmptyState
        val isUserNearTop = listState.firstVisibleItemIndex <= 1

        // wait for any ongoing scroll to finish
        snapshotFlow { listState.isScrollInProgress }.first { !it }

        // new message arrives
        if (!isCardVisible && isUserNearTop) {
            listState.scrollToItem(0)
        }

        // card appears
        if (isCardVisible && listState.firstVisibleItemIndex > 0) {
            listState.animateScrollToItem(0)
        }
    }

    // measure appbar height for settings drawer
    var appbarHeightPx by remember { mutableStateOf(0) }
    val appbarHeight = with(LocalDensity.current) { appbarHeightPx.toDp() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                windowInsets = WindowInsets(0)
            ) {
                SettingsDrawer(
                    viewModel = viewModel,
                    onSignOut = onSignOut,
                    onCustomSignalClick = onCustomSignalClick,
                    appBarHeight = appbarHeight
                )
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor.copy(alpha = 0.5f)
    ) {

        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier.onSizeChanged { appbarHeightPx = it.height }
                ) {
                    MessagesTopBar(
                        viewModel = viewModel,
                        onToggleFeedMode = { viewModel.toggleFeedMode() },
                        onToggleListening = { viewModel.toggleListening(context) })
                }
            }
        ) { padding ->

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                // messages
                LazyColumn(state = listState) {

                    // notification card
                    item {
                        InlineActionCard(
                            iconRes = R.drawable.notifications,
                            title = notificationsDisabledTitle,
                            subTitle = notificationsDisabledSubtitle,
                            visible = !areNotificationsEnabled,
                            onClick = { viewModel.launchSystemNotificationSettings(context) },
                            titleWeight = FontWeight.Bold
                        )
                    }

                    // user signal empty state card
                    item {
                        InlineActionCard(
                            iconRes = R.drawable.webhook,
                            title = emptyStateTitle,
                            subTitle =
                                if (session is Authenticated) emptyStateSubtitle
                                else guestEmptyStateSubtitle,
                            onClick = onCustomSignalClick,
                            visible = isEmptyState
                        )
                    }

                    when (feedMode) {

                        // all messages by timestamp
                        FeedMode.Linear -> {
                            itemsIndexed(
                                items = messages,
                                key = { _, it -> it.timestamp }) { index, message ->

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
