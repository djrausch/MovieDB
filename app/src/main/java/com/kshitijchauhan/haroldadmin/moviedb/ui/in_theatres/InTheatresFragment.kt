package com.kshitijchauhan.haroldadmin.moviedb.ui.in_theatres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.kshitijchauhan.haroldadmin.moviedb.R
import com.kshitijchauhan.haroldadmin.moviedb.repository.data.Resource
import com.kshitijchauhan.haroldadmin.moviedb.ui.BaseFragment
import com.kshitijchauhan.haroldadmin.moviedb.ui.UIState
import com.kshitijchauhan.haroldadmin.moviedb.ui.common.EpoxyCallbacks
import com.kshitijchauhan.haroldadmin.moviedb.ui.library.LibraryFragmentDirections
import com.kshitijchauhan.haroldadmin.moviedb.ui.main.MainViewModel
import com.kshitijchauhan.haroldadmin.moviedb.utils.EqualSpaceGridItemDecoration
import com.kshitijchauhan.haroldadmin.moviedb.utils.extensions.getNumberOfColumns
import com.kshitijchauhan.haroldadmin.mvrxlite.base.MVRxLiteView
import kotlinx.android.synthetic.main.fragment_in_theatres.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class InTheatresFragment : BaseFragment(), MVRxLiteView<UIState.InTheatresScreenState> {

    private val mainViewModel: MainViewModel by sharedViewModel()

    override val associatedUIState: UIState = UIState.InTheatresScreenState(Resource.Loading())

    private val inTheatresViewModel: InTheatresViewModel by viewModel {
        parametersOf(associatedUIState)
    }

    private val callbacks = object : EpoxyCallbacks {
        override fun onMovieItemClicked(id: Int, transitionName: String, sharedView: View?) {
            InTheatresFragmentDirections.actionInTheatresFragmentToMovieDetailsFragment()
                .apply {
                    movieIdArg = id
                    isAuthenticatedArg = mainViewModel.isAuthenticated
                    transitionNameArg = transitionName
                }
                .also { action ->
                    sharedView?.let {
                        val extras = FragmentNavigatorExtras(it to transitionName)
                        findNavController().navigate(action, extras)
                    } ?: run {
                        findNavController().navigate(action)
                    }
                }
        }
    }

    private val glideRequestManager: RequestManager by inject("fragment-glide-request-manager") {
        parametersOf(this)
    }

    private val inTheatresEpoxyController: InTheatresEpoxyController by inject {
        parametersOf(callbacks, glideRequestManager)
    }

    override fun updateToolbarTitle() {
        mainViewModel.updateToolbarTitle(getString(R.string.title_in_theatres_screen))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_in_theatres, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inTheatresViewModel.apply {
            getMoviesInTheatres()

            state.observe(viewLifecycleOwner, Observer { state ->
                renderState(state)
            })

            message.observe(viewLifecycleOwner, Observer { message ->
                mainViewModel.showSnackbar(message)
            })

            forceRefreshInTheatresCollection()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToolbarTitle()
        rvMovies.apply {
            val columns = resources.getDimension(R.dimen.movie_grid_poster_width).getNumberOfColumns(context!!)
            val space = resources.getDimension(R.dimen.movie_grid_item_space)
            layoutManager = GridLayoutManager(context, columns)
            addItemDecoration(EqualSpaceGridItemDecoration(space.roundToInt()))
            setController(inTheatresEpoxyController)
        }
    }

    override fun renderState(state: UIState.InTheatresScreenState) {
        inTheatresEpoxyController.setData(state)
    }
}
