package com.example.taskorium.ui.uiComponents

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.taskorium.ui.theme.TaskoriumTheme

@Composable
fun TaskoriumFilterChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (()-> Unit)? = null,
    onLongClick: (()-> Unit)? = null,
    isSelected: Boolean = false,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceDim,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary

){
    val chipTransition = updateTransition(targetState = isSelected)
    val backgroundColor by chipTransition.animateColor(transitionSpec = { tween(700) }) { selected ->
        if (selected) selectedContainerColor else containerColor
    }
    val textAndIconColor by chipTransition.animateColor(transitionSpec = { tween(700) }) { selected ->
        if (selected) selectedContentColor else contentColor
    }

    val horizontalSpacing: Dp by chipTransition.animateDp(transitionSpec = { tween(700) }) { selected ->
        if (selected) 16.dp else 10.dp
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .height(30.dp)
            .drawBehind{
                drawRect(
                    brush = SolidColor(backgroundColor)
                )
            }
            .combinedClickable(onClick != null,
                onLongClick = {onLongClick?.invoke()}
            ){onClick?.invoke()}
            .padding(vertical = 4.dp, horizontal = horizontalSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        BasicText(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = textAndIconColor
            )
        )
        icon?.let {
            Image(
                imageVector = icon,
                contentDescription = "$text chip icon",
                modifier = Modifier
//                    .padding(start = horizontalSpacing)
                    .size(20.dp),
                colorFilter = ColorFilter.tint(textAndIconColor)
            )
        }
    }

}


@Preview
@Composable
private fun ChipPreview(){
    TaskoriumTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskoriumFilterChip(
                text = "chip",
//                icon = Notification
            )
            TaskoriumFilterChip(
                text = "chip",
//                icon = Profile,
                isSelected = true
            )
            TaskoriumFilterChip(
                text = "No Icon",
                isSelected = true
            )
        }
    }
}