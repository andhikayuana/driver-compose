package id.yuana.driver.compose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.yuana.driver.compose.DriveStep
import id.yuana.driver.compose.Driver
import id.yuana.driver.compose.DriverConfig
import id.yuana.driver.compose.PopoverAlign
import id.yuana.driver.compose.PopoverConfig
import id.yuana.driver.compose.PopoverSide
import id.yuana.driver.compose.demo.ui.theme.DriverComposeTheme
import id.yuana.driver.compose.driverTarget
import id.yuana.driver.compose.rememberDriverController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriverComposeTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Create the driver controller with configuration
    val driverController = rememberDriverController(
        config = DriverConfig(
            animate = true,
            showProgress = true,
            overlayOpacity = 0.7f,
            stagePadding = 12.dp,
            stageRadius = 8.dp
        )
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Driver Compose Demo",
                        modifier = Modifier.driverTarget(driverController, "app-title")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.driverTarget(driverController, "menu-button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.driverTarget(driverController, "search-button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.driverTarget(driverController, "settings-button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.driverTarget(driverController, "bottom-nav")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.driverTarget(driverController, "fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .driverTarget(driverController, "welcome-card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Welcome to Driver Compose!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This library helps you create beautiful onboarding tours " +
                                "and highlight important UI elements in your Jetpack Compose apps.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feature cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .driverTarget(driverController, "feature-1")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tours", style = MaterialTheme.typography.titleMedium)
                        Text("Multi-step guides", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .driverTarget(driverController, "feature-2")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Highlights", style = MaterialTheme.typography.titleMedium)
                        Text("Focus elements", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start tour buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        // Start the full onboarding tour
                        driverController.drive(
                            listOf(
                                DriveStep(
                                    targetId = "app-title",
                                    popover = PopoverConfig(
                                        title = "Welcome! 👋",
                                        description = "This is Driver Compose - a port of driver.js for Jetpack Compose.",
                                        side = PopoverSide.BOTTOM,
                                        align = PopoverAlign.START
                                    )
                                ),
                                DriveStep(
                                    targetId = "menu-button",
                                    popover = PopoverConfig(
                                        title = "Navigation Menu",
                                        description = "Access the main navigation drawer from here.",
                                        side = PopoverSide.BOTTOM,
                                        align = PopoverAlign.START
                                    )
                                ),
                                DriveStep(
                                    targetId = "search-button",
                                    popover = PopoverConfig(
                                        title = "Search",
                                        description = "Find anything quickly with the search feature.",
                                        side = PopoverSide.BOTTOM,
                                        align = PopoverAlign.END
                                    )
                                ),
                                DriveStep(
                                    targetId = "welcome-card",
                                    popover = PopoverConfig(
                                        title = "Welcome Card",
                                        description = "Important information is displayed in cards like this one.",
                                        side = PopoverSide.BOTTOM
                                    )
                                ),
                                DriveStep(
                                    targetId = "feature-1",
                                    popover = PopoverConfig(
                                        title = "Product Tours",
                                        description = "Create multi-step walkthroughs to guide users through your app.",
                                        side = PopoverSide.TOP
                                    )
                                ),
                                DriveStep(
                                    targetId = "fab",
                                    popover = PopoverConfig(
                                        title = "Quick Actions",
                                        description = "The floating action button provides quick access to primary actions.",
                                        side = PopoverSide.LEFT
                                    )
                                ),
                                DriveStep(
                                    targetId = "bottom-nav",
                                    popover = PopoverConfig(
                                        title = "Navigation Bar",
                                        description = "Switch between main sections of the app using the bottom navigation.",
                                        side = PopoverSide.TOP
                                    )
                                )
                            )
                        )
                    },
                    modifier = Modifier.driverTarget(driverController, "start-tour-btn")
                ) {
                    Text("Start Full Tour")
                }

                Button(
                    onClick = {
                        // Highlight a single element
                        driverController.highlight(
                            DriveStep(
                                targetId = "fab",
                                popover = PopoverConfig(
                                    title = "Highlighted Element",
                                    description = "This is how you highlight a single element without a full tour.",
                                    side = PopoverSide.LEFT
                                )
                            )
                        )
                    }
                ) {
                    Text("Highlight FAB")
                }
            }
        }

    }

    // Driver overlay - must be at the end to appear on top
    Driver(controller = driverController)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DriverComposeTheme {
        MainScreen()
    }
}