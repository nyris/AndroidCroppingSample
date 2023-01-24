package io.nyris.ui

internal class NyrisSearcherConfig(
    val isDebug: Boolean,
    val apiKey: String,
    val host: String?,
    val outputFormat: String,
    val language: String,
    val limit: Int,
    val dialogErrorTitle: String,
    val positiveButtonText: String,
    val captureLabelText: String,
    val noOfferFoundErrorText: String,
    val cameraPermissionDeniedErrorMessage: String,
)