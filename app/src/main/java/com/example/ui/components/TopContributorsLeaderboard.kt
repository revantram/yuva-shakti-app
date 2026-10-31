package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DepositNotificationEntity
import com.example.data.MemberEntity
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.SaffronGold

data class ContributorRank(
    val rank: Int,
    val member: MemberEntity,
    val totalAmount: Int,
    val depositCount: Int
)

@Composable
fun TopContributorsLeaderboard(
    members: List<MemberEntity>,
    notifications: List<DepositNotificationEntity>,
    onMemberClick: (MemberEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val rankedContributors = remember(members, notifications) {
        val totalPerMember = mutableMapOf<Long, Int>()
        val countPerMember = mutableMapOf<Long, Int>()

        notifications.forEach { notif ->
            if (notif.memberId > 0 && notif.amount > 0) {
                totalPerMember[notif.memberId] = (totalPerMember[notif.memberId] ?: 0) + notif.amount
                countPerMember[notif.memberId] = (countPerMember[notif.memberId] ?: 0) + 1
            }
        }

        members.map { m ->
            val total = (totalPerMember[m.id] ?: 0) + if (m.status == "Paid") m.monthlyAmount else 0
            val count = (countPerMember[m.id] ?: 0) + if (m.status == "Paid") 1 else 0
            ContributorRank(
                rank = 0,
                member = m,
                totalAmount = total,
                depositCount = count
            )
        }.sortedByDescending { it.totalAmount }
            .mapIndexed { index, item -> item.copy(rank = index + 1) }
    }

    val maxAmount = rankedContributors.firstOrNull()?.totalAmount?.coerceAtLeast(1) ?: 1
    val displayList = if (isExpanded) rankedContributors else rankedContributors.take(3)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_contributors_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "सर्वोच्च योगदानकर्ता (Leaderboard)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "पारिवारिक फंड में संचित कुल सहयोग आधार पर",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (rankedContributors.size > 3) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "कम देखें" else "सभी (${rankedContributors.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Leaderboard Bar Items
            displayList.forEach { rankItem ->
                val progress = (rankItem.totalAmount.toFloat() / maxAmount).coerceIn(0.05f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    label = "leaderboard_bar_${rankItem.member.id}"
                )

                val (badgeText, badgeBg, badgeColor) = when (rankItem.rank) {
                    1 -> Triple("🥇 #1", Color(0xFFFEF3C7), Color(0xFFB45309))
                    2 -> Triple("🥈 #2", Color(0xFFF1F5F9), Color(0xFF475569))
                    3 -> Triple("🥉 #3", Color(0xFFFFEDD5), Color(0xFFC2410C))
                    else -> Triple("#${rankItem.rank}", Color(0xFFF8FAFC), Color(0xFF64748B))
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onMemberClick(rankItem.member) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = badgeBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = rankItem.member.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Text(
                                text = "₹${java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "IN")).format(rankItem.totalAmount)}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                color = PaidGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Visual contribution bar chart
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when (rankItem.rank) {
                                1 -> SaffronGold
                                2 -> Color(0xFF64748B)
                                3 -> Color(0xFFEA580C)
                                else -> Color(0xFF3B82F6)
                            },
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}
