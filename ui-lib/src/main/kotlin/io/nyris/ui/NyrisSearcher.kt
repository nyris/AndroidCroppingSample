package io.nyris.ui

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import io.nyris.ui.screens.NyrisSearcherActivity


class NyrisSearcher {
    companion object {
        const val CONFIG_KEY = "CONFIG_KEY"
        const val SEARCH_RESULT_KEY = "SEARCH_RESULT_KEY"
    }

    private val gson = Gson()

    private val isDebug: Boolean
    private val apiKey: String
    private var fromActivity: Activity? = null
    private var fromFragment: Fragment? = null

    private var host: String? = null
    private var outputFormat: String = Constants.defaultResultFormat
    private var language: String = Constants.defaultLanguage
    private var limit: Int = Constants.defaultLimit
    private var dialogErrorTitle: String = Constants.defaultDialogErrorTitle
    private var positiveButtonText: String = Constants.defaultPositiveButtonText
    private var captureLabelText: String = Constants.defaultCaptureLabelText
    private var noOfferFoundErrorText: String = Constants.defaultNoOfferFoundErrorText
    private var cameraPermissionDeniedErrorMessage: String = Constants.defaultCameraPermissionDeniedErrorMessage

    private constructor(apiKey: String, isDebug: Boolean) {
        this.apiKey = apiKey
        this.isDebug = isDebug
    }

    constructor(fragment: Fragment, apiKey: String, isDebug: Boolean = false) : this(apiKey, isDebug) {
        fromFragment = fragment
    }

    constructor(activity: Activity, apiKey: String, isDebug: Boolean = false) : this(apiKey, isDebug) {
        this.fromActivity = activity
    }

    fun host(value: String) = apply { this.host = value }
    fun outputFormat(value: String) = apply { this.outputFormat = value }

    fun language(value: String) = apply { this.language = value }

    fun limit(value: Int) = apply { this.limit = value }

    fun dialogErrorTitle(value: String) = apply { this.dialogErrorTitle = value }

    fun positiveButtonText(value: String) = apply { this.positiveButtonText = value }

    fun captureLabelText(value: String) = apply { this.captureLabelText = value }

    fun noOfferFoundErrorText(value: String) = apply { this.noOfferFoundErrorText = value }

    fun cameraPermissionDeniedErrorMessage(value: String) = apply { this.cameraPermissionDeniedErrorMessage = value }

    fun start() {
        when {
            fromActivity != null -> {
                val intent = Intent(fromActivity, NyrisSearcherActivity::class.java).attachConfig()
                fromActivity?.startActivity(intent)
            }
            fromFragment != null -> {
                val intent = Intent(fromFragment?.context, NyrisSearcherActivity::class.java).attachConfig()
                fromFragment?.startActivity(intent)
            }
            else -> {
                throw IllegalArgumentException("You need to handle the casting case here!")
            }
        }
    }

    private fun Intent.attachConfig() = apply {
        val config = creationConfig()
        val jsonConfig = gson.toJson(config)
        putExtra(CONFIG_KEY, jsonConfig)
    }

    private fun creationConfig() = NyrisSearcherConfig(
        isDebug = isDebug,
        apiKey = apiKey,
        host = host,
        outputFormat = outputFormat,
        language = language,
        limit = limit,
        dialogErrorTitle = dialogErrorTitle,
        positiveButtonText = positiveButtonText,
        captureLabelText = captureLabelText,
        noOfferFoundErrorText = noOfferFoundErrorText,
        cameraPermissionDeniedErrorMessage = cameraPermissionDeniedErrorMessage,
    )
}