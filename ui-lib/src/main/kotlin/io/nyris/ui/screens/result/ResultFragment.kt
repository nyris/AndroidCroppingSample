package io.nyris.ui.screens.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.nyris.ui.Constants
import io.nyris.ui.NyrisSearcher
import io.nyris.ui.R
import io.nyris.ui.models.OfferUiModel
import kotlinx.android.synthetic.main.result_fragment.*

class ResultFragment : Fragment(R.layout.result_fragment) {
    companion object {
        const val TAG = "ResultFragment"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcView.adapter = ResultAdapter(
            requireArguments()
                .getParcelableArrayList<OfferUiModel>(NyrisSearcher.SEARCH_RESULT_KEY)
                ?.toList() ?: emptyList()
        )
    }
}