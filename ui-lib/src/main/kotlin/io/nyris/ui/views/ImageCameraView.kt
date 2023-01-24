package io.nyris.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

internal class ImageCameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {
    private var bitmap: Bitmap? = null
    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        requestLayout()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bitmap ?: return
        canvas?.drawBitmap(bitmap!!, 0F, 0F, paint)
    }
}