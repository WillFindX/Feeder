package me.zsr.feeder.source;

import java.util.List;

import me.zsr.feeder.dao.FeedItem;

/**
 * @description:
 * @author: Match
 * @date: 11/15/15
 */
public interface OnItemListLoadListener {

    void success(List<FeedItem> list);

    void error(String msg);
}
