package id.yuana.driver.compose

/**
 * Configuration for a single step in a tour.
 * Mirrors driver.js DriveStep type.
 *
 * @param targetId The ID of the target element to highlight.
 *                 This should match the ID passed to Modifier.driverTarget(controller, id).
 *                 If null, only the popover is shown without highlighting.
 * @param popover Configuration for the popover. If null, only the highlight is shown.
 * @param disableActiveInteraction Whether to disable interaction with the element for this step.
 *                                  Overrides global config.
 * @param onHighlightStarted Callback when this step is about to be highlighted.
 * @param onHighlighted Callback when this step is highlighted.
 * @param onDeselected Callback when this step is deselected.
 */
data class DriveStep(
    val targetId: String? = null,
    val popover: PopoverConfig? = null,
    val disableActiveInteraction: Boolean? = null,
    val onHighlightStarted: (() -> Unit)? = null,
    val onHighlighted: (() -> Unit)? = null,
    val onDeselected: (() -> Unit)? = null
)
