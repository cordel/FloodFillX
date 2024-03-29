package me.suzdalnitsky.floodfillx.ui

import android.graphics.Point
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxSeekBar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import me.suzdalnitsky.floodfillx.PointRandomizer
import me.suzdalnitsky.floodfillx.R
import me.suzdalnitsky.floodfillx.StaticStore
import me.suzdalnitsky.floodfillx.algorithm.BfsAlgorithm
import me.suzdalnitsky.floodfillx.algorithm.DfsAlgorithm
import me.suzdalnitsky.floodfillx.algorithm.RandomPickAlgorithm
import me.suzdalnitsky.floodfillx.consume
import me.suzdalnitsky.floodfillx.invokeIfResumed
import me.suzdalnitsky.floodfillx.log
import me.suzdalnitsky.floodfillx.persistance.SettingsStore
import me.suzdalnitsky.floodfillx.persistance.UserSettings
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SettingsFragment.Listener {

    private var disposable: Disposable? = null
    private lateinit var settingsStore: SettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingsStore = SettingsStore(applicationContext)
        initViews()
    }

    private fun initViews() {
        restoreSettings(settingsStore.userSettings)
        pointsView.setOnPointClickListener(::startAlgorithm)
        refresh.setOnClickListener { refresh() }

        with(toolbar) {
            inflateMenu(R.menu.menu_algorithms)
            setOnMenuItemClickListener(::onMenuItemSelected)
            setNavigationOnClickListener { navigateToSettings() }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeAlgorithm()
    }

    override fun onPause() {
        pauseAlgorithm()
        saveSpeedSetting()
        super.onPause()
    }

    private fun resumeAlgorithm() {
        if (StaticStore.algorithm == null) return

        var skipInitialDelay = true

        RxSeekBar.changes(seekBar)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .map { 101L - it }
            .switchMap { interval ->
                Observable.interval(if (skipInitialDelay) 0L else interval, interval, TimeUnit.MILLISECONDS)
                    .also { skipInitialDelay = false }
            }
            .map { StaticStore.algorithm!!.performStep() }
            .takeWhile { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pointsView.refresh() }, { log(it) })
            .let { disposable = it }
    }

    private fun pauseAlgorithm() = disposable?.dispose()
    private fun saveSpeedSetting() = settingsStore.updateSettings { copy(speed = seekBar.progress) }

    override fun onSettingsUpdated() = invokeIfResumed(::refresh)
    override fun onSettingsNotUpdated() = invokeIfResumed(::resumeAlgorithm)

    private fun refresh() = with(settingsStore.userSettings) {
        disposable?.dispose()
        StaticStore.algorithm = null

        StaticStore.points = PointRandomizer.randomizePoints(width = width, height = height)
        pointsView.init(width, height, StaticStore.points)
        pointsView.refresh()
    }

    private fun startAlgorithm(point: Point) {
        disposable?.dispose()
        StaticStore.algorithm = when (settingsStore.userSettings.algorithm) {
            SelectableAlgorithm.BFS -> BfsAlgorithm(StaticStore.points, point)
            SelectableAlgorithm.DFS -> DfsAlgorithm(StaticStore.points, point)
            SelectableAlgorithm.RP -> RandomPickAlgorithm(StaticStore.points, point)
        }
        resumeAlgorithm()
    }

    private fun restoreSettings(settings: UserSettings) = with(settings) {
        StaticStore.assureInit(width, height)
        toolbar.setTitle(algorithm.title)
        pointsView.init(width, height, StaticStore.points)
        seekBar.progress = speed
    }

    private fun navigateToSettings() = with(supportFragmentManager) {
        if (findFragmentByTag(SETTINGS_FRAGMENT_KEY) == null) {
            pauseAlgorithm()
            SettingsFragment().showNow(this, SETTINGS_FRAGMENT_KEY)
        }
    }

    private fun onMenuItemSelected(item: MenuItem) = consume {
        val newAlgorithm = when (item.itemId) {
            R.id.menu_bfs -> SelectableAlgorithm.BFS
            R.id.menu_dfs -> SelectableAlgorithm.DFS
            R.id.menu_rp -> SelectableAlgorithm.RP
            else -> error("Unreachable")
        }
        settingsStore.updateSettings { copy(algorithm = newAlgorithm) }
        toolbar.setTitle(newAlgorithm.title)
    }

    companion object {
        private const val SETTINGS_FRAGMENT_KEY = "SETTINGS_FRAGMENT_KEY"
    }
}
