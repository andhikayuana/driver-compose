package id.yuana.driver.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

/**
 * Main Driver composable that renders the overlay and popover.
 * This should be placed at the root of your composable tree, typically
 * inside your Scaffold or as the last child in a Box.
 *
 * Usage:
 * ```
 * val controller = rememberDriverController()
 * 
 * Box {
 *     // Your app content
 *     Text(
 *         text = "Hello",
 *         modifier = Modifier.driverTarget(controller, "greeting")
 *     )
 *     
 *     // Place Driver at the end to overlay on top
 *     Driver(controller = controller)
 * }
 * ```
 *
 * @param controller The DriverController instance.
 * @param modifier Modifier for the Driver container.
 */
@Composable
fun Driver(
    controller: DriverController,
    modifier: Modifier = Modifier
) {
    val state = controller.state
    val config = controller.getConfig()
    
    // Only render if active
    if (!state.isActive) return
    
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Get the current target bounds
    val targetBounds: Rect? = remember(state.activeStep?.targetId, controller.targetBounds) {
        state.activeStep?.targetId?.let { controller.targetBounds[it] }
    }
    
    // Notify that the element is now highlighted
    LaunchedEffect(state.activeStep) {
        state.activeStep?.let { step ->
            controller.notifyHighlighted(step)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenSize = coordinates.size
            }
    ) {
        // Overlay with cutout
        DriverOverlay(
            targetBounds = targetBounds,
            overlayColor = config.overlayColor,
            overlayOpacity = config.overlayOpacity,
            stagePadding = config.stagePadding,
            stageRadius = config.stageRadius,
            animate = config.animate,
            onClick = { controller.handleOverlayClick() }
        )
        
        // Popover
        val popover = state.activeStep?.popover
        if (popover != null || state.activeStep?.targetId == null) {
            val effectivePopover = popover ?: PopoverConfig()
            
            // Merge step-level config with global config
            val showButtons = effectivePopover.showButtons ?: config.showButtons
            val disableButtons = effectivePopover.disableButtons ?: config.disableButtons
            val showProgress = effectivePopover.showProgress ?: config.showProgress
            val progressText = effectivePopover.progressText ?: config.progressText
            val nextBtnText = effectivePopover.nextBtnText ?: config.nextBtnText
            val prevBtnText = effectivePopover.prevBtnText ?: config.prevBtnText
            val doneBtnText = effectivePopover.doneBtnText ?: config.doneBtnText
            
            DriverPopover(
                config = effectivePopover,
                targetBounds = targetBounds,
                screenSize = screenSize,
                side = effectivePopover.side,
                align = effectivePopover.align,
                showProgress = showProgress,
                currentStep = state.currentStepNumber,
                totalSteps = state.totalSteps,
                progressText = progressText,
                buttons = showButtons,
                disabledButtons = disableButtons,
                buttonTexts = PopoverButtonTexts(
                    next = nextBtnText,
                    previous = prevBtnText,
                    done = doneBtnText
                ),
                isLastStep = controller.isLastStep(),
                animate = config.animate,
                stagePadding = config.stagePadding,
                onNext = { controller.handleNextClick() },
                onPrevious = { controller.handlePrevClick() },
                onClose = { controller.handleCloseClick() }
            )
        }
    }
}

/**
 * Convenience wrapper to provide Driver within a content scope.
 * Use this when you want to easily add Driver to your existing layout.
 *
 * Usage:
 * ```
 * val controller = rememberDriverController()
 * 
 * DriverScope(controller = controller) {
 *     // Your content here
 *     Text(
 *         text = "Hello",
 *         modifier = Modifier.driverTarget(controller, "greeting")
 *     )
 * }
 * ```
 *
 * @param controller The DriverController instance.
 * @param modifier Modifier for the container.
 * @param content Your app content.
 */
@Composable
fun DriverScope(
    controller: DriverController,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        Driver(controller = controller)
    }
}
