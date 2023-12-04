/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.samples.socialite.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.samples.socialite.repository.ChatRepository
import kotlinx.coroutines.launch

class SettingsViewModel @JvmOverloads constructor(
    private val application: Application,
    private val repository: ChatRepository = ChatRepository.getInstance(application),
) : AndroidViewModel(application) {

    fun clearMessages() {
        viewModelScope.launch {
            repository.clearMessages()
            repository.initialize(reset = true)
            Toast.makeText(
                application.applicationContext,
                "Messages have been reset",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
