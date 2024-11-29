package com.starkindustries.expensetracker.presentation.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.starkindustries.expensetracker.R

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    shouldShow: Boolean
) {
    if (shouldShow) {
        val items = listOf(
            NavigationItem("Dashboard", R.drawable.baseline_home_24, 0),
            NavigationItem("Settings", R.drawable.baseline_settings_24, 1),
        )

        Column(modifier = modifier.fillMaxWidth()) {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                items.forEach { item ->
                    val isSelected = selectedItem == item.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onItemSelected(item.index)
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isSelected) Color(0xFFFFFFFF) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(30.dp),
                                    colorFilter = if (!isSelected) ColorFilter.tint(Color.Gray) else null
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color(0xFFFFFFFF)
                        ),
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

data class NavigationItem(val label: String, val iconRes: Int, val index: Int)
