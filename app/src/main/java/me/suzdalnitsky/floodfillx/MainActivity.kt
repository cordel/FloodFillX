package me.suzdalnitsky.floodfillx

import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.widget.RxSeekBar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.suzdalnitsky.floodfillx.algorithm.BfsAlgorithm
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
        applySettings(settingsStore.userSettings)
        pointsView.setOnPointClickListener(::startAlgorithm)

        toolbar.setNavigationOnClickListener {
            with(supportFragmentManager) {
                if (findFragmentByTag(SETTINGS_FRAGMENT_KEY) == null) {
                    pauseAlgorithm()
                    SettingsFragment().showNow(this, SETTINGS_FRAGMENT_KEY)
                }
            }
        }
        refresh.setOnClickListener { refresh() }
    }

    override fun onResume() {
        super.onResume()
        resumeAlgorithm()
    }

    override fun onPause() {
        pauseAlgorithm()
        super.onPause()
    }

    override fun onSettingsUpdated() = refresh()
    override fun onSettingsNotUpdated() = resumeAlgorithm()

    private fun refresh() = with(settingsStore.userSettings) {
        disposable?.dispose()
        StaticStore.algorithm = null

        StaticStore.points = randomizePoints(width = width, height = height)
        pointsView.init(width, height, StaticStore.points)
        pointsView.refresh()
    }

    private fun startAlgorithm(point: Point) {
        disposable?.dispose()
        StaticStore.algorithm = BfsAlgorithm(StaticStore.points, point)
        resumeAlgorithm()
    }

    private fun resumeAlgorithm() {
        if (lifecycle.currentState != Lifecycle.State.RESUMED) return
        if (StaticStore.algorithm == null) return

        var isLaunching = true

        RxSeekBar.changes(seekBar)
            .map { 101L - it }
            .switchMap { interval ->
                Observable.interval(if (isLaunching) 0L else interval, interval, TimeUnit.MILLISECONDS)
                    .also { isLaunching = false }
            }
            .observeOn(Schedulers.computation())
            .map { StaticStore.algorithm!!.performStep() }
            .takeWhile { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pointsView.refresh() }, { log(it) })
            .let { disposable = it }
    }

    private fun applySettings(settings: UserSettings) = with(settings) {
        StaticStore.assureInit(width, height)
        pointsView.init(width, height, StaticStore.points)
        seekBar.progress = speed
    }

    private fun pauseAlgorithm() = disposable?.dispose()

    companion object {
        private const val SETTINGS_FRAGMENT_KEY = "SETTINGS_FRAGMENT_KEY"
    }
}
