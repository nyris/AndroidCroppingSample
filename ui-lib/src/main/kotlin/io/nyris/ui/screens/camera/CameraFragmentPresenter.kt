package io.nyris.ui.screens.camera

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import io.nyris.camera.BaseCameraView
import io.nyris.camera.Callback
import io.nyris.camera.ImageUtils
import io.nyris.camera.Size
import io.nyris.sdk.INyris
import io.nyris.sdk.Nyris
import io.nyris.sdk.NyrisConfig
import io.nyris.ui.NyrisSearcherConfig
import io.nyris.ui.normalize
import io.nyris.ui.models.toOfferUiModelList
import io.nyris.ui.screens.camera.CameraFragmentPresenter.NyrisSearcherPresenterStatus.*
import io.nyris.ui.toRectFList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream

internal class CameraFragmentPresenter: CameraContract.Presenter, Callback {
    enum class NyrisSearcherPresenterStatus {None, CameraListening, Cropping, Searching}

    private var presenterStatus: NyrisSearcherPresenterStatus = None
    private var view: CameraContract.View? = null
    private var lastCroppedImage: Rect? = null

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var nyris: INyris
    private lateinit var config: NyrisSearcherConfig
    private lateinit var originalImageBitmap: Bitmap
    private lateinit var originalImageSize: Size

    override fun onAttach(view: CameraContract.View) {
        this.view = view
        checkApiKey()
        view.setCaptureLabel(config.captureLabelText)
    }

    override fun onDetach() {
        compositeDisposable.clear()
        view = null
    }

    override fun onResume() {
        view?.addCameraCallback(this)
        if (presenterStatus == Cropping) {
            return
        }

        view?.startCamera()
    }

    override fun onPause() {
        view?.removeCameraCallback(this)
        view?.stopCamera()
    }

    override fun onSearchConfig(config: NyrisSearcherConfig) {
        this.config = config
        with(config) {
            nyris = Nyris.createInstance(
                apiKey,
                NyrisConfig(isDebug = isDebug).apply {
                    if (host != null) {
                        hostUrl = host
                    }
                }
            ).apply {
                imageMatching().outputFormat(outputFormat)
                imageMatching().language(language)
                imageMatching().limit(limit)
            }
        }
    }

    override fun onTakePicViewClick() {
        view?.takePicture()
    }

    override fun onCircleViewAnimationEnd() {
        if (presenterStatus == Cropping) {
            view?.showValidateView()
        }
    }

    override fun onError(errorMessage: String) {
        view?.showError(errorMessage)
    }
    override fun onPictureTaken(cameraView: BaseCameraView, resizedImage: ByteArray) {
        view?.showLoading()
        view?.hideTakePicView()
        val resizedImageBtm = BitmapFactory.decodeByteArray(resizedImage, 0, resizedImage.size)

        compositeDisposable.add(
            nyris.regions()
            .detect(resizedImage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({objectList ->
                presenterStatus = Cropping
                view?.hideLoading()
                view?.showCroppingObjects(
                    objectList.normalize(
                        resizedImageBtm.width, resizedImageBtm.height, originalImageSize
                    ).toRectFList()
                )
                view?.showViewCropper()
                view?.showValidateView()
            }, {
                presenterStatus = Cropping
                view?.hideLoading()
                view?.resetViewCropper()
                view?.showError(it.message)
            })
        )
    }

    override fun onPictureTakenOriginal(cameraView: BaseCameraView, original: ByteArray) {
        view?.stopCamera()
        originalImageBitmap = BitmapFactory.decodeByteArray(original, 0, original.size)
        originalImageSize = Size(originalImageBitmap.width, originalImageBitmap.height)
        view?.setImPreviewBitmap(originalImageBitmap)

        view?.showImageCameraPreview()
    }

    override fun onImageCrop(rect: Rect) {
        view?.getFragmentContext() ?: return

        presenterStatus = Searching
        lastCroppedImage = rect.normalize(originalImageBitmap)

        val croppedBitmap = Bitmap.createBitmap(
            originalImageBitmap,
            lastCroppedImage!!.left,
            lastCroppedImage!!.top,
            lastCroppedImage!!.width(),
            lastCroppedImage!!.height()
        )

        //view?.showDebugBitmap(croppedBitmap)

        val stream = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitmapData = stream.toByteArray()
        val image = ImageUtils.resize(view!!.getFragmentContext(), bitmapData, 512, 512)

        view?.hideValidateView()
        view?.showLoading()
        view?.hideViewCropper()

        compositeDisposable.add(
            nyris.imageMatching()
                .limit(config.limit)
                .match(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.hideLoading()
                    val offers = it.toOfferUiModelList()
                    if (offers.isEmpty()) {
                        view?.showError(config.noOfferFoundErrorText)
                    } else {
                        view?.hideLoading()
                        presenterStatus = Cropping
                        view?.sendResult(it.toOfferUiModelList())
                        Handler().postDelayed({
                            view?.showViewCropper()
                            view?.showValidateView()
                        }, 2000)
                        //onBackPressed()
                    }
                }, {
                    view?.hideLoading()
                    view?.showError(it.message)
                    view?.showViewCropper()
                    view?.showValidateView()
                })
        )
    }

    override fun onBackPressed() {
        when (presenterStatus) {
            Searching -> {
                compositeDisposable.clear()
                view?.hideLoading()
                view?.showValidateView()
                view?.showViewCropper()
                view?.resetViewCropper()
                presenterStatus = Cropping
            }
            Cropping -> {
                view?.hideLoading()
                view?.hideViewCropper()
                view?.hideValidateView()
                view?.hideImageCameraPreview()
                view?.showTakePicView()
                view?.showLabelCapture()
                view?.startCamera()
                presenterStatus = CameraListening
            }
            else -> {
                view?.close()
            }
        }
    }

    override fun onOkErrorClick() {
        if (presenterStatus == CameraListening || presenterStatus == None) {
            view?.close()
            return
        }

        presenterStatus = Cropping
        compositeDisposable.clear()
        view?.hideLoading()
        view?.showTakePicView()
        view?.showValidateView()
        view?.showViewCropper()
        if (lastCroppedImage == null) {
            view?.resetViewCropper();
        }
        else {
            view?.resetViewCropper(lastCroppedImage!!)
        }
    }

    override fun onPermissionsDenied(permissions: List<String>) {
        permissions.forEach { permission ->
            if(permission == Manifest.permission.CAMERA) {
                view?.showError(config.cameraPermissionDeniedErrorMessage)
            }
        }
    }

    private fun checkApiKey() {
        val isValid = config.apiKey.isNotBlank() && !config.apiKey.contains(" ")
        if (!isValid) {
            onError("Please set a correct api key")
        }
    }

//region ignore
    override fun onCameraOpened(cameraView: BaseCameraView) {
    }

    override fun onCameraClosed(cameraView: BaseCameraView) {
    }
//endregion
}