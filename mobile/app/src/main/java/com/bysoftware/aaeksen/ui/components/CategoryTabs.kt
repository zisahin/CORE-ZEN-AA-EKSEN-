package com.bysoftware.aaeksen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bysoftware.aaeksen.data.CategoryTab
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme

@Composable
fun CategoryTabRow(
    categories: List<CategoryTab>,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            CategoryTabItem(
                category = category,
                onClick = { onCategorySelected(category.name) }
            )
        }
    }
}

@Composable
fun CategoryTabItem(
    category: CategoryTab,
    onClick: () -> Unit
) {
    val icon = when (category.name) {
        "Politic" -> Icons.Default.Language
        "Sport" -> Icons.Default.SportsSoccer
        "Education" -> Icons.Default.School
        "Gaming" -> Icons.Default.SportsEsports
        else -> null
    }
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (category.isSelected) Color(0xFF2563EB) else Color(0xFFF1F5F9)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (category.isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        
        Text(
            text = category.name,
            color = if (category.isSelected) Color.White else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (category.isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }

}

@Composable
@Preview
fun categoryTabItemRow() {
    AAEksenTheme {
        CategoryTabRow(
            categories = listOf(
                CategoryTab("Politic", isSelected = true),
                CategoryTab("Sport"),
                CategoryTab("Education"),
                CategoryTab("Gaming"),
                CategoryTab("Technology"),
            ),
            onCategorySelected = {}
        )
    }
}
