package io.nyris.ui.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import io.nyris.ui.NyrisSearcher
import io.nyris.ui.R
import io.nyris.ui.screens.camera.CameraFragment

internal class NyrisSearcherActivity : AppCompatActivity(R.layout.searcher_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val bundle = bundleOf(
                    NyrisSearcher.CONFIG_KEY to intent.getStringExtra(NyrisSearcher.CONFIG_KEY)
                )
                setReorderingAllowed(true)
                add<CameraFragment>(containerViewId = R.id.fragmentContainer, args = bundle)
            }
        }
    }
}