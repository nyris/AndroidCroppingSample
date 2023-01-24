package io.nyris.ui.models

import android.os.Parcelable
import androidx.annotation.Keep
import io.nyris.sdk.Links
import io.nyris.sdk.OfferResponse
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OfferUiModel(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val descriptionLong: String? = null,
    val language: String? = null,
    val brand: String? = null,
    val catalogNumbers: List<String>? = null,
    val customIds: Map<String, String>? = null,
    val keywords: List<String>? = null,
    val categories: List<String>? = null,
    val availability: String? = null,
    val feedId: String? = null,
    val groupId: String? = null,
    val priceStr: String? = null,
    val salePrice: String? = null,
    val links: LinksUiModel? = null,
    val images: List<String>? = null,
    val metadata: String? = null,
    val sku: String? = null,
    val score: Float = 0.0F
) : Parcelable

@Keep
@Parcelize
data class LinksUiModel (
    val main: String? = null,
    val mobile: String? = null
) : Parcelable

fun OfferResponse.toOfferUiModelList() = offers.map {
    with(it) {
        OfferUiModel(
            id = id,
            title = title,
            description = description,
            descriptionLong = descriptionLong,
            language = language,
            brand = brand,
            catalogNumbers = catalogNumbers,
            customIds =  customIds,
            keywords = keywords,
            categories = categories,
            availability = availability,
            feedId = feedId,
            groupId = groupId,
            priceStr = priceStr,
            salePrice = salePrice,
            links = links?.toLinkUiModel(),
            images = images,
            metadata = metadata,
            sku = sku,
            score = score,
        )
    }
}

fun Links.toLinkUiModel() = LinksUiModel(
    main = main,
    mobile = mobile
)