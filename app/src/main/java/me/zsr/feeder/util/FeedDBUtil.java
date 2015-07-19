package me.zsr.feeder.util;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.zsr.feeder.App;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedItemDao;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.dao.FeedSourceDao;

/**
 * @description: Save data to db and send event broadcast.
 * @author: Zhangshaoru
 * @date: 15-6-11
 */
public class FeedDBUtil {
    private static FeedDBUtil sFeedDBUtil;
    private FeedSourceDao mFeedSourceDao;
    private FeedItemDao mFeedItemDao;

    private FeedDBUtil() {}

    public static FeedDBUtil getInstance() {
        if (sFeedDBUtil == null) {
            // All init here
            sFeedDBUtil = new FeedDBUtil();
            sFeedDBUtil.mFeedSourceDao = App.getDaoSession().getFeedSourceDao();
            sFeedDBUtil.mFeedItemDao = App.getDaoSession().getFeedItemDao();
        }
        return sFeedDBUtil;
    }

    public void saveFeedSource(FeedSource feedSource) {
        mFeedSourceDao.insertOrReplace(feedSource);
//        EventBus.getDefault().post(CommonEvent.FEED_DB_UPDATED);
    }

    public void saveFeedItem(FeedItem feedItem) {
        mFeedItemDao.insertOrReplace(feedItem);
    }

    public int countFeedItemByRead(long sourceId, boolean read) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                FeedItemDao.Properties.Read.eq(read)).list().size();
    }

    public List<FeedItem> getFeedItemListByRead(long sourceId, boolean read) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                FeedItemDao.Properties.Read.eq(read)).orderDesc(FeedItemDao.Properties.Date).list();
    }

    public int countFeedItemByStar(long sourceId, boolean star) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                FeedItemDao.Properties.Star.eq(star)).list().size();
    }

    public List<FeedItem> getFeedItemListByStar(long sourceId, boolean star) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId),
                FeedItemDao.Properties.Star.eq(star)).orderDesc(FeedItemDao.Properties.Date).list();
    }

    public List<FeedItem> getAllFeedItemList(long sourceId) {
        return mFeedItemDao.queryBuilder().where(
                FeedItemDao.Properties.FeedSourceId.eq(sourceId)).orderDesc(FeedItemDao.Properties.Date).list();
    }

    public void saveFeedItem(final List<FeedItem> feedItemList) {
        App.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = feedItemList.size() - 1; i >= 0; i--) {
                    mFeedItemDao.insertOrReplace(feedItemList.get(i));
                }
            }
        });
        EventBus.getDefault().post(CommonEvent.FEED_DB_UPDATED);
    }

    public FeedSource getFeedSourceById(long id) {
        List<FeedSource> list = App.getDaoSession().getFeedSourceDao().queryBuilder()
                .where(FeedSourceDao.Properties.Id.eq(id)).list();
        if (list.size() == 0) {
            LogUtil.w("No FeedSource found.");
            return null;
        } if (list.size() == 1) {
            return list.get(0);
        } else {
            LogUtil.e("Somethings wrong with DB !!");
            return null;
        }
    }

    public FeedItem getFeedItemById(long id) {
        List<FeedItem> list = App.getDaoSession().getFeedItemDao().queryBuilder()
                .where(FeedItemDao.Properties.Id.eq(id)).list();
        if (list.size() == 0) {
            LogUtil.w("No FeedItem found.");
            return null;
        } if (list.size() == 1) {
            return list.get(0);
        } else {
            LogUtil.e("Somethings wrong with DB !!");
            return null;
        }
    }

    public List<FeedSource> loadAll() {
        return mFeedSourceDao.loadAll();
    }

    public boolean hasSource(String url) {
        return App.getDaoSession().getFeedSourceDao().queryBuilder()
                .where(FeedSourceDao.Properties.Url.eq(url)).list().size() > 0;
    }
}
