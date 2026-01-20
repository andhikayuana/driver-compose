package id.yuana.driver.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Modifier to mark a composable as a target for the Driver.
 * The element's bounds will be tracked and can be highlighted using the given ID.
 *
 * Usage:
 * ```
 * Text(
 *     text = "Hello",
 *     modifier = Modifier.driverTarget(controller, "greeting")
 * )
 * 
 * // Later, highlight this element:
 * controller.highlight(DriveStep(targetId = "greeting"))
 * ```
 *
 * @param controller The DriverController instance.
 * @param id Unique identifier for this target element.
 * @return Modified Modifier that tracks the element's position.
 */
fun Modifier.driverTarget(
    controller: DriverController,
    id: String
): Modifier = this.onGloballyPositioned { coordinates ->
    val bounds = coordinates.boundsInRoot()
    // Only update if bounds have changed to avoid unnecessary recompositions
    if (controller.targetBounds[id] != bounds) {
        controller.targetBounds[id] = bounds
    }
}
