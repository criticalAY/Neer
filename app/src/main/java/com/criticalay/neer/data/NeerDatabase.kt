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

package com.criticalay.neer.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.criticalay.neer.alarm.default_alarm.data.AlarmDao
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.data.dao.BeverageDao
import com.criticalay.neer.data.dao.IntakeDao
import com.criticalay.neer.data.dao.UserDao
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.utils.Constants.ALARM_DATABASE_TABLE
import com.criticalay.neer.utils.Converters

@Database(entities = [
    Beverage::class,
    User::class,
    Intake::class,
    AlarmItem::class],
    version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class NeerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun intakeDao() : IntakeDao
    abstract fun beverageDao() : BeverageDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Migration code goes here
                // Example:
                // database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER")
                db.execSQL("DROP TABLE IF EXISTS $ALARM_DATABASE_TABLE")

                // Create the new table for AlarmItem with the corrected structure
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS $ALARM_DATABASE_TABLE (" +
                            "alarmId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "time INTEGER NOT NULL," + // Corrected to INTEGER
                            "interval REAL," +
                            "title TEXT NOT NULL," +
                            "message TEXT NOT NULL," +
                            "repeating INTEGER NOT NULL DEFAULT 0," +
                            "alarmState INTEGER NOT NULL DEFAULT 1)"
                )
            }
        }

    }
}