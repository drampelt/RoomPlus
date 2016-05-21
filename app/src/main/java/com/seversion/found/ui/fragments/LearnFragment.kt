package com.seversion.found.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.hannesdorfmann.mosby.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_learn.*
import com.seversion.found.R
import com.seversion.found.data.models.Location
import com.seversion.found.inflate
import com.seversion.found.ui.FragmentLifecycle
import com.seversion.found.ui.activities.MainActivity
import com.seversion.found.ui.activities.SettingsActivity
import com.seversion.found.ui.adapters.LocationAdapter
import com.seversion.found.ui.presenters.LearnPresenter
import com.seversion.found.ui.views.LearnView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by Daniel on 2016-04-18.
 */

class LearnFragment : MvpFragment<LearnView, LearnPresenter>(), LearnView, FragmentLifecycle, MainActivity.FabHandler, AnkoLogger {

    private var locationAdapter: LocationAdapter? = null

    private var inputMethodManager: InputMethodManager? = null

    companion object {
        fun newInstance(): LearnFragment {
            return LearnFragment()
        }
    }

    override fun createPresenter() = LearnPresenter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_learn)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        locationAdapter = LocationAdapter(presenter)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = locationAdapter

        presenter.loadLocations()
    }

    override fun setLocations(locations: List<Location>) {
        locationAdapter?.items = locations
        locationAdapter?.notifyDataSetChanged()
    }

    override fun showList() {
        emptyState.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    override fun showEmptyState() {
        recyclerView.visibility = View.INVISIBLE
        emptyState.visibility = View.VISIBLE
    }

    override fun showError(error: String, showSettings: Boolean) {
        val snackbar = Snackbar.make(root, error, if (showSettings) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG)
        if (showSettings) {
            snackbar.setAction(R.string.main_action_open_settings, {
                startActivity<SettingsActivity>()
            })
        }
        snackbar.show()
        locationAdapter?.deselect()
    }

    override fun showDeleteConfirm(location: Location) {
        alert(resources.getString(R.string.learn_label_delete_confirm, location.name)) {
            positiveButton(R.string.learn_label_delete_confirm_yes) {
                presenter.confirmDelete(location)
            }
            negativeButton(R.string.learn_label_delete_confirm_no)
        }.show()
    }

    override fun onPauseFragment() {
        locationAdapter?.deselect()
    }

    override fun onResumeFragment() {}

    override fun getIcon() = R.drawable.ic_add

    override fun onClickFab() {
        var input: EditText? = null
        alert {
            customView {
                frameLayout {
                    input = editText() {
                        hint = resources.getString(R.string.learn_hint_location_name)
                    }.lparams(matchParent, wrapContent) {
                        margin = dip(16)
                    }
                }
            }

            positiveButton(resources.getString(R.string.learn_action_location_add)) {
                presenter.addLocation(input!!.text.toString())
            }
            negativeButton(resources.getString(R.string.learn_action_location_add_cancel))
        }.show()

        Observable.timer(500, TimeUnit.MILLISECONDS)
            .subscribe {
                inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            }
    }
}
