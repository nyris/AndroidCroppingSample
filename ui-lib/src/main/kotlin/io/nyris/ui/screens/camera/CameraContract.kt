package io.nyris.ui.screens.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import io.nyris.camera.Callback
import io.nyris.ui.NyrisSearcherConfig
import io.nyris.ui.models.OfferUiModel
import java.util.Objects

internal class CameraContract {
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun onSearchConfig(config: NyrisSearcherConfig)
        fun onResume()
        fun onPause()
        fun onTakePicViewClick()
        fun onCircleViewAnimationEnd()
        fun onImageCrop(rectF: Rect)
        fun onBackPressed()
        fun onOkErrorClick()
        fun onPermissionsDenied(permissions: List<String>)
    }

    interface View {
        fun showError(message: String?)
        fun setCaptureLabel(label: String)
        fun addCameraCallback(callback: Callback)
        fun removeCameraCallback(callback: Callback)
        fun startCamera()
        fun stopCamera()
        fun hideLabelCapture()
        fun showLabelCapture()
        fun showImageCameraPreview()
        fun hideImageCameraPreview()
        fun showTakePicView()
        fun hideTakePicView()
        fun showValidateView()
        fun hideValidateView()
        fun showLoading()
        fun hideLoading()
        fun takePicture()
        fun setImPreviewBitmap(bitmap: Bitmap)
        fun showDebugBitmap(bitmap: Bitmap)
        fun resetViewCropper()
        fun resetViewCropper(defaultRect: Rect)
        fun hideViewCropper()
        fun showViewCropper()
        fun sendResult(offers: List<OfferUiModel>)
        fun close()
        fun getFragmentContext(): Context
        fun showCroppingObjects(objects: List<RectF>)
    }
}