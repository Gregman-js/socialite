package com.google.android.samples.socialite.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.samples.socialite.R
import com.google.android.samples.socialite.ui.AnimationConstants
import com.google.android.samples.socialite.ui.home.ChatList
import com.google.android.samples.socialite.ui.home.HomeAppBar
import com.google.android.samples.socialite.ui.home.HomeBackground
import com.google.android.samples.socialite.ui.home.HomeViewModel
import com.google.android.samples.socialite.ui.home.timeline.Timeline
import java.util.UUID
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@RequiresPermission(allOf = [
    Manifest.permission.BLUETOOTH_ADVERTISE,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT
])
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Add(
    modifier: Modifier = Modifier,
    onConnect: (BluetoothDevice) -> Unit = {}
) {
    val context = LocalContext.current
    val adapter = checkNotNull(context.getSystemService<BluetoothManager>()?.adapter)
    var scanning by remember {
        mutableStateOf(true)
    }
    val devices = remember {
        mutableStateListOf<BluetoothDevice>()
    }
    val pairedDevices = remember {
        // Get a list of previously paired devices
        mutableStateListOf<BluetoothDevice>(*adapter.bondedDevices.toTypedArray())
    }
    val sampleServerDevices = remember {
        mutableStateListOf<BluetoothDevice>()
    }
    val scanSettings: ScanSettings = ScanSettings.Builder()
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .build()

    // This effect will start scanning for devices when the screen is visible
    // If scanning is stop removing the effect will stop the scanning.
    if (scanning) {
        BluetoothScanEffect(
            scanSettings = scanSettings,
            onScanFailed = {
                scanning = false
                Log.w("FindBLEDevicesSample", "Scan failed with error: $it")
            },
            onDeviceFound = { scanResult ->
                if (!devices.contains(scanResult.device)) {
                    devices.add(scanResult.device)
                }

                // If we find our GATT server sample let's highlight it
                val serviceUuids = scanResult.scanRecord?.serviceUuids.orEmpty()
                if (serviceUuids.contains(ParcelUuid(UUID.fromString("00002222-0000-1000-8000-00805f9b34fb")))) {
                    if (!sampleServerDevices.contains(scanResult.device)) {
                        sampleServerDevices.add(scanResult.device)
                    }
                }
            },
        )
        // Stop scanning after a while
        LaunchedEffect(true) {
            delay(15000)
            scanning = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { HomeAppBar(title = stringResource(R.string.add)) },
    ) { innerPadding ->
        HomeBackground(modifier = Modifier.fillMaxSize())
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = innerPadding
        ) {
            if (devices.isEmpty()) {
                item {
                    Text(text = "No devices found")
                }
            }
            items(devices) { item ->
                BluetoothDeviceItem(
                    bluetoothDevice = item,
                    isSampleServer = sampleServerDevices.contains(item),
                    onConnect = onConnect,
                )
            }

            if (pairedDevices.isNotEmpty()) {
                item {
                    Text(text = "Saved devices", style = MaterialTheme.typography.titleSmall)
                }
                items(pairedDevices) {
                    BluetoothDeviceItem(
                        bluetoothDevice = it,
                        onConnect = onConnect,
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
internal fun BluetoothDeviceItem(
    bluetoothDevice: BluetoothDevice,
    isSampleServer: Boolean = false,
    onConnect: (BluetoothDevice) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onConnect(bluetoothDevice) },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            if (isSampleServer) {
                "GATT Sample server"
            } else {
                bluetoothDevice.name ?: "N/A"
            },
            style = if (isSampleServer) {
                TextStyle(fontWeight = FontWeight.Bold)
            } else {
                TextStyle(fontWeight = FontWeight.Normal)
            },
        )
        Text(bluetoothDevice.address)
        val state = when (bluetoothDevice.bondState) {
            BluetoothDevice.BOND_BONDED -> "Paired"
            BluetoothDevice.BOND_BONDING -> "Pairing"
            else -> "None"
        }
        Text(text = state)

    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
@Composable
private fun BluetoothScanEffect(
    scanSettings: ScanSettings,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onScanFailed: (Int) -> Unit,
    onDeviceFound: (device: ScanResult) -> Unit,
) {
    val context = LocalContext.current
    val adapter = context.getSystemService<BluetoothManager>()?.adapter

    if (adapter == null) {
        onScanFailed(ScanCallback.SCAN_FAILED_INTERNAL_ERROR)
        return
    }

    val currentOnDeviceFound by rememberUpdatedState(onDeviceFound)

    DisposableEffect(lifecycleOwner, scanSettings) {
        val leScanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                currentOnDeviceFound(result)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                onScanFailed(errorCode)
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            // Start scanning once the app is in foreground and stop when in background
            if (event == Lifecycle.Event.ON_START) {
                adapter.bluetoothLeScanner.startScan(null, scanSettings, leScanCallback)
            } else if (event == Lifecycle.Event.ON_STOP) {
                adapter.bluetoothLeScanner.stopScan(leScanCallback)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer and stop scanning
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adapter.bluetoothLeScanner.stopScan(leScanCallback)
        }
    }
}
