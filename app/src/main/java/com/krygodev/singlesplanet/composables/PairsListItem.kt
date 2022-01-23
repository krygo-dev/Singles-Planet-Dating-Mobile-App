package com.krygodev.singlesplanet.composables

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Screen

@Composable
fun PairsListItem(
    user: User,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(280.dp)
                .clickable {
                    navController.navigate(Screen.DetailsScreen.route + "/${user.uid}")
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = user.photoURL,
                        builder = {
                            transformations(CircleCropTransformation())
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "${user.name}",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
        val context = LocalContext.current
        IconButton(
            onClick = {
                val launchIntent: Intent? =
                    context.packageManager.getLaunchIntentForPackage("com.example.singlesplanetchat")
                launchIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Chat,
                contentDescription = null,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}