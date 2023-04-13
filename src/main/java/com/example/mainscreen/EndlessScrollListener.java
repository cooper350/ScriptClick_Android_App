package com.example.mainscreen;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotalItemCount) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    public abstract boolean onLoadMore(int page, int totalItemsCount);

}
