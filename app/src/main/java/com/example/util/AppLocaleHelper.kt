package com.example.util

object AppLocaleHelper {
    fun t(key: String, isEnglish: Boolean): String {
        return if (isEnglish) {
            when (key) {
                "app_title" -> "Yuva Shakti Group"
                "app_subtitle" -> "Bhati Family • Monthly Fund"
                "total_collected" -> "Total Collected"
                "total_target" -> "Total Target"
                "monthly_progress" -> "Monthly Fund Progress (Consensus)"
                "total_members" -> "Total Members"
                "paid" -> "Paid"
                "pending" -> "Pending"
                "all" -> "All"
                "search_placeholder" -> "Search member by name or mobile..."
                "members_tab" -> "Members"
                "accounting_tab" -> "Accounts & Slips"
                "gallery_tab" -> "Events & Photos"
                "chat_tab" -> "Group Chat"
                "daily_thought" -> "Today's Inspiration"
                "qr_payment" -> "QR Code Payment"
                "request_deposit" -> "Request Deposit Verification"
                "admin_mode" -> "Admin Mode"
                "viewer_mode" -> "Member Mode"
                "settings" -> "Settings"
                "theme_mode" -> "Theme Mode"
                "language" -> "Language"
                "share_app" -> "Share Application"
                "urgent_alert" -> "Urgent Alert"
                "whatsapp_report" -> "WhatsApp Report"
                "today" -> "Today"
                "days_left" -> "days remaining"
                "save" -> "Save"
                "cancel" -> "Cancel"
                "close" -> "Close"
                else -> key
            }
        } else {
            when (key) {
                "app_title" -> "युवा शक्ति ग्रुप"
                "app_subtitle" -> "भाटी परिवार • मासिक अमावस्या फंड"
                "total_collected" -> "कुल एकत्रित राशि"
                "total_target" -> "कुल लक्ष्य"
                "monthly_progress" -> "मासिक फंड प्रगति (सर्वसम्मति अनुसार)"
                "total_members" -> "कुल सदस्य"
                "paid" -> "जमा"
                "pending" -> "बाकी"
                "all" -> "सभी"
                "search_placeholder" -> "सदस्य का नाम या मोबाइल नंबर खोजें..."
                "members_tab" -> "सदस्य सूची"
                "accounting_tab" -> "बहीखाता"
                "gallery_tab" -> "कार्यक्रम फ़ोटो"
                "chat_tab" -> "ग्रुप संवाद"
                "daily_thought" -> "दैनिक सुविचार"
                "qr_payment" -> "QR कोड पेमेंट"
                "request_deposit" -> "भुगतान सत्यापन अनुरोध"
                "admin_mode" -> "एडमिन मोड"
                "viewer_mode" -> "दर्शक मोड"
                "settings" -> "सेटिंग्स"
                "theme_mode" -> "थीम मोड"
                "language" -> "भाषा"
                "share_app" -> "ऐप शेयर करें"
                "urgent_alert" -> "आवश्यक सूचना"
                "whatsapp_report" -> "व्हाट्सएप रिपोर्ट"
                "today" -> "आज"
                "days_left" -> "दिन शेष"
                "save" -> "सहेजें"
                "cancel" -> "रद्द करें"
                "close" -> "बंद करें"
                else -> key
            }
        }
    }
}
