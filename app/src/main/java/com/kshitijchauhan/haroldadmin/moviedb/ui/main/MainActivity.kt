package com.kshitijchauhan.haroldadmin.moviedb.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.kshitijchauhan.haroldadmin.moviedb.R
import com.kshitijchauhan.haroldadmin.moviedb.ui.UIState
import com.kshitijchauhan.haroldadmin.moviedb.ui.auth.LoginFragment
import com.kshitijchauhan.haroldadmin.moviedb.ui.discover.DiscoverFragment
import com.kshitijchauhan.haroldadmin.moviedb.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {

    private var sessionId by SharedPreferencesDelegate(this, Constants.KEY_SESSION_ID, "")
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.state.observe(this, Observer { pair ->
            log(pair.second.toString())
            handleStateChange(pair)
        })

        savedInstanceState ?: mainViewModel.updateState(null to UIState.HomeScreenState)

        setSupportActionBar(mainToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

    }

    private fun handleStateChange(state: Pair<UIState?, UIState>) {
        when (state.second) {
            is UIState.HomeScreenState ->
                replaceFragment(HomeFragment.newInstance(), R.id.fragment_container)

            is UIState.AuthScreenState ->
                replaceFragment(LoginFragment.newInstance(), R.id.fragment_container)
            is UIState.DiscoverScreenState -> {

                val adInterpolator = AccelerateDecelerateInterpolator()
                val exitFade = Fade()
                exitFade.apply {
                    duration = 200
                    exitFade.interpolator = adInterpolator
                }

                val enterTransitionSet = TransitionSet()
                enterTransitionSet.apply {
                    addTransition(TransitionInflater.from(this@MainActivity).inflateTransition(android.R.transition.move))
                    interpolator = adInterpolator
                    startDelay = 100
                    duration = 200
                }

                val enterFade = Fade()
                enterFade.apply {
                    duration = 200
                    interpolator = adInterpolator
                }

                replaceFragment(DiscoverFragment.newInstance(),
                    R.id.fragment_container,
                    enterTransition = enterFade,
                    exitTransition = exitFade,
                    sharedElement = btDiscover,
                    sharedElementTransisition = enterTransitionSet)
            }
        }.safe
    }
}
