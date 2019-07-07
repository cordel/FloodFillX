package me.suzdalnitsky.floodfillx.ui

import androidx.annotation.StringRes
import me.suzdalnitsky.floodfillx.R

enum class SelectableAlgorithm(@StringRes val title: Int) {
    BFS(R.string.title_algorithm_bfs),
    DFS(R.string.title_algorithm_dfs),
    RP(R.string.title_algorithm_rp)
}