package id.yuana.driver.compose

import androidx.compose.runtime.Composable

/**
 * Position of the popover relative to the target element.
 * Mirrors driver.js Popover.side.
 */
enum class PopoverSide {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT
}

/**
 * Alignment of the popover relative to the target element.
 * Mirrors driver.js Popover.align.
 */
enum class PopoverAlign {
    START,
    CENTER,
    END
}

/**
 * Configuration for a popover attached to a step.
 * Mirrors driver.js Popover type.
 *
 * @param title Title shown in the popover.
 * @param description Description shown in the popover.
 * @param side Position of the popover relative to target. (default: BOTTOM)
 * @param align Alignment of the popover. (default: CENTER)
 * @param showButtons Buttons to show, overrides global config.
 * @param disableButtons Buttons to disable, overrides global config.
 * @param showProgress Whether to show progress text, overrides global config.
 * @param progressText Progress text template, overrides global config.
 * @param nextBtnText Next button text, overrides global config.
 * @param prevBtnText Previous button text, overrides global config.
 * @param doneBtnText Done button text, overrides global config.
 * @param onNextClick Callback when next is clicked, overrides global.
 * @param onPrevClick Callback when previous is clicked, overrides global.
 * @param onCloseClick Callback when close is clicked, overrides global.
 * @param customContent Custom composable content to render instead of default.
 */
data class PopoverConfig(
    val title: String? = null,
    val description: String? = null,
    val side: PopoverSide = PopoverSide.BOTTOM,
    val align: PopoverAlign = PopoverAlign.CENTER,
    val showButtons: List<DriverButton>? = null,
    val disableButtons: List<DriverButton>? = null,
    val showProgress: Boolean? = null,
    val progressText: String? = null,
    val nextBtnText: String? = null,
    val prevBtnText: String? = null,
    val doneBtnText: String? = null,
    val onNextClick: (() -> Unit)? = null,
    val onPrevClick: (() -> Unit)? = null,
    val onCloseClick: (() -> Unit)? = null,
    val customContent: (@Composable () -> Unit)? = null
)
