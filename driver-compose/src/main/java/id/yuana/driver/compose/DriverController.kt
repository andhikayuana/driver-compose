package id.yuana.driver.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect

/**
 * Controller for the Driver onboarding/highlighting system.
 * This is the main API entry point, equivalent to `driver()` in driver.js.
 *
 * Usage:
 * ```
 * val controller = rememberDriverController()
 * 
 * // Start a tour
 * controller.drive(listOf(
 *     DriveStep(targetId = "step1", popover = PopoverConfig(title = "Welcome!"))
 * ))
 * 
 * // Or highlight a single element
 * controller.highlight(DriveStep(targetId = "element1"))
 * ```
 */
@Stable
class DriverController(
    initialConfig: DriverConfig = DriverConfig()
) {
    /**
     * Current configuration (internal).
     */
    private var _config by mutableStateOf(initialConfig)
    
    /**
     * Internal state of the driver.
     */
    internal var state by mutableStateOf(DriverState())
        private set
    
    /**
     * Map of target IDs to their bounds in the composition.
     */
    internal val targetBounds = mutableStateMapOf<String, Rect>()
    
    /**
     * Start a tour with the given steps.
     * Equivalent to driver.js `driverObj.drive()`.
     *
     * @param steps List of steps to show in the tour.
     * @param startIndex Index of the step to start from. (default: 0)
     */
    fun drive(steps: List<DriveStep>, startIndex: Int = 0) {
        if (steps.isEmpty()) return
        
        val clampedIndex = startIndex.coerceIn(0, steps.lastIndex)
        state = DriverState(
            isActive = true,
            activeIndex = clampedIndex,
            activeStep = steps[clampedIndex],
            previousStep = null,
            steps = steps
        )
        
        notifyHighlightStarted(steps[clampedIndex])
    }
    
    /**
     * Highlight a single element.
     * Equivalent to driver.js `driverObj.highlight()`.
     *
     * @param step The step configuration for the element to highlight.
     */
    fun highlight(step: DriveStep) {
        state = DriverState(
            isActive = true,
            activeIndex = 0,
            activeStep = step,
            previousStep = null,
            steps = listOf(step)
        )
        
        notifyHighlightStarted(step)
    }
    
    /**
     * Move to the next step in the tour.
     * Equivalent to driver.js `driverObj.moveNext()`.
     */
    fun moveNext() {
        if (!hasNextStep()) return
        
        val currentStep = state.activeStep
        val nextIndex = state.activeIndex + 1
        val nextStep = state.steps[nextIndex]
        
        currentStep?.let { notifyDeselected(it) }
        
        state = state.copy(
            activeIndex = nextIndex,
            activeStep = nextStep,
            previousStep = currentStep
        )
        
        notifyHighlightStarted(nextStep)
    }
    
    /**
     * Move to the previous step in the tour.
     * Equivalent to driver.js `driverObj.movePrevious()`.
     */
    fun movePrevious() {
        if (!hasPreviousStep()) return
        
        val currentStep = state.activeStep
        val prevIndex = state.activeIndex - 1
        val prevStep = state.steps[prevIndex]
        
        currentStep?.let { notifyDeselected(it) }
        
        state = state.copy(
            activeIndex = prevIndex,
            activeStep = prevStep,
            previousStep = currentStep
        )
        
        notifyHighlightStarted(prevStep)
    }
    
    /**
     * Move to a specific step by index.
     * Equivalent to driver.js `driverObj.moveTo()`.
     *
     * @param index The index of the step to move to.
     */
    fun moveTo(index: Int) {
        if (index < 0 || index >= state.steps.size) return
        if (index == state.activeIndex) return
        
        val currentStep = state.activeStep
        val targetStep = state.steps[index]
        
        currentStep?.let { notifyDeselected(it) }
        
        state = state.copy(
            activeIndex = index,
            activeStep = targetStep,
            previousStep = currentStep
        )
        
        notifyHighlightStarted(targetStep)
    }
    
    /**
     * Check if there is a next step available.
     */
    fun hasNextStep(): Boolean {
        return state.isActive && state.activeIndex < state.steps.lastIndex
    }
    
    /**
     * Check if there is a previous step available.
     */
    fun hasPreviousStep(): Boolean {
        return state.isActive && state.activeIndex > 0
    }
    
    /**
     * Check if the driver is currently active.
     */
    fun isActive(): Boolean = state.isActive
    
    /**
     * Check if the current step is the first step.
     */
    fun isFirstStep(): Boolean = state.activeIndex == 0
    
    /**
     * Check if the current step is the last step.
     */
    fun isLastStep(): Boolean = state.activeIndex == state.steps.lastIndex
    
    /**
     * Destroy the driver and reset state.
     * Equivalent to driver.js `driverObj.destroy()`.
     */
    fun destroy() {
        if (!state.isActive) return
        
        _config.onDestroyStarted?.invoke()
        
        val currentStep = state.activeStep
        currentStep?.let { notifyDeselected(it) }
        
        state = DriverState()
        
        _config.onDestroyed?.invoke()
    }
    
    /**
     * Refresh the driver state (e.g., after layout changes).
     * In Compose, this is mostly handled automatically via recomposition.
     */
    fun refresh() {
        // Force recomposition by updating state
        state = state.copy()
    }
    
    /**
     * Get the current state of the driver.
     * Equivalent to driver.js `driverObj.getState()`.
     */
    fun getState(): DriverState = state
    
    /**
     * Get the current configuration.
     * Equivalent to driver.js `driverObj.getConfig()`.
     */
    fun getConfig(): DriverConfig = _config
    
    /**
     * Update the configuration.
     * Equivalent to driver.js `driverObj.setConfig()`.
     *
     * @param newConfig The new configuration to apply.
     */
    fun setConfig(newConfig: DriverConfig) {
        _config = newConfig
    }
    
    /**
     * Get the active element's target ID.
     */
    fun getActiveElement(): String? = state.activeStep?.targetId
    
    /**
     * Get the previous element's target ID.
     */
    fun getPreviousElement(): String? = state.previousStep?.targetId
    
    /**
     * Get the active step.
     */
    fun getActiveStep(): DriveStep? = state.activeStep
    
    /**
     * Get the previous step.
     */
    fun getPreviousStep(): DriveStep? = state.previousStep
    
    /**
     * Get the active step index.
     */
    fun getActiveIndex(): Int = state.activeIndex
    
    // Internal callback helpers
    
    private fun notifyHighlightStarted(step: DriveStep) {
        step.onHighlightStarted?.invoke()
        _config.onHighlightStarted?.invoke(step)
    }
    
    internal fun notifyHighlighted(step: DriveStep) {
        step.onHighlighted?.invoke()
        _config.onHighlighted?.invoke(step)
    }
    
    private fun notifyDeselected(step: DriveStep) {
        step.onDeselected?.invoke()
        _config.onDeselected?.invoke(step)
    }
    
    internal fun handleNextClick() {
        val step = state.activeStep ?: return
        
        // Check for step-level override first
        step.popover?.onNextClick?.invoke()
        
        // Then global config
        _config.onNextClick?.invoke(step)
        
        // Default behavior if no override prevents it
        if (step.popover?.onNextClick == null && _config.onNextClick == null) {
            if (hasNextStep()) {
                moveNext()
            } else {
                destroy()
            }
        }
    }
    
    internal fun handlePrevClick() {
        val step = state.activeStep ?: return
        
        // Check for step-level override first
        step.popover?.onPrevClick?.invoke()
        
        // Then global config
        _config.onPrevClick?.invoke(step)
        
        // Default behavior if no override prevents it
        if (step.popover?.onPrevClick == null && _config.onPrevClick == null) {
            movePrevious()
        }
    }
    
    internal fun handleCloseClick() {
        val step = state.activeStep ?: return
        
        // Check for step-level override first
        step.popover?.onCloseClick?.invoke()
        
        // Then global config
        _config.onCloseClick?.invoke(step)
        
        // Default behavior if no override prevents it
        if (step.popover?.onCloseClick == null && _config.onCloseClick == null) {
            destroy()
        }
    }
    
    internal fun handleOverlayClick() {
        if (!_config.allowClose) return
        
        when (val behavior = _config.overlayClickBehavior) {
            is OverlayClickBehavior.Close -> destroy()
            is OverlayClickBehavior.NextStep -> {
                if (hasNextStep()) moveNext() else destroy()
            }
            is OverlayClickBehavior.Custom -> behavior.action(state.activeStep)
        }
    }
}

/**
 * Remember a DriverController instance.
 * Equivalent to creating `const driverObj = driver(config)` in driver.js.
 *
 * @param config Initial configuration for the driver.
 * @return A remembered DriverController instance.
 */
@Composable
fun rememberDriverController(
    config: DriverConfig = DriverConfig()
): DriverController {
    return remember { DriverController(config) }
}
