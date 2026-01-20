package id.yuana.driver.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Data class for popover button texts.
 */
internal data class PopoverButtonTexts(
    val next: String,
    val previous: String,
    val done: String
)

/**
 * Popover tooltip component that displays step information.
 *
 * @param config Popover configuration.
 * @param targetBounds Bounds of the target element.
 * @param screenSize Size of the screen for positioning.
 * @param side Position of the popover relative to target.
 * @param align Alignment of the popover.
 * @param showProgress Whether to show progress text.
 * @param currentStep Current step number (1-indexed).
 * @param totalSteps Total number of steps.
 * @param progressText Template for progress text.
 * @param buttons List of buttons to show.
 * @param disabledButtons List of buttons to disable.
 * @param buttonTexts Button text labels.
 * @param isLastStep Whether this is the last step.
 * @param animate Whether to animate the popover.
 * @param stagePadding Padding around the highlighted element.
 * @param onNext Callback when next button is clicked.
 * @param onPrevious Callback when previous button is clicked.
 * @param onClose Callback when close button is clicked.
 */
@Composable
internal fun DriverPopover(
    config: PopoverConfig,
    targetBounds: Rect?,
    screenSize: IntSize,
    side: PopoverSide,
    align: PopoverAlign,
    showProgress: Boolean,
    currentStep: Int,
    totalSteps: Int,
    progressText: String,
    buttons: List<DriverButton>,
    disabledButtons: List<DriverButton>,
    buttonTexts: PopoverButtonTexts,
    isLastStep: Boolean,
    animate: Boolean,
    stagePadding: Dp,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var popoverSize by remember { mutableStateOf(IntSize.Zero) }
    val stagePaddingPx = with(density) { stagePadding.toPx() }
    val popoverOffset = with(density) { 12.dp.toPx() }
    
    // Calculate popover position
    val (offsetX, offsetY) = remember(targetBounds, popoverSize, side, align, screenSize) {
        calculatePopoverPosition(
            targetBounds = targetBounds,
            popoverSize = popoverSize,
            screenSize = screenSize,
            side = side,
            align = align,
            stagePadding = stagePaddingPx,
            popoverOffset = popoverOffset
        )
    }
    
    // Animation state
    val visibleState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        visibleState.targetState = true
    }
    
    AnimatedVisibility(
        visibleState = visibleState,
        enter = if (animate) fadeIn() + scaleIn(
            initialScale = 0.9f,
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) else fadeIn(initialAlpha = 1f),
        exit = if (animate) fadeOut() + scaleOut(
            targetScale = 0.9f,
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) else fadeOut(targetAlpha = 0f),
        modifier = modifier.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
    ) {
        PopoverContent(
            config = config,
            showProgress = showProgress,
            currentStep = currentStep,
            totalSteps = totalSteps,
            progressText = progressText,
            buttons = buttons,
            disabledButtons = disabledButtons,
            buttonTexts = buttonTexts,
            isLastStep = isLastStep,
            onNext = onNext,
            onPrevious = onPrevious,
            onClose = onClose,
            onSizeChanged = { popoverSize = it }
        )
    }
}

/**
 * Calculate the popover position based on target bounds and configuration.
 */
private fun calculatePopoverPosition(
    targetBounds: Rect?,
    popoverSize: IntSize,
    screenSize: IntSize,
    side: PopoverSide,
    align: PopoverAlign,
    stagePadding: Float,
    popoverOffset: Float
): Pair<Float, Float> {
    if (targetBounds == null) {
        // Center the popover on screen if no target
        return Pair(
            (screenSize.width - popoverSize.width) / 2f,
            (screenSize.height - popoverSize.height) / 2f
        )
    }
    
    val padding = 16f // Screen edge padding
    
    var x: Float
    var y: Float
    
    when (side) {
        PopoverSide.TOP -> {
            y = targetBounds.top - stagePadding - popoverSize.height - popoverOffset
            x = when (align) {
                PopoverAlign.START -> targetBounds.left - stagePadding
                PopoverAlign.CENTER -> targetBounds.center.x - popoverSize.width / 2f
                PopoverAlign.END -> targetBounds.right + stagePadding - popoverSize.width
            }
        }
        PopoverSide.BOTTOM -> {
            y = targetBounds.bottom + stagePadding + popoverOffset
            x = when (align) {
                PopoverAlign.START -> targetBounds.left - stagePadding
                PopoverAlign.CENTER -> targetBounds.center.x - popoverSize.width / 2f
                PopoverAlign.END -> targetBounds.right + stagePadding - popoverSize.width
            }
        }
        PopoverSide.LEFT -> {
            x = targetBounds.left - stagePadding - popoverSize.width - popoverOffset
            y = when (align) {
                PopoverAlign.START -> targetBounds.top - stagePadding
                PopoverAlign.CENTER -> targetBounds.center.y - popoverSize.height / 2f
                PopoverAlign.END -> targetBounds.bottom + stagePadding - popoverSize.height
            }
        }
        PopoverSide.RIGHT -> {
            x = targetBounds.right + stagePadding + popoverOffset
            y = when (align) {
                PopoverAlign.START -> targetBounds.top - stagePadding
                PopoverAlign.CENTER -> targetBounds.center.y - popoverSize.height / 2f
                PopoverAlign.END -> targetBounds.bottom + stagePadding - popoverSize.height
            }
        }
    }
    
    // Clamp to screen bounds
    x = x.coerceIn(padding, (screenSize.width - popoverSize.width - padding).coerceAtLeast(padding))
    y = y.coerceIn(padding, (screenSize.height - popoverSize.height - padding).coerceAtLeast(padding))
    
    return Pair(x, y)
}

/**
 * The actual popover content with title, description, progress, and buttons.
 */
@Composable
private fun PopoverContent(
    config: PopoverConfig,
    showProgress: Boolean,
    currentStep: Int,
    totalSteps: Int,
    progressText: String,
    buttons: List<DriverButton>,
    disabledButtons: List<DriverButton>,
    buttonTexts: PopoverButtonTexts,
    isLastStep: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    onSizeChanged: (IntSize) -> Unit
) {
    Surface(
        modifier = Modifier
            .widthIn(min = 200.dp, max = 320.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .onGloballyPositioned { coordinates ->
                onSizeChanged(coordinates.size)
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        // Check for custom content
        if (config.customContent != null) {
            config.customContent.invoke()
        } else {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Close button row
                if (DriverButton.CLOSE in buttons) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onClose,
                            enabled = DriverButton.CLOSE !in disabledButtons,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                text = "✕",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Title
                config.title?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Description
                config.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Progress text
                if (showProgress && totalSteps > 1) {
                    val formattedProgress = progressText
                        .replace("{{current}}", currentStep.toString())
                        .replace("{{total}}", totalSteps.toString())
                    
                    Text(
                        text = formattedProgress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Navigation buttons
                val showNavButtons = buttons.any { it in listOf(DriverButton.NEXT, DriverButton.PREVIOUS) }
                if (showNavButtons) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        // Previous button
                        if (DriverButton.PREVIOUS in buttons) {
                            OutlinedButton(
                                onClick = onPrevious,
                                enabled = DriverButton.PREVIOUS !in disabledButtons
                            ) {
                                Text(buttonTexts.previous)
                            }
                        }
                        
                        // Next/Done button
                        if (DriverButton.NEXT in buttons) {
                            Button(
                                onClick = onNext,
                                enabled = DriverButton.NEXT !in disabledButtons
                            ) {
                                Text(if (isLastStep) buttonTexts.done else buttonTexts.next)
                            }
                        }
                    }
                }
            }
        }
    }
}
