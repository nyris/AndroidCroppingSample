package io.nyris.ui.screens.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.gson.Gson
import io.nyris.camera.Callback
import io.nyris.ui.*
import io.nyris.ui.NyrisSearcherConfig
import io.nyris.ui.models.OfferUiModel
import io.nyris.ui.screens.result.ResultFragment
import kotlinx.android.synthetic.main.camera_fragment.*

class CameraFragment : Fragment(R.layout.camera_fragment), CameraContract.View {
    companion object {
        val TAG = "CameraFragment"
    }
    private val gson = Gson()
    private val presenter = CameraFragmentPresenter()
    private lateinit var config: NyrisSearcherConfig
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = createSearcherConfig()
        presenter.onSearchConfig(config = config)
        presenter.onAttach(this)

        pinViewCropper.addOnPinClickListener {
            pinViewCropper.initCropWindow(it)
        }

        cvTakePic.setOnClickListener {
            onTakePicClick()
        }
        imValidate.setOnClickListener {
            onValidateClick()
        }

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            presenter.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (isCameraPermissionGranted) {
            presenter.onResume()
        } else {
            presenter.onPermissionsDenied(
                mutableListOf<String>().apply {
                    add(Manifest.permission.CAMERA)
                }
            )
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }

    private fun createSearcherConfig() : NyrisSearcherConfig {
        val extraJson = requireArguments().getString(NyrisSearcher.CONFIG_KEY)
        return gson.fromJson(extraJson, NyrisSearcherConfig::class.java)
    }

    private fun onTakePicClick() {
        presenter.onTakePicViewClick()
    }

    private fun onValidateClick() {
        presenter.onImageCrop(pinViewCropper.selectedObjectProposal.toRect())
    }

    override fun setCaptureLabel(label: String) {
        tvCaptureLabel.text = label
    }

    override fun addCameraCallback(callback: Callback) {
        camera.addCallback(callback)
    }

    override fun removeCameraCallback(callback: Callback) {
        camera.removeCallback(callback)
    }

    override fun startCamera() {
        camera.start()
    }

    override fun stopCamera() {
        camera.stop()
    }

    override fun hideLabelCapture() {
        tvCaptureLabel.isVisible = false
    }

    override fun showLabelCapture() {
        tvCaptureLabel.isVisible = true
    }

    override fun showImageCameraPreview() {
        imPreview.isVisible = true
    }

    override fun hideImageCameraPreview() {
        imPreview.isVisible = false
    }

    override fun showTakePicView() {
        cvTakePic.isVisible = true
    }

    override fun hideTakePicView() {
        cvTakePic.isVisible = false
    }

    override fun showValidateView() {
        imValidate.isVisible = true
    }

    override fun hideValidateView() {
        imValidate.isVisible = false
    }

    override fun showLoading() {
        progress.isVisible = true
    }

    override fun hideLoading() {
        progress.isVisible = false
    }

    override fun takePicture() {
        camera.takePicture()
    }

    override fun setImPreviewBitmap(bitmap: Bitmap) {
        imPreview.setBitmap(bitmap)
    }

    override fun showDebugBitmap(bitmap: Bitmap) {
        imageDebug.isVisible = true
        imageDebug.setImageBitmap(bitmap)
    }

    override fun showError(message: String?) {
        showErrorDialog(message)
    }

    override fun resetViewCropper(defaultRect: Rect) {
        pinViewCropper.initCropWindow(defaultRect.toRectF())
    }

    override fun resetViewCropper() {
        pinViewCropper.reset()
    }

    override fun hideViewCropper() {
        pinViewCropper.isVisible = false
    }

    override fun showViewCropper() {
        pinViewCropper.isVisible = true
    }

    override fun sendResult(offers: List<OfferUiModel>) {
        val bundle = bundleOf(
            NyrisSearcher.SEARCH_RESULT_KEY to ArrayList(offers)
        )
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
            add<ResultFragment>(containerViewId = R.id.fragmentContainer, args = bundle)
            addToBackStack(ResultFragment.TAG)
        }
    }

    override fun close() {
        onBackPressedCallback.remove()
        activity?.onBackPressed()
    }

    override fun getFragmentContext(): Context = requireContext()

    override fun showCroppingObjects(objects: List<RectF>) {
        showViewCropper()
        pinViewCropper.setExtractedObjects(objects)
        pinViewCropper.initCropWindow(objects.first())
    }

    private fun showErrorDialog(message: String?) {
        message ?: return
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(config.dialogErrorTitle)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(config.positiveButtonText) { _, _ ->
            presenter.onOkErrorClick()
        }
        alertDialog.show()
    }
}