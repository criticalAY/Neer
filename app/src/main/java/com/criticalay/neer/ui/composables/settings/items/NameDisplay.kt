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

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.text.isDigitsOnly
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem
import com.criticalay.neer.ui.composables.userdetails.DetailTextField

@Composable
fun NameDisplay(
    modifier: Modifier = Modifier,
    userName:String,
    handleClick: () -> Unit
) {
    SettingItem(modifier = modifier .clickable {
        handleClick()
    }) {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                fontSize = 18.sp,
                text = stringResource(R.string.name)
            )

            Text(
                fontSize = 18.sp,
                text = userName
            )
        }
    }
}

@Composable
private fun EditNameDialog(
    modifier: Modifier = Modifier,
    setShowDialog: (Boolean) -> Unit,
    onDismissRequest: (Int) -> Unit,
    currentValue: Int,
) {
    var currentAmount by remember {
        mutableStateOf(currentValue.toString())
    }
    Dialog(onDismissRequest = {
        // Do nothing
    }) {

        Card(modifier = modifier) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = stringResource(R.string.edit_your_name),
                    fontSize = 20.sp
                )
                DetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = currentAmount,
                    onValueChange = { newValue ->
                        if (newValue.isNotEmpty() && newValue.isDigitsOnly()) {
                            currentAmount = newValue
                        } else if (newValue.isEmpty()) {
                            currentAmount = ""
                        }
                    },
                    label = stringResource(R.string.name),
                    placeholder = stringResource(R.string.edit_your_name),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (currentAmount.isNotBlank() && currentAmount.toInt() > 0) {
                            IconButton(onClick = { currentAmount = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { setShowDialog(false) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                    TextButton(
                        onClick = {
                            if (currentAmount.isDigitsOnly()) onDismissRequest(currentAmount.toInt())

                            setShowDialog(false)
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = currentAmount.isNotBlank() && currentAmount.toInt() > 0
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}