package com.example.mymapapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Random

@Composable
fun MyMap() {
    val cameraState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(47.0, 19.0), 10f)
    }

    val markersPosition = remember {
        listOf(LatLng(47.0, 19.0)).toMutableStateList()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isSatellite by remember {
        mutableStateOf(false)
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true
            )
        )
    }
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, com.example.mymapapplication.R.raw.mymapconfig
                )
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Switch(
            checked = isSatellite,
            onCheckedChange = {
                isSatellite = it
                mapProperties = mapProperties.copy(
                    mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL

                )
            }
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapClick = {
                markersPosition.add(it)

                val random = Random(System.currentTimeMillis())
                val cameraPosition = CameraPosition.Builder()
                    .target(it)
                    .zoom(1f + random.nextInt(5))
                    .tilt(30f + random.nextInt(15))
                    .bearing(-45f + random.nextInt(90))
                    .build()
                //cameraState.position = cameraPostion
                coroutineScope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newCameraPosition(cameraPosition), 3000
                    )
                }
            }

        ) {
            // Add items like Marker-s, Polylines, etc.
            for (marker in markersPosition) {
                Marker(
                    state = MarkerState(position = marker),
                    title = "My Marker",
                    snippet = "Marker description loc: ${marker.latitude}, ${marker.longitude}",
                    draggable = true,
                )

                Polyline(
                    points = listOf(
                        LatLng(47.0, 19.0),
                        LatLng(45.0, 18.0),
                        LatLng(49.0, 23.0),
                    ),
                    color = androidx.compose.ui.graphics.Color.Red,
                    width = 10f
                )
            }

        }
    }
}