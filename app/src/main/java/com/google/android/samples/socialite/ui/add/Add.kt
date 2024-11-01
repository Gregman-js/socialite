package com.google.android.samples.socialite.ui.add

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.samples.socialite.R
import com.google.android.samples.socialite.ui.AnimationConstants
import com.google.android.samples.socialite.ui.home.ChatList
import com.google.android.samples.socialite.ui.home.HomeAppBar
import com.google.android.samples.socialite.ui.home.HomeBackground
import com.google.android.samples.socialite.ui.home.HomeViewModel
import com.google.android.samples.socialite.ui.home.timeline.Timeline

@Composable
fun Add(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { HomeAppBar(title = stringResource(R.string.add)) },
    ) { innerPadding ->
        HomeBackground(modifier = Modifier.fillMaxSize())
        ChatList(
            chats = emptyList(),
            contentPadding = innerPadding,
            onChatClicked = {},
            modifier = modifier,
        )
    }
}
