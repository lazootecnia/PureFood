package com.lazootecnia.purefood.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationDialog(
    onDismiss: () -> Unit,
    onAuthenticate: (String) -> Unit,
    error: String? = null
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            password = ""
            onDismiss()
        },
        title = {
            Text(
                text = "🔒 Autenticación de Administrador",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ingresa la contraseña para acceder al panel de administración.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (password.isNotEmpty()) {
                                onAuthenticate(password)
                                password = ""
                            }
                        }
                    ),
                    isError = error != null,
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAuthenticate(password)
                    password = ""
                },
                enabled = password.isNotEmpty()
            ) {
                Text("Ingresar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    password = ""
                    onDismiss()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}
