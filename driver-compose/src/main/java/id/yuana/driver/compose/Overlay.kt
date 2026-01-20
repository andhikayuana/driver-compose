package id.yuana.driver.compose

import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Semi-transparent overlay with a cutout for the highlighted element.
 * Creates a spotlight effect by drawing the overlay and then cutting out
 * a rounded rectangle where the target element is.
 *
 * @param targetBounds Bounds of the target element to highlight (null for no cutout).
 * @param overlayColor Color of the overlay.
 * @param overlayOpacity Opacity of the overlay.
 * @param stagePadding Padding around the cutout.
 * @param stageRadius Corner radius of the cutout.
 * @param animate Whether to animate the cutout position/size.
 * @param onClick Callback when the overlay is clicked.
 */
@Composable
internal fun DriverOverlay(
    targetBounds: Rect?,
    overlayColor: Color,
    overlayOpacity: Float,
    stagePadding: Dp,
    stageRadius: Dp,
    animate: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val stagePaddingPx = with(density) { stagePadding.toPx() }
    val stageRadiusPx = with(density) { stageRadius.toPx() }
    
    // Animate the target bounds for smooth transitions
    val animatedBounds by animateRectAsState(
        targetValue = targetBounds ?: Rect.Zero,
        animationSpec = if (animate) tween(durationMillis = 300) else tween(durationMillis = 0),
        label = "overlay_bounds"
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        drawOverlayWithCutout(
            bounds = if (targetBounds != null) animatedBounds else null,
            overlayColor = overlayColor,
            overlayOpacity = overlayOpacity,
            stagePadding = stagePaddingPx,
            stageRadius = stageRadiusPx
        )
    }
}

/**
 * Draw the overlay with a cutout for the highlighted element.
 */
private fun DrawScope.drawOverlayWithCutout(
    bounds: Rect?,
    overlayColor: Color,
    overlayOpacity: Float,
    stagePadding: Float,
    stageRadius: Float
) {
    val overlayPath = Path().apply {
        // Full screen rectangle
        addRect(Rect(Offset.Zero, Size(size.width, size.height)))
    }
    
    if (bounds != null && bounds != Rect.Zero) {
        // Create the cutout path with padding and rounded corners
        val cutoutRect = Rect(
            left = bounds.left - stagePadding,
            top = bounds.top - stagePadding,
            right = bounds.right + stagePadding,
            bottom = bounds.bottom + stagePadding
        )
        
        val cutoutPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = cutoutRect,
                    cornerRadius = CornerRadius(stageRadius, stageRadius)
                )
            )
        }
        
        // Subtract the cutout from the overlay
        val resultPath = Path().apply {
            op(overlayPath, cutoutPath, PathOperation.Difference)
        }
        
        drawPath(
            path = resultPath,
            color = overlayColor.copy(alpha = overlayOpacity)
        )
    } else {
        // No cutout, just draw the full overlay
        drawPath(
            path = overlayPath,
            color = overlayColor.copy(alpha = overlayOpacity)
        )
    }
}
