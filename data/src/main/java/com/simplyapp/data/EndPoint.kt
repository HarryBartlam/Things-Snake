package com.simplyapp.data

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

data class EndPoint(val endpointId: String, val discoveredEndpointInfo: DiscoveredEndpointInfo)