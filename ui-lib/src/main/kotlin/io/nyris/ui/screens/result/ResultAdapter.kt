package io.nyris.ui.screens.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.nyris.ui.R
import io.nyris.ui.models.OfferUiModel
import kotlinx.android.synthetic.main.result_item_view.view.*

class ResultAdapter(
    private val offers: List<OfferUiModel>
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.result_item_view, parent, false)
    )

    override fun getItemCount(): Int = offers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(offers[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(offer: OfferUiModel) {
            itemView.imOffer.load(
                offer.images?.firstOrNull() ?: android.R.drawable.stat_notify_error
            )
            itemView.tvTitle.text = offer.title ?: ""
            itemView.tvPrice.text = offer.priceStr ?: ""
        }
    }
}