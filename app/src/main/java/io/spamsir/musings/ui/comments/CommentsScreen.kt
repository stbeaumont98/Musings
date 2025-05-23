package io.spamsir.musings.ui.comments

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.spamsir.musings.ui.listitems.CommentItem
import io.spamsir.musings.R
import io.spamsir.musings.data.domain.Comment
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(state: CommentsState, onEvent: (CommentEvent) -> Unit, navEvent: (String) -> Unit) {

    val focusRequester = remember { FocusRequester() }

    val isEditing = remember { mutableStateOf(false) }
    val isNew = remember { mutableStateOf(false) }

    val commentId = remember { mutableLongStateOf(-1L) }

    val content = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = state.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navEvent("main")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Return to main screen"
                            )
                        }
                    },
                    actions = {
                        IconToggleButton(
                            checked = state.isLiked,
                            onCheckedChange = {
                                onEvent(CommentEvent.UpdateNote(state.noteId, !state.isLiked))
                            }
                        ) {
                            Icon(
                                if (state.isLiked) painterResource(id = R.drawable.ic_btn_star) else painterResource(
                                    id = R.drawable.ic_btn_star_empty
                                ), "Favorite"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (!isEditing.value) {
                    FloatingActionButton(
                        onClick = {
                            isEditing.value = true
                            isNew.value = true
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Add a new comment")
                    }
                }
            },
            bottomBar = {
                if (isEditing.value) {
                    BottomAppBar(
                        actions = {
                            if (isEditing.value) {
                                IconButton(onClick = {
                                    if (content.value != "") {
                                        Log.d("AM I GETTING HERE", "")
                                        val comment = Comment(
                                            Calendar.getInstance().timeInMillis,
                                            content.value,
                                            state.noteId
                                        )
                                        if (isNew.value) {
                                            Log.d("NEW COMMENT", comment.toString())
                                            onEvent(CommentEvent.NewComment(comment))
                                        } else {
                                            Log.d("EDIT COMMENT", comment.toString())
                                            comment.commentId = commentId.longValue
                                            onEvent(CommentEvent.UpdateComment(comment))
                                            commentId.longValue = -1L
                                        }
                                    }
                                    isEditing.value = false
                                    isNew.value = false
                                    content.value = ""
                                }) {
                                    Icon(Icons.Filled.Done, "Save comment")
                                }
                                IconButton(onClick = {
                                    isEditing.value = false
                                    content.value = ""
                                    if (!isNew.value) {
                                        onEvent(CommentEvent.RemoveComment(commentId.longValue))
                                    }
                                    commentId.longValue = -1L
                                }) {
                                    Icon(Icons.Filled.Delete, "Delete comment")
                                }
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    isEditing.value = false
                                    isNew.value = false
                                    content.value = ""
                                    commentId.longValue = -1L
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Close, "Cancel")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    text = state.content,
                    modifier = Modifier
                        .padding(16.dp)
                )
                LazyColumn {
                    items(state.comments) { comment ->
                        if (comment.commentId != commentId.longValue) {
                            CommentItem(comment) {
                                isEditing.value = true
                                content.value = comment.content
                                commentId.longValue = comment.commentId
                            }
                        }
                    }
                    if (isEditing.value) {
                        item {
                            OutlinedCard(modifier = Modifier
                                .padding(8.dp)
                            ) {
//                                var textFieldValueState by remember {
//                                    mutableStateOf(
//                                        TextFieldValue(
//                                            text = content.value,
//                                            selection = TextRange(content.value.length)
//                                        )
//                                    )
//                                }
                                TextField(
                                    value = content.value,
                                    onValueChange = { content.value = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .focusRequester(focusRequester),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent
                                    ),
                                )
                            }
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CommentScreenPreview() {
    MaterialTheme {
        CommentScreen(
            CommentsState(
            title = "Title",
            content = "Content",
            isLiked = true,
            comments = listOf(
                Comment(
                    content = "This is an comment!"
                )
            )
        ), {}) {}
    }
}