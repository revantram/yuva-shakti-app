package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MemberEntity::class,
        CycleHistoryEntity::class,
        DepositNotificationEntity::class,
        GroupMessageEntity::class,
        ExpenseEntity::class,
        PaymentRequestEntity::class,
        EventGalleryEntity::class,
        LoanRequestEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "yuva_shakti_bhati_fund.db"
                    ).fallbackToDestructiveMigration().build().also {
                        INSTANCE = it
                    }
                } catch (t: Throwable) {
                    android.util.Log.e("AppDatabase", "Database build failed, resetting database file", t)
                    try {
                        context.applicationContext.deleteDatabase("yuva_shakti_bhati_fund.db")
                    } catch (_: Throwable) {}
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "yuva_shakti_bhati_fund.db"
                    ).fallbackToDestructiveMigration().build().also {
                        INSTANCE = it
                    }
                }
            }
        }

        suspend fun seedInitialMembers(dao: MemberDao) {
            // Revat Ram Bhati verified details from Firestore
            val initialList = listOf(
                MemberEntity(
                    id = 1,
                    name = "रेवंतराम भाटी",
                    phone = "8696663615",
                    monthlyAmount = 0,
                    status = "Pending",
                    paidDate = "2026-10-28",
                    paymentMode = "UPI",
                    notes = "मुख्य व्यवस्थापक",
                    sortOrder = 1,
                    instagramId = "Revant__",
                    facebookId = "",
                    whatsappNumber = "8696663615",
                    villageCity = "कुचेरा"
                )
            )
            dao.insertMembers(initialList)

            // Seed initial group welcome message
            val initialMessages = listOf(
                GroupMessageEntity(
                    senderName = "रेवंत राम जी भाटी",
                    senderRole = "मुख्य व्यवस्थापक",
                    message = "🚩 जय श्री कृष्णा! युवा शक्ति ग्रुप (भाटी परिवार) के डिजिटल बहीखाता व संवाद मंच में हार्दिक स्वागत है। यहाँ से सभी सदस्य फंड, खर्च व आगामी अमावस्या की जानकारी प्राप्त कर सकते हैं।",
                    timestamp = System.currentTimeMillis() - 3600000L * 2
                ),
                GroupMessageEntity(
                    senderName = "सिस्टम सूचना",
                    senderRole = "ऑटो अपडेट",
                    message = "📢 ऑनलाइन पेमेंट व बहीखाता सुविधा सक्रिय है। नए सदस्य जोड़ने व अंशदान जमा होने पर तुरंत यहाँ सूचना प्रसारित होगी।",
                    timestamp = System.currentTimeMillis() - 1800000L,
                    isSystemAlert = true
                )
            )
            initialMessages.forEach { dao.insertMessage(it) }
        }
    }
}
