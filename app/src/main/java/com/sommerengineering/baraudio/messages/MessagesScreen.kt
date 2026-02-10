package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.areNotificationsEnabled
import com.sommerengineering.baraudio.backgroundPadding
import com.sommerengineering.baraudio.colorTransitionTimeMillis
import com.sommerengineering.baraudio.recentMessageTimeMillis
import com.sommerengineering.baraudio.settings.SettingsDrawer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit) {

    val messages by viewModel.messages.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    val isDarkMode = viewModel.isDarkMode
    val backgroundImageId =
        if (isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

    val quoteState by viewModel.mindfulnessQuoteState.collectAsState()
    val quote = (quoteState as? MindfulnessQuoteState.Success)?.mindfulnessQuote?.quote
    val isShowQuote = viewModel.isShowQuote

    LaunchedEffect(Unit) {
        viewModel.startListeningToDatabase()
    }

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
        scrimColor = DrawerDefaults.scrimColor.copy(
            alpha = 0.5f)) {

        Scaffold(

            // top bar
            topBar = {
                MessagesTopBar(
                    viewModel = viewModel,
                    onSettingsClick = {
                        coroutine.launch {
                            drawerState.open()
                        }
                    })
            },

            // fab, mute button
            floatingActionButton = {
                MessagesFloatingActionButton(
                    viewModel = viewModel)
            },

            bottomBar = {
                AllowNotificationsBottomBar(
                    areNotificationsAllowed = areNotificationsEnabled)
            }

        ) { padding ->

            // screen container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) {

                // fade background as new messages appear
                val animatedAlpha by animateFloatAsState(
                    targetValue =
                        if (messages.isEmpty()) { 1f }
                        else { (1 - 0.2 * messages.size).toFloat() },
                    animationSpec = tween(colorTransitionTimeMillis))

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
                            .align(Alignment.Center)
                            .alpha(animatedAlpha)
                    )
                }

                // background image
                Image(
                    modifier = Modifier
                        .padding(
                            start = backgroundPadding,
                            end = backgroundPadding,
                            bottom = 64.dp
                        )
                        .align(Alignment.Center),
                    painter = painterResource(backgroundImageId),
                    contentDescription = null,
                    alpha = animatedAlpha
                )

                // pull to refresh
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    onRefresh = {

                        // start spinner
                        isRefreshing = true

                        // todo

                        // dismiss indicator
                        coroutine.launch {
                            delay(1000)
                            isRefreshing = false
                        }
                    },

                    // default spinner from top of screen
                    indicator = {
                        Indicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter),
                            isRefreshing = isRefreshing,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            state = pullToRefreshState
                        )
                    }) {

                    // messages list
                    LazyColumn(
                        state = listState,
                        reverseLayout = true) {

                        items(
                            items = messages,
                            key = { it.timestamp }) { message ->

                            // highlight recent messages
                            var isRecent by remember { mutableStateOf(true) }
                            isRecent = recentMessageTimeMillis * 60 > System.currentTimeMillis() - message.timestamp.toLong()

                            MessageItem(
                                viewModel = viewModel,
                                isRecent = isRecent,
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        fadeOutSpec = spring(stiffness = Spring.StiffnessVeryLow),
                                        placementSpec = spring(stiffness = Spring.StiffnessVeryLow)
                                    ),
                                message = message,
                                onRemove = {
                                    viewModel.deleteMessage(message)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
