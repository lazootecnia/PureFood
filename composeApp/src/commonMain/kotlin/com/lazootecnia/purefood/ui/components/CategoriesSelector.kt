package com.lazootecnia.purefood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoriesSelector(
    availableCategories: List<String>,
    selectedCategories: List<String>,
    onCategoriesChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var customCategory by remember { mutableStateOf("") }
    val allCategories = (availableCategories + selectedCategories).distinct().sorted()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Custom category input
        OutlinedTextField(
            value = customCategory,
            onValueChange = { customCategory = it },
            label = { Text("Agregar categoría personalizada") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (customCategory.isNotBlank()) {
                    Text(
                        "➕",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                val newCategory = customCategory.trim()
                                if (newCategory.isNotBlank() && !selectedCategories.contains(newCategory)) {
                                    onCategoriesChanged(selectedCategories + newCategory)
                                    customCategory = ""
                                }
                            }
                    )
                }
            }
        )

        // Categories grid/list with checkboxes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                )
                .heightIn(max = 200.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(allCategories) { category ->
                    val isSelected = selectedCategories.contains(category)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newCategories = if (isSelected) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                                onCategoriesChanged(newCategories)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val newCategories = if (isSelected) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                                onCategoriesChanged(newCategories)
                            }
                        )
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (allCategories.isEmpty()) {
                    item {
                        Text(
                            text = "Sin categorías disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Selected categories chips display
        if (selectedCategories.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Seleccionadas: ${selectedCategories.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (category in selectedCategories) {
                        CategoryChip(
                            category = category,
                            onRemove = {
                                onCategoriesChanged(selectedCategories - category)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = "✕",
            modifier = Modifier.clickable(onClick = onRemove),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
