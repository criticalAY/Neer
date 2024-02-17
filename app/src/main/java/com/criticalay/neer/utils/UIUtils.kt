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

package com.criticalay.neer.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.ceil


object UIUtils {
    val Int.dpf: Float
        get() {
            return dp.toFloat()
        }


    val Float.dpf: Float
        get() {
            return dp.toFloat()
        }

    val Int.dp: Int
        get() {
            return if (this == 0) {
                0
            } else ceil((Resources.getSystem().displayMetrics.density * this).toDouble()).toInt()
        }

    val Float.dp: Int
        get() {
            return if (this == 0f) {
                0
            } else ceil((Resources.getSystem().displayMetrics.density * this).toDouble()).toInt()
        }
}