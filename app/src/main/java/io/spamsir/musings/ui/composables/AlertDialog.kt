package io.spamsir.musings.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AlertDialog(title: String, message: String, positiveButton: String, onPositiveButton: () -> Unit, negativeButton: String = "", onNegativeButton: () -> Unit = {}, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier
                .padding(24.dp)
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = message,
                    textAlign = TextAlign.Left
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    if (negativeButton != "") {
                        TextButton(
                            onClick = { onNegativeButton() }
                        ) {
                            Text(
                                text = negativeButton
                            )
                        }
                    }
                    TextButton(
                        onClick = { onPositiveButton() }
                    ) {
                        Text(
                            text = positiveButton
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun AlertDialogPreview() {
    MaterialTheme {
        AlertDialog(
            title = "Alert!",
            message = "You have been alerted!",
            positiveButton = "Yes",
            onPositiveButton = {  },
            negativeButton = "No",
            onNegativeButton = {  }) {
        }
    }
}