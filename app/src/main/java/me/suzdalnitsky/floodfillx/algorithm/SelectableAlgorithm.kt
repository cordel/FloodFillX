package me.suzdalnitsky.floodfillx.algorithm

import androidx.annotation.StringRes
import me.suzdalnitsky.floodfillx.R

enum class SelectableAlgorithm(@StringRes val title: Int) {
    BFS(R.string.title_algorithm_bfs),
    DFS(R.string.title_algorithm_dfs)
}