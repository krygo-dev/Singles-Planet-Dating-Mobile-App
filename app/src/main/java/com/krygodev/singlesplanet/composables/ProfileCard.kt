package com.krygodev.singlesplanet.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.krygodev.singlesplanet.model.User
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ProfileCard(
    user: User
) {
    var userAge: Int? = null
    user.birthDate?.let {
        userAge =
            Calendar.getInstance()
                .get(Calendar.YEAR) - user.birthDate
                .substring(user.birthDate.lastIndexOf("-") + 1).toInt()
    }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
        shape = RoundedCornerShape(30.0f),
        elevation = 5.dp
    ) {
        Box(
            modifier = Modifier.height(600.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = rememberImagePainter(
                    data = user.photoURL,
                    builder = {
                        transformations(RoundedCornersTransformation(30.0f))
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box {
                Text(
                    text = "${user.name}, $userAge",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}