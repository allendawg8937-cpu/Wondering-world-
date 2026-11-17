package com.example.network

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.ui.DashboardTab
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecureUrlAndSqlTest {

    @Test
    fun normalizeHttpsUrl_upgradesHttpAndBlocksUnsafeSchemes() {
        assertEquals("https://988lifeline.org/", SecureUrlHelper.normalizeHttpsUrl("http://988lifeline.org/"))
        assertEquals("https://wereallequals.world/finance", SecureUrlHelper.normalizeHttpsUrl("wereallequals.world/finance"))
        assertEquals(SecureUrlHelper.CANONICAL_HTTPS_ORIGIN, SecureUrlHelper.normalizeHttpsUrl("javascript:alert(1)"))
        assertEquals(SecureUrlHelper.CANONICAL_HTTPS_ORIGIN, SecureUrlHelper.normalizeHttpsUrl("file:///etc/passwd"))
        assertTrue(SecureUrlHelper.isStrictHttpsUrl("https://wereallequals.world/resources"))
        assertFalse(SecureUrlHelper.isStrictHttpsUrl("http://wereallequals.world/resources"))
    }

    @Test
    fun resolveDashboardTabFromUri_parsesTrustedHttpsAndPlatformUrls() {
        assertEquals(
            "https://wereallequals.world/finance?stage=student",
            SecureUrlHelper.buildPlatformModuleUrl("finance", "student")
        )
        assertEquals(
            DashboardTab.FINANCE_GOALS,
            SecureUrlHelper.resolveDashboardTabFromUri(Uri.parse("https://wereallequals.world/finance?stage=student"))
        )
        assertEquals(
            DashboardTab.RESOURCES,
            SecureUrlHelper.resolveDashboardTabFromUri(Uri.parse("wereallequals://platform/resources"))
        )
        assertEquals(
            DashboardTab.CODE_OF_CONDUCT,
            SecureUrlHelper.resolveDashboardTabFromUri(Uri.parse("https://wereallequals.world/dating"))
        )
        assertNull(
            SecureUrlHelper.resolveDashboardTabFromUri(Uri.parse("https://untrusted-domain.example/finance"))
        )
    }

    @Test
    fun sqliteSchemaSql_executesCleanlyAndEnforcesHttpsCheckConstraint() {
        val candidates = listOf(
            File("database/sqlite_schema.sql"),
            File("../database/sqlite_schema.sql")
        )
        val schemaFile = candidates.firstOrNull { it.exists() }
        checkNotNull(schemaFile) { "database/sqlite_schema.sql not found in workspace" }

        val sql = schemaFile.readText()
        val statements = sql.lines()
            .filterNot { it.trimStart().startsWith("--") }
            .joinToString("\n")
            .split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val db = SQLiteDatabase.create(null)
        try {
            statements.forEach { stmt -> db.execSQL(stmt) }

            // Valid HTTPS insert succeeds
            db.execSQL(
                "INSERT INTO saved_resources (resourceId, userId, title, subtitle, badge, url, category, stageId, createdAtMillis) " +
                    "VALUES ('res_988', 'u1', '988 Lifeline', 'Support', 'FREE', 'https://988lifeline.org/', 'Crisis', 'all', 1700000000000)"
            )

            // Insecure http:// URL is rejected by SQL CHECK constraint
            try {
                db.execSQL(
                    "INSERT INTO saved_resources (resourceId, userId, title, subtitle, badge, url, category, stageId, createdAtMillis) " +
                        "VALUES ('res_bad', 'u1', 'Bad URL', 'Insecure', 'BAD', 'http://insecure.example.com', 'Crisis', 'all', 1700000000001)"
                )
                fail("Expected SQLiteConstraintException for non-HTTPS URL")
            } catch (_: SQLiteConstraintException) {
                // Expected CHECK violation
            }
        } finally {
            db.close()
        }
    }
}
