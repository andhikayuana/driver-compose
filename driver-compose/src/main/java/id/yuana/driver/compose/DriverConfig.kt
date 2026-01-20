package id.yuana.driver.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Button types that can be shown in the popover.
 * Mirrors driver.js AllowedButtons.
 */
enum class DriverButton {
    NEXT,
    PREVIOUS,
    CLOSE
}

/**
 * Behavior when the overlay backdrop is clicked.
 * Mirrors driver.js overlayClickBehavior.
 */
sealed class OverlayClickBehavior {
    data object Close : OverlayClickBehavior()
    data object NextStep : OverlayClickBehavior()
    data class Custom(val action: (DriveStep?) -> Unit) : OverlayClickBehavior()
}

/**
 * Global configuration for the Driver.
 * Mirrors driver.js Config type.
 *
 * @param animate Whether to animate transitions. (default: true)
 * @param overlayColor Color of the overlay backdrop. (default: Black)
 * @param overlayOpacity Opacity of the overlay. (default: 0.5f)
 * @param stagePadding Distance between the highlighted element and the cutout. (default: 10.dp)
 * @param stageRadius Radius of the cutout around the highlighted element. (default: 5.dp)
 * @param allowClose Whether to allow closing by clicking on the backdrop. (default: true)
 * @param overlayClickBehavior What to do when the overlay is clicked. (default: Close)
 * @param allowKeyboardControl Whether to allow keyboard navigation. (default: true) - Not applicable in Compose
 * @param disableActiveInteraction Whether to disable interaction with highlighted element. (default: false)
 * @param showButtons Buttons to show in the popover. (default: [NEXT, PREVIOUS, CLOSE])
 * @param disableButtons Buttons to disable. (default: empty)
 * @param showProgress Whether to show progress text. (default: false)
 * @param progressText Template for progress text. Use {{current}} and {{total}}. (default: "{{current}} of {{total}}")
 * @param nextBtnText Text for the next button. (default: "Next")
 * @param prevBtnText Text for the previous button. (default: "Previous")
 * @param doneBtnText Text for the done button on last step. (default: "Done")
 */
data class DriverConfig(
    val animate: Boolean = true,
    val overlayColor: Color = Color.Black,
    val overlayOpacity: Float = 0.5f,
    val stagePadding: Dp = 10.dp,
    val stageRadius: Dp = 5.dp,
    val allowClose: Boolean = true,
    val overlayClickBehavior: OverlayClickBehavior = OverlayClickBehavior.Close,
    val allowKeyboardControl: Boolean = true,
    val disableActiveInteraction: Boolean = false,
    val showButtons: List<DriverButton> = listOf(DriverButton.NEXT, DriverButton.PREVIOUS, DriverButton.CLOSE),
    val disableButtons: List<DriverButton> = emptyList(),
    val showProgress: Boolean = false,
    val progressText: String = "{{current}} of {{total}}",
    val nextBtnText: String = "Next",
    val prevBtnText: String = "Previous",
    val doneBtnText: String = "Done",
    // Callbacks
    val onHighlightStarted: ((DriveStep) -> Unit)? = null,
    val onHighlighted: ((DriveStep) -> Unit)? = null,
    val onDeselected: ((DriveStep) -> Unit)? = null,
    val onDestroyStarted: (() -> Unit)? = null,
    val onDestroyed: (() -> Unit)? = null,
    val onNextClick: ((DriveStep) -> Unit)? = null,
    val onPrevClick: ((DriveStep) -> Unit)? = null,
    val onCloseClick: ((DriveStep) -> Unit)? = null
)
