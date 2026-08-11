package com.example.taskorium.ui.composable

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.taskorium.ui.uiComponents.DirectionSwipe
import com.example.taskorium.ui.uiComponents.DragAnchors
import kotlin.math.roundToInt

@SuppressLint("FrequentlyChangingValue")
@Composable
fun SwipeableActionsBox(
    targetDp: Dp,
    directionSwipe: DirectionSwipe = DirectionSwipe.Left,
    backgroundContent: @Composable (Modifier)-> Unit,
    onClickBackgroundContent: ()-> Unit,
    backgroundContentAlignment: Alignment = Alignment.CenterEnd,
    foregroundContent: @Composable (Modifier)-> Unit,

    ) {
    val density = LocalDensity.current
    val targetPx = with(density) { targetDp.toPx() }
    val decaySpec = rememberSplineBasedDecay<Float>()
    val targetPxDirection = if (directionSwipe == DirectionSwipe.Left)targetPx else -targetPx
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Settled,
            anchors = DraggableAnchors {
                DragAnchors.Settled at 0f
                DragAnchors.Revealed at -targetPxDirection
            },
            positionalThreshold = { distance -> distance * 0.1f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            decayAnimationSpec = decaySpec
        )
    }
    val isRevealed = if(draggableState.currentValue == DragAnchors.Revealed) 1f else 0f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Box(
            modifier = Modifier
                .width(targetDp + 15.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .zIndex(isRevealed)
                .align(Alignment.CenterEnd),
            contentAlignment = backgroundContentAlignment
        ) {
                backgroundContent(
                    Modifier
                        .padding(end = 6.dp)
                        .size(28.dp)
                        .graphicsLayer{
                            val progress = if (targetPx != 0f) {
                                (draggableState.requireOffset() / -targetPx).coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            alpha = progress
                            scaleY = progress
                            scaleX = progress

                        }
                        .clickable {
                            onClickBackgroundContent()
                        }
                )

        }
        foregroundContent(Modifier.swipeToRevealRow(draggableState,Orientation.Horizontal))
    }
}
private fun Modifier.swipeToRevealRow(
    draggableState: AnchoredDraggableState<DragAnchors>,
    orientation: Orientation
): Modifier{
    return this
        .anchoredDraggable(
            state = draggableState,
            orientation = orientation
        )
        .offset {
            IntOffset(
                x = draggableState.requireOffset().roundToInt(),
                y = 0
            )
        }
}
