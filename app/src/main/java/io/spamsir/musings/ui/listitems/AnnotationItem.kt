package io.spamsir.musings.ui.listitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.spamsir.musings.DateConverter
import io.spamsir.musings.data.domain.Annotation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AnnotationItem(annotation: Annotation, onClick: () -> Unit) {

    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    val dateText: String

    val cal = Calendar.getInstance()
    cal.time = DateConverter.toDate(annotation.dateTime)

    val diff = Calendar.getInstance().timeInMillis - cal.timeInMillis
    dateText = if (diff < ONE_MINUTE) {
        (diff / ONE_SECOND).toString() + " second" + (if ((diff / ONE_SECOND).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_HOUR) {
        (diff / ONE_MINUTE).toString() + " minute" + (if ((diff / ONE_MINUTE).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_DAY) {
        (diff / ONE_HOUR).toString() + " hour" + (if ((diff / ONE_HOUR).toInt() != 1) "s" else "") + " ago"
    } else {
        formatter.format(DateConverter.toDate(annotation.dateTime))
    }

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            onClick()
        }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = dateText,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = annotation.content,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun AnnotationItemPreview() {
    val annotation = Annotation(content = "This Musing is fun!")
    MaterialTheme {
        AnnotationItem(annotation, {})
    }
}