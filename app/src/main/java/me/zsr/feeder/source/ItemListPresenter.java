package me.zsr.feeder.source;

import java.util.ArrayList;
import java.util.List;

import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.data.DataModel;
import me.zsr.feeder.data.IDataModel;
import me.zsr.feeder.util.NetworkUtil;

/**
 * @description:
 * @author: Match
 * @date: 11/16/15
 */
public class ItemListPresenter implements IItemListPresenter {
    private static final int LIMIT_LOAD_ONCE = 20;
    private IItemListView mView;
    private IDataModel mModel;
    private OnItemListLoadListener mItemLoadListener = new OnItemListLoadListener() {
        @Override
        public void success(List<FeedItem> list) {
            List<FeedItem> filtedList = new ArrayList<>();
            for (FeedItem feedItem : list) {
                if (!feedItem.getTrash()) {
                    filtedList.add(feedItem);
                }
            }

            mView.updated(filtedList);
            mView.hideLoading();
        }

        @Override
        public void error(String msg) {
            mView.showError(msg);
            mView.hideLoading();
        }
    };

    public ItemListPresenter(IItemListView view) {
        mView = view;
        mModel = new DataModel();
    }

    @Override
    public void itemSelected(final FeedItem item) {
        item.setRead(true);
        mModel.updateItem(item, new OnActionListener() {
            @Override
            public void success() {
                mView.showBody(item.getTitle());
            }

            @Override
            public void error(String msg) {
                mView.showError(msg);
            }
        });
    }

    @Override
    public void loadMore(long sourceId, int currentSize) {
        if (sourceId == App.SOURCE_ID_ALL) {
            mModel.loadAllItem(mItemLoadListener, currentSize + LIMIT_LOAD_ONCE);
        } else {
            mModel.loadItemList(sourceId, mItemLoadListener, currentSize + LIMIT_LOAD_ONCE);
        }
    }

    @Override
    public void reload(long sourceId, int currentSize) {
        if (sourceId == App.SOURCE_ID_ALL) {
            mModel.loadAllItem(mItemLoadListener, currentSize);
        } else {
            mModel.loadItemList(sourceId, mItemLoadListener, currentSize);
        }
    }

    @Override
    public void refresh(final long sourceId) {
        if (NetworkUtil.isWifiEnabled(App.getInstance())) {
            OnActionListener onActionListener = new OnActionListener() {
                @Override
                public void success() {
                    loadMore(sourceId, 0);
                }

                @Override
                public void error(String msg) {
                    mView.showError(msg);
                    mView.hideLoading();
                }
            };
            if (sourceId == App.SOURCE_ID_ALL) {
                mModel.refreshAll(onActionListener);
            } else {
                mModel.refreshSource(sourceId, onActionListener);
            }
        } else {
            mView.showError("Wi-Fi is disabled");
        }
    }
}
