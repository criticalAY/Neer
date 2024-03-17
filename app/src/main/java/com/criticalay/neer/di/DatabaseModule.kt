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

package com.criticalay.neer.di

import android.content.Context
import androidx.room.Room
import com.criticalay.neer.data.NeerDatabase
import com.criticalay.neer.utils.Constants.NEER_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context = context,
            NeerDatabase::class.java,
            NEER_DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideUserDao(database: NeerDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideBeverageDao(database: NeerDatabase) = database.beverageDao()

    @Provides
    @Singleton
    fun provideIntakeDao(database: NeerDatabase) = database.intakeDao()

    @Provides
    @Singleton
    fun provideAlarmDao(database: NeerDatabase) = database.alarmDao()

}