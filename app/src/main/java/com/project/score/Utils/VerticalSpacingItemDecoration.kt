package com.project.score.Utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position != itemCount - 1) { // 마지막 아이템이 아닐 때만 간격 적용
            outRect.right = space
        }
    }

}


class EqualSpacingItemDecoration(
    private val spanCount: Int, // 한 줄에 몇 개의 아이템을 배치할지
    private val screenWidth: Int // 화면 너비
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemWidth = screenWidth / spanCount // 각 아이템의 너비를 균등하게 설정
        val position = parent.getChildAdapterPosition(view) // 아이템 위치

        val spacing = (screenWidth - (itemWidth * spanCount)) / (spanCount + 1) // 간격 계산

        outRect.left = spacing
        outRect.right = spacing
    }
}

class DynamicSpacingItemDecoration(private val spacingRatio: Float) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val itemCount = parent.adapter?.itemCount ?: 1
        val spacing = (parent.width * spacingRatio / itemCount).toInt()

        outRect.left = spacing / 2
        outRect.right = spacing / 2
    }
}

