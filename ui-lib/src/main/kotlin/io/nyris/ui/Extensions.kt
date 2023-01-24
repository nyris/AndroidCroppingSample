package io.nyris.ui

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import io.nyris.camera.ImageUtils
import io.nyris.camera.Size
import io.nyris.sdk.ObjectList
import io.nyris.sdk.Region
import kotlin.math.abs
import kotlin.math.sqrt


internal fun Region?.isValid(): Boolean {
    if(this == null) return false
    return abs(left + top + right + bottom) >= 0
}

internal fun Rect.normalize(bitmap: Bitmap): Rect  = normalize(bitmap.width, bitmap.height)

internal fun Rect.normalize(with: Int, height: Int): Rect {
    val newRect = Rect(this)

    if(newRect.left < 0) newRect.left = 0
    if(newRect.top < 0) newRect.top = 0
    if(newRect.bottom > height) newRect.bottom = height
    if(newRect.right > with) newRect.right = with

    return newRect
}

fun List<Rect>.toRectFList() = map {
    it.toRectF()
}

fun Rect.toRectF() = RectF(
    left.toFloat(),
    top.toFloat(),
    right.toFloat(),
    bottom.toFloat()
)

fun RectF.toRect() = Rect(
    left.toInt(),
    top.toInt(),
    right.toInt(),
    bottom.toInt()
)

fun ObjectList.normalize(bitmapWidth: Int, bitmapHeight: Int, targetSize: Size): MutableList<Rect> {
    var width: Int
    val height: Int
    if (targetSize.height > targetSize.width) {
        height = targetSize.height
        width = targetSize.width
    } else {
        height = targetSize.width
        width = targetSize.height
    }

    val margin = 250
    val centerMargin = margin / 2
    width -= margin

    val defaultRect = Rect()
    defaultRect.left = centerMargin
    defaultRect.top = height / 2 - width / 2
    defaultRect.right = width + centerMargin
    defaultRect.bottom = defaultRect.top + width

    val mObjectProposalList = mutableListOf<Rect>()

    mObjectProposalList.add(0, defaultRect)

    val matrixTransform = ImageUtils.getTransformationMatrix(
        bitmapWidth,
        bitmapHeight,
        targetSize.width,
        targetSize.height,
        0,
        true)

    for (objectProposal in this.regions!!) {
        val region = objectProposal.region!!
        val rectF = RectF(region.left.toFloat(), region.top.toFloat(), region.right.toFloat(), region.bottom.toFloat())
        matrixTransform.mapRect(rectF)
        if (rectF.left < 0F)
            rectF.left = 0F
        if (rectF.top < 0F)
            rectF.top = 0F
        if (rectF.bottom > targetSize.height.toFloat())
            rectF.bottom = targetSize.height.toFloat()
        if (rectF.right > targetSize.width.toFloat())
            rectF.right = targetSize.width.toFloat()

        val distance = distance(defaultRect.centerX(), defaultRect.centerY(), rectF.centerX(), rectF.centerY())
        val thirdWidth = defaultRect.width()/3
        if(distance >0 && distance <= thirdWidth){
            mObjectProposalList.remove(defaultRect)
        }

        mObjectProposalList.add(rectF.toRect())
    }

    return mObjectProposalList
}

private fun distance(x1 : Int, y1 : Int, x2 : Float, y2 : Float) : Int {
    val dx   = (x1 - x2).toDouble()
    val dy   = (y1 - y2).toDouble()
    return sqrt( dx*dx + dy*dy ).toInt()
}