package com.lazootecnia.purefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lazootecnia.purefood.data.models.Recipe
import com.lazootecnia.purefood.ui.components.CategoriesSelector

@Composable
fun RecipeEditorForm(
    recipe: Recipe,
    availableCategories: List<String> = emptyList(),
    onSave: (Recipe) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(recipe.title) }
    var selectedCategories by remember { mutableStateOf(recipe.categories) }
    var imageUrl by remember { mutableStateOf(recipe.imageUrl) }
    var ingredientsText by remember { mutableStateOf(recipe.ingredients.joinToString("\n")) }
    var stepsText by remember { mutableStateOf(recipe.steps.joinToString("\n")) }
    var notesText by remember { mutableStateOf(recipe.notes.joinToString("\n")) }

    val isValid = title.isNotBlank() &&
            ingredientsText.isNotBlank() &&
            stepsText.isNotBlank()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (recipe.id == 0) "Nueva Receta" else "Editar Receta",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank()
            )
        }

        item {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            CategoriesSelector(
                availableCategories = availableCategories,
                selectedCategories = selectedCategories,
                onCategoriesChanged = { selectedCategories = it }
            )
        }

        item {
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de Imagen") },
                placeholder = { Text("assets/images/001.png") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Text(
                text = "Ingredientes *",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = ingredientsText,
                onValueChange = { ingredientsText = it },
                placeholder = { Text("Un ingrediente por línea...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 10,
                isError = ingredientsText.isBlank()
            )
        }

        item {
            Text(
                text = "Pasos de Preparación *",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = stepsText,
                onValueChange = { stepsText = it },
                placeholder = { Text("Un paso por línea...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 10,
                isError = stepsText.isBlank()
            )
        }

        item {
            Text(
                text = "Notas (opcional)",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                placeholder = { Text("Consejos, tips, variaciones...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                maxLines = 5
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val updatedRecipe = recipe.copy(
                            title = title.trim(),
                            categories = selectedCategories,
                            imageUrl = imageUrl.trim(),
                            ingredients = ingredientsText
                                .split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() },
                            steps = stepsText
                                .split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() },
                            notes = notesText
                                .split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                        )
                        onSave(updatedRecipe)
                    },
                    enabled = isValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("💾 Guardar")
                }
            }
        }

        item {
            Text(
                text = "* Campos obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
