package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Stages Of Life", appName)
  }

  @Test
  fun `mainActivity launches cleanly renders unified header and enters dashboard`() {
    composeTestRule.onNodeWithTag("unified_search_and_theme_header_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("enter_6d_hub_button").performScrollTo().performClick()
    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("persona_stage_selector_row").performScrollTo().assertIsDisplayed()
  }
}
