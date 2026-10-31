package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.CycleHistoryEntity
import com.example.data.ExpenseEntity
import com.example.data.GroupMessageEntity
import com.example.data.LoanRequestEntity
import com.example.data.MemberDao
import com.example.data.MemberEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CloudBackupHelper {

    private const val BACKUP_FILE_NAME = "YuvaShakti_Parivar_CloudBackup.json"

    /**
     * Creates a full JSON snapshot of all members, expenses, loans, chat, and cycle history.
     * Saves it to /sdcard/Download/ and internal cache.
     */
    suspend fun createFullBackupJson(
        memberDao: MemberDao,
        context: Context,
        additionalSettings: Map<String, String> = emptyMap()
    ): Pair<File, String> = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("appName", "युवा शक्ति ग्रुप भाटी परिवार कुचेरा")
        root.put("version", "2.5.0")
        root.put("backupTimestamp", System.currentTimeMillis())
        root.put("backupDate", SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()))

        // 1. Members
        val members = memberDao.getAllMembersSnapshot()
        val membersArray = JSONArray()
        for (m in members) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("name", m.name)
                put("phone", m.phone)
                put("monthlyAmount", m.monthlyAmount)
                put("status", m.status)
                put("paidDate", m.paidDate)
                put("paymentMode", m.paymentMode)
                put("notes", m.notes)
                put("sortOrder", m.sortOrder)
                put("instagramId", m.instagramId)
                put("facebookId", m.facebookId)
                put("whatsappNumber", m.whatsappNumber)
                put("villageCity", m.villageCity)
            }
            membersArray.put(obj)
        }
        root.put("members", membersArray)

        // 2. Expenses
        val expenses = memberDao.getAllExpensesSnapshot()
        val expensesArray = JSONArray()
        for (e in expenses) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("itemName", e.itemName)
                put("amount", e.amount)
                put("category", e.category)
                put("date", e.date)
                put("receiptSlipUri", e.receiptSlipUri)
                put("recordedBy", e.recordedBy)
                put("timestamp", e.timestamp)
            }
            expensesArray.put(obj)
        }
        root.put("expenses", expensesArray)

        // 3. Loan Requests
        val loans = memberDao.getAllLoanRequestsSnapshot()
        val loansArray = JSONArray()
        for (l in loans) {
            val obj = JSONObject().apply {
                put("id", l.id)
                put("memberId", l.memberId)
                put("memberName", l.memberName)
                put("memberPhone", l.memberPhone)
                put("amountRequested", l.amountRequested)
                put("purpose", l.purpose)
                put("returnPromiseDays", l.returnPromiseDays)
                put("requestDate", l.requestDate)
                put("status", l.status)
                put("adminNote", l.adminNote)
                put("upVotes", l.upVotes)
                put("downVotes", l.downVotes)
                put("votedMemberIds", l.votedMemberIds)
                put("timestamp", l.timestamp)
            }
            loansArray.put(obj)
        }
        root.put("loans", loansArray)

        // 4. Messages
        val messages = memberDao.getAllMessagesSnapshot()
        val msgArray = JSONArray()
        for (msg in messages) {
            val obj = JSONObject().apply {
                put("id", msg.id)
                put("senderName", msg.senderName)
                put("senderRole", msg.senderRole)
                put("message", msg.message)
                put("timestamp", msg.timestamp)
                put("isSystemAlert", msg.isSystemAlert)
                put("isWhatsAppMessage", msg.isWhatsAppMessage)
                put("isUrgentAlert", msg.isUrgentAlert)
            }
            msgArray.put(obj)
        }
        root.put("messages", msgArray)

        // 5. Cycle History
        val histories = memberDao.getCycleHistorySnapshot()
        val histArray = JSONArray()
        for (h in histories) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("cycleName", h.cycleName)
                put("dateRecorded", h.dateRecorded)
                put("totalMembers", h.totalMembers)
                put("paidMembers", h.paidMembers)
                put("pendingMembers", h.pendingMembers)
                put("totalCollected", h.totalCollected)
                put("targetAmount", h.targetAmount)
            }
            histArray.put(obj)
        }
        root.put("cycleHistory", histArray)

        // 6. Settings
        val settingsObj = JSONObject()
        for ((k, v) in additionalSettings) {
            settingsObj.put(k, v)
        }
        root.put("settings", settingsObj)

        val jsonString = root.toString(2)

        // Write to Downloads folder for safety and persistent access
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) downloadDir.mkdirs()
        val backupFile = File(downloadDir, BACKUP_FILE_NAME)
        backupFile.writeText(jsonString, Charsets.UTF_8)

        // Also write to internal cache
        val internalFile = File(context.cacheDir, BACKUP_FILE_NAME)
        internalFile.writeText(jsonString, Charsets.UTF_8)

        Pair(backupFile, jsonString)
    }

    /**
     * Restores full database snapshot from JSON text.
     * Returns count of restored members.
     */
    suspend fun restoreFromJson(
        jsonString: String,
        memberDao: MemberDao,
        onSettingsRestored: (Map<String, String>) -> Unit = {}
    ): Int = withContext(Dispatchers.IO) {
        val root = JSONObject(jsonString)

        // 1. Members
        if (root.has("members")) {
            val membersArray = root.getJSONArray("members")
            val restoredMembers = mutableListOf<MemberEntity>()
            for (i in 0 until membersArray.length()) {
                val o = membersArray.getJSONObject(i)
                restoredMembers.add(
                    MemberEntity(
                        id = o.optLong("id", 0L),
                        name = o.optString("name", "सदस्य"),
                        phone = o.optString("phone", ""),
                        monthlyAmount = o.optInt("monthlyAmount", 1000),
                        status = o.optString("status", "Pending"),
                        paidDate = o.optString("paidDate", ""),
                        paymentMode = o.optString("paymentMode", ""),
                        notes = o.optString("notes", ""),
                        sortOrder = o.optInt("sortOrder", i + 1),
                        instagramId = o.optString("instagramId", ""),
                        facebookId = o.optString("facebookId", ""),
                        whatsappNumber = o.optString("whatsappNumber", ""),
                        villageCity = o.optString("villageCity", "")
                    )
                )
            }
            if (restoredMembers.isNotEmpty()) {
                memberDao.insertMembers(restoredMembers)
            }
        }

        // 2. Expenses
        if (root.has("expenses")) {
            val arr = root.getJSONArray("expenses")
            val restored = mutableListOf<ExpenseEntity>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                restored.add(
                    ExpenseEntity(
                        id = o.optLong("id", 0L),
                        itemName = o.optString("itemName", ""),
                        amount = o.optInt("amount", 0),
                        category = o.optString("category", "व्यवस्था"),
                        date = o.optString("date", ""),
                        receiptSlipUri = o.optString("receiptSlipUri", ""),
                        recordedBy = o.optString("recordedBy", "व्यवस्थापक"),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            if (restored.isNotEmpty()) memberDao.insertExpenses(restored)
        }

        // 3. Loans
        if (root.has("loans")) {
            val arr = root.getJSONArray("loans")
            val restored = mutableListOf<LoanRequestEntity>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                restored.add(
                    LoanRequestEntity(
                        id = o.optLong("id", 0L),
                        memberId = o.optLong("memberId", 0L),
                        memberName = o.optString("memberName", ""),
                        memberPhone = o.optString("memberPhone", ""),
                        amountRequested = o.optInt("amountRequested", 0),
                        purpose = o.optString("purpose", ""),
                        returnPromiseDays = o.optInt("returnPromiseDays", 30),
                        requestDate = o.optString("requestDate", ""),
                        status = o.optString("status", "Pending"),
                        adminNote = o.optString("adminNote", ""),
                        upVotes = o.optInt("upVotes", 0),
                        downVotes = o.optInt("downVotes", 0),
                        votedMemberIds = o.optString("votedMemberIds", ""),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            if (restored.isNotEmpty()) memberDao.insertLoanRequests(restored)
        }

        // 4. Messages
        if (root.has("messages")) {
            val arr = root.getJSONArray("messages")
            val restored = mutableListOf<GroupMessageEntity>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                restored.add(
                    GroupMessageEntity(
                        id = o.optLong("id", 0L),
                        senderName = o.optString("senderName", "सदस्य"),
                        senderRole = o.optString("senderRole", "सदस्य"),
                        message = o.optString("message", ""),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                        isSystemAlert = o.optBoolean("isSystemAlert", false),
                        isWhatsAppMessage = o.optBoolean("isWhatsAppMessage", false),
                        isUrgentAlert = o.optBoolean("isUrgentAlert", false)
                    )
                )
            }
            if (restored.isNotEmpty()) memberDao.insertMessages(restored)
        }

        // 5. Cycle History
        if (root.has("cycleHistory")) {
            val arr = root.getJSONArray("cycleHistory")
            val restored = mutableListOf<CycleHistoryEntity>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                restored.add(
                    CycleHistoryEntity(
                        id = o.optLong("id", 0L),
                        cycleName = o.optString("cycleName", ""),
                        dateRecorded = o.optLong("dateRecorded", System.currentTimeMillis()),
                        totalMembers = o.optInt("totalMembers", 0),
                        paidMembers = o.optInt("paidMembers", 0),
                        pendingMembers = o.optInt("pendingMembers", 0),
                        totalCollected = o.optInt("totalCollected", 0),
                        targetAmount = o.optInt("targetAmount", 0)
                    )
                )
            }
            if (restored.isNotEmpty()) memberDao.insertCycleHistories(restored)
        }

        // 6. Settings
        if (root.has("settings")) {
            val sObj = root.getJSONObject("settings")
            val map = mutableMapOf<String, String>()
            val keys = sObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k] = sObj.optString(k, "")
            }
            onSettingsRestored(map)
        }

        memberDao.getMemberCount()
    }

    /**
     * Direct upload/share to Google Drive app or Cloud storage
     */
    fun shareBackupToGoogleDrive(context: Context, backupFile: File) {
        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "युवा शक्ति ग्रुप भाटी परिवार - Google Drive ऑनलाइन बैकअप")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🚩 *युवा शक्ति ग्रुप भाटी परिवार कुचेरा*\n\n" +
                    "📁 यह एप्लिकेशन का पूर्ण Google Drive सुरक्षित डेटा बैकअप है।\n" +
                    "इसे अपने Google Account / Google Drive में 'Save to Drive' करके सुरक्षित रखें।\n" +
                    "फ़ोन बदलने या नया ऐप इंस्टॉल करने पर इस फ़ाइल से पूरा बहीखाता 1-क्लिक में रिस्टोर हो जाएगा।"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Check if Google Drive is installed directly
            val googleDrivePackage = "com.google.android.apps.docs"
            var targeted = false
            try {
                context.packageManager.getPackageInfo(googleDrivePackage, 0)
                shareIntent.setPackage(googleDrivePackage)
                targeted = true
            } catch (_: Exception) {}

            if (targeted) {
                context.startActivity(shareIntent)
            } else {
                context.startActivity(Intent.createChooser(shareIntent, "📁 Google Drive में बैकअप सुरक्षित करें"))
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Drive बैकअप त्रुटि: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Legacy alias for backward compatibility
     */
    fun shareBackupToJioCloud(context: Context, backupFile: File) {
        shareBackupToGoogleDrive(context, backupFile)
    }

    /**
     * Checks if a persistent local backup file exists in Downloads
     */
    fun getLocalBackupFile(): File? {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, BACKUP_FILE_NAME)
        return if (file.exists() && file.length() > 0) file else null
    }
}
