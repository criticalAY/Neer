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

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Recommend
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Support(
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val supportMeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/criticalAY"))
    val gitHubIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/criticalAY/Neer"))

    SettingItem(modifier = modifier.clickable {
        showBottomSheet= true
    }) {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = 5.dp),
                imageVector = Icons.Rounded.Support,
                contentDescription = stringResource(R.string.support)
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontSize = 18.sp,
                text = stringResource(R.string.support),
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Column {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Icon(
                        modifier= Modifier.padding(5.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null)
                    Text(
                        modifier= Modifier.padding(5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        text = stringResource(id = R.string.support)
                    )

                }
               Contribute {
                   context.startActivity(supportMeIntent)
               }

                HorizontalDivider()

                SupportMe {
                    context.startActivity(gitHubIntent)
                }

                HorizontalDivider()


            }
        }
    }
}

@Composable
private fun Contribute(
    modifier: Modifier= Modifier,
    handleClick : () -> Unit
){
    SettingItem(modifier= modifier.clickable {
        handleClick()
    }) {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                modifier = Modifier.padding(end = 5.dp),
                imageVector = Icons.Rounded.Link,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontSize = 18.sp,
                text = "Contribute to the project")
        }
    }
}

@Composable
private fun SupportMe(
    modifier: Modifier= Modifier,
    handleClick : () -> Unit
){
    SettingItem(modifier= modifier.clickable {
        handleClick()
    }) {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                modifier = Modifier.padding(end = 5.dp),
                imageVector = Icons.Rounded.Recommend,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                fontSize = 18.sp,
                text = "Support me")
        }
    }
}