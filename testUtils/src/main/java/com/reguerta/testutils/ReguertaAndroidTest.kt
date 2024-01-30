package com.reguerta.testutils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.reguerta.localdata.datastore.userPreferences
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule

/*****
 * Project: Reguerta
 * From: com.reguerta.testutils
 * Created By Manuel Lopera on 30/1/24 at 10:41
 * All rights reserved 2024
 */
abstract class ReguertaAndroidTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    protected lateinit var context: Context

    @Before
    open fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        clearDataStore()
    }

    @After
    open fun tearDown() {
        //
    }

    private fun clearDataStore() = runBlocking {
        context.userPreferences.edit {
            it.clear()
        }
    }
}