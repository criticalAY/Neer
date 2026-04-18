/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
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

package com.criticalay.neer.hydration

import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import java.time.Duration
import java.time.LocalTime
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Research-backed daily water goal & reminder schedule generator.
 *
 * Baseline comes from the National Academies / Institute of Medicine Adequate
 * Intake report (Dietary Reference Intakes for Water, Potassium, Sodium,
 * Chloride, and Sulfate, 2004): 3.7 L/day total for adult men, 2.7 L/day for
 * adult women. The weight-based multiplier of 35 ml/kg is the commonly cited
 * mid-point of the 30–40 ml/kg clinical range. We take the larger of the two
 * so very-low-weight profiles don't fall below the AI floor, and apply a 10 %
 * reduction past age 65 to match the reduced thirst/kidney-concentrating
 * response documented in geriatric hydration literature.
 */
object HydrationPlan {
    private const val ML_PER_KG = 35.0
    private const val LBS_TO_KG = 0.45359237

    private const val AI_FLOOR_MALE_ML = 3700
    private const val AI_FLOOR_FEMALE_ML = 2700
    private const val AI_FLOOR_OTHER_ML = 3200

    private const val ELDERLY_AGE_CUTOFF = 65
    private const val ELDERLY_MULTIPLIER = 0.9

    private const val MIN_DAILY_GOAL_ML = 1500
    private const val MAX_DAILY_GOAL_ML = 5000
    private const val GOAL_ROUNDING_STEP_ML = 50

    private const val DEFAULT_SLOT_SPACING_MINUTES = 90L
    private const val PRE_SLEEP_BUFFER_MINUTES = 60L
    private const val AMOUNT_ROUNDING_STEP_ML = 50
    private const val MORNING_BOOST = 1.2
    private const val EVENING_TAPER = 0.85

    /**
     * Daily water goal in ml, based on the user's body composition.
     *
     * @param weight user weight in the app's current unit system
     * @param gender biological sex used for AI floor selection
     * @param ageYears nullable; only used for the elderly adjustment
     * @param units unit system the weight is expressed in
     */
    fun computeDailyGoalMl(
        weight: Double,
        gender: Gender,
        ageYears: Int?,
        units: Units,
    ): Int {
        val weightKg = if (units == Units.LBS_OZ) weight * LBS_TO_KG else weight

        val weightBased = weightKg * ML_PER_KG
        val aiFloor = when (gender) {
            Gender.MALE -> AI_FLOOR_MALE_ML
            Gender.FEMALE -> AI_FLOOR_FEMALE_ML
            Gender.OTHER -> AI_FLOOR_OTHER_ML
        }.toDouble()

        val raw = max(weightBased, aiFloor)
        val adjusted = if (ageYears != null && ageYears >= ELDERLY_AGE_CUTOFF) {
            raw * ELDERLY_MULTIPLIER
        } else {
            raw
        }

        val rounded = (adjusted / GOAL_ROUNDING_STEP_ML).roundToInt() * GOAL_ROUNDING_STEP_ML
        return rounded.coerceIn(MIN_DAILY_GOAL_ML, MAX_DAILY_GOAL_ML)
    }

    /**
     * A human-readable breakdown for showing the formula on screen.
     */
    data class GoalBreakdown(
        val weightKg: Double,
        val weightBasedMl: Int,
        val aiFloorMl: Int,
        val elderlyAdjustmentApplied: Boolean,
        val goalMl: Int,
    )

    fun explain(
        weight: Double,
        gender: Gender,
        ageYears: Int?,
        units: Units,
    ): GoalBreakdown {
        val weightKg = if (units == Units.LBS_OZ) weight * LBS_TO_KG else weight
        val floor = when (gender) {
            Gender.MALE -> AI_FLOOR_MALE_ML
            Gender.FEMALE -> AI_FLOOR_FEMALE_ML
            Gender.OTHER -> AI_FLOOR_OTHER_ML
        }
        return GoalBreakdown(
            weightKg = weightKg,
            weightBasedMl = (weightKg * ML_PER_KG).roundToInt(),
            aiFloorMl = floor,
            elderlyAdjustmentApplied = ageYears != null && ageYears >= ELDERLY_AGE_CUTOFF,
            goalMl = computeDailyGoalMl(weight, gender, ageYears, units),
        )
    }

    /**
     * A single reminder slot: time of day + how many ml to drink.
     */
    data class ScheduleSlot(
        val time: LocalTime,
        val amountMl: Int,
    )

    /**
     * Distribute [goalMl] across the waking window `[wakeTime, sleepTime − 60 min]`
     * into slots spaced [slotSpacingMinutes] apart. The first slot is nudged up
     * (morning rebound) and the last is tapered down to avoid pre-bed intake;
     * the remaining total is spread evenly. All amounts round to 50 ml.
     */
    fun generateSchedule(
        goalMl: Int,
        wakeTime: LocalTime,
        sleepTime: LocalTime,
        slotSpacingMinutes: Long = DEFAULT_SLOT_SPACING_MINUTES,
    ): List<ScheduleSlot> {
        if (goalMl <= 0) return emptyList()

        val cutoff = sleepTime.minusMinutes(PRE_SLEEP_BUFFER_MINUTES)
        val awakeMinutes = minutesBetween(wakeTime, cutoff)
        if (awakeMinutes <= 0) return emptyList()

        val slots = max(2, ceil(awakeMinutes.toDouble() / slotSpacingMinutes).toInt())
        val step = awakeMinutes / (slots - 1).toLong()

        val times = (0 until slots).map { i ->
            wakeTime.plusMinutes(step * i)
        }

        val rawAmount = goalMl.toDouble() / slots
        val boosted = (rawAmount * MORNING_BOOST).roundToMl()
        val tapered = (rawAmount * EVENING_TAPER).roundToMl()
        val middleShare = ((goalMl - boosted - tapered).toDouble() / (slots - 2).coerceAtLeast(1))
            .roundToMl()

        return times.mapIndexed { index, time ->
            val amount = when {
                slots < 3 -> (goalMl.toDouble() / slots).roundToMl()
                index == 0 -> boosted
                index == slots - 1 -> tapered
                else -> middleShare
            }
            ScheduleSlot(time = time, amountMl = amount.coerceAtLeast(AMOUNT_ROUNDING_STEP_ML))
        }
    }

    private fun Double.roundToMl(): Int {
        val stepped = (this / AMOUNT_ROUNDING_STEP_ML).roundToInt() * AMOUNT_ROUNDING_STEP_ML
        return stepped.coerceAtLeast(AMOUNT_ROUNDING_STEP_ML)
    }

    private fun minutesBetween(
        start: LocalTime,
        end: LocalTime,
    ): Long {
        val direct = Duration.between(start, end).toMinutes()
        // If end < start (e.g., sleep at 01:00), wrap around 24h
        return if (direct < 0) direct + 24 * 60 else direct
    }
}
