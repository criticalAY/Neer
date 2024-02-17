/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.criticalay.neer.ui.composables.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.progressbar.CustomCircularProgressIndicator
import com.criticalay.neer.ui.theme.Light_blue
import com.criticalay.neer.ui.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {

            Text(
                text = "Drink Target",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier.fillMaxHeight(0.4f)
            ) {
                CustomCircularProgressIndicator(
                    initialValue = 50,
                    maxValue = 200,
                    primaryColor = Color.Blue,
                    secondaryColor = Light_blue,
                    circleRadius = 230f,
                    onPositionChange = {}
                )
            }

            Button(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_water_full),
                    contentDescription = stringResource(
                        R.string.add_water
                    )
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text =stringResource(
                    R.string.add_water
                ))

            }

            Spacer(Modifier.size(26.dp))

            HorizontalDivider()

            Spacer(Modifier.size(26.dp))

            Row(modifier = Modifier.padding(start = 8.dp)) {
                Icon(imageVector = Icons.Filled.WaterDrop, contentDescription = null )

                Spacer(Modifier.size(8.dp))

                Text(
                    fontSize = 18.sp,
                    modifier = Modifier,
                    text = stringResource(R.string.today_record)
                )
            }

        }

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewHome() {
    Home()
}