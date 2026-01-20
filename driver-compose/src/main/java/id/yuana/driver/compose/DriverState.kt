package id.yuana.driver.compose

/**
 * Runtime state of the Driver.
 * Mirrors driver.js State type.
 *
 * @param isActive Whether the driver is currently active.
 * @param activeIndex Index of the currently active step (-1 if none).
 * @param activeStep The currently active step configuration.
 * @param previousStep The previously active step configuration.
 * @param steps List of all steps in the current tour.
 */
data class DriverState(
    val isActive: Boolean = false,
    val activeIndex: Int = -1,
    val activeStep: DriveStep? = null,
    val previousStep: DriveStep? = null,
    val steps: List<DriveStep> = emptyList()
) {
    /**
     * Whether the driver is currently initialized and running.
     * Alias for isActive to match driver.js naming.
     */
    val isInitialized: Boolean get() = isActive
    
    /**
     * Total number of steps in the tour.
     */
    val totalSteps: Int get() = steps.size
    
    /**
     * Current step number (1-indexed for display).
     */
    val currentStepNumber: Int get() = if (activeIndex >= 0) activeIndex + 1 else 0
}
