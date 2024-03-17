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

package com.criticalay.neer.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionSpacer(
    modifier: Modifier = Modifier,
     title:String? = null
){
    Box{
        Box(
            modifier = modifier
                .height(50.dp)
                .alpha(0.04f)
                .background(color = MaterialTheme.colorScheme.onSurface)

        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp) // Adjust the padding as needed
        ) {
            title?.let {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSS(){
    SectionSpacer(modifier = Modifier.fillMaxWidth())
}