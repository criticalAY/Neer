/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.ui.composables.settings.SettingItem
import com.criticalay.neer.ui.composables.userdetails.DetailTextField

@Composable
fun Gender(
    modifier: Modifier = Modifier,
    userGender: String,
    newValue: (name: Gender) -> Unit,
) {
    Box {
        var expanded by remember { mutableStateOf(false) }
        SettingItem(modifier = modifier.clickable {
            expanded = !expanded
        }) {
            Row(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 5.dp),
                    imageVector = Icons.Rounded.Wc,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
                    fontSize = 18.sp,
                    text = stringResource(R.string.gender)
                )

                Text(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    text = userGender
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for (gender in Gender.entries) {
                DropdownMenuItem(
                    text = { Text(text = gender.genderValue) },
                    onClick = {
                        newValue(gender)
                        expanded = false
                    },
                    Modifier.offset(16.dp, 0.dp)
                )
            }
        }
    }
}

