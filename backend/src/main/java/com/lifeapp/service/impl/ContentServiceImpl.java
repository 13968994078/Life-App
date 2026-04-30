package com.lifeapp.service.impl;

import com.lifeapp.integration.RemoteInspirationClient;
import com.lifeapp.service.ContentService;
import com.lifeapp.vo.HomeContentResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContentServiceImpl implements ContentService {

    private static final long CACHE_TTL_MILLIS = 30L * 60L * 1000L;
    private static final String[] LOCAL_QUOTES = new String[] {
            "把日子翻到今天这一页，先吃好一顿。",
            "清晨不必很响，准时醒来就算开了个好头。",
            "生活不急着改版，先把今天排整齐。",
            "给自己留一点余地，也给今天留一点光。"
    };

    private final RemoteInspirationClient remoteInspirationClient;

    private volatile HomeContentResponse.QuoteBlock cachedQuote;
    private volatile long cachedAtMillis;

    public ContentServiceImpl(RemoteInspirationClient remoteInspirationClient) {
        this.remoteInspirationClient = remoteInspirationClient;
    }

    @Override
    public HomeContentResponse getHomeContent() {
        HomeContentResponse response = new HomeContentResponse();
        response.setQuote(resolveQuote());
        response.setHomeHero(new HomeContentResponse.HeroBlock(
                "今天这一页，从饭点和作息开始",
                "先吃稳一餐，再记下一次起床时间，日子就有了线索。"
        ));
        response.setFoodHero(new HomeContentResponse.HeroBlock(
                "这一餐，让转盘提个醒",
                "选个大概方向，剩下的交给一点运气。",
                "菜单还是空的，先写下几样常吃的。"
        ));
        response.setCheckinHero(new HomeContentResponse.HeroBlock(
                "把清晨留一笔",
                "起床时间写下来，作息会慢慢露出自己的样子。",
                "这个月还没有记录，第一次签到会从这里开始。"
        ));
        response.setMineHero(new HomeContentResponse.HeroBlock(
                "你的生活小账本",
                "饭点、签到、提醒，都收在这一页，翻起来不费劲。"
        ));
        return response;
    }

    private HomeContentResponse.QuoteBlock resolveQuote() {
        long now = System.currentTimeMillis();
        HomeContentResponse.QuoteBlock current = cachedQuote;
        if (current != null && now - cachedAtMillis < CACHE_TTL_MILLIS) {
            return current;
        }

        synchronized (this) {
            current = cachedQuote;
            now = System.currentTimeMillis();
            if (current != null && now - cachedAtMillis < CACHE_TTL_MILLIS) {
                return current;
            }

            try {
                cachedQuote = remoteInspirationClient.fetchLatestQuote();
            } catch (Exception ignored) {
                cachedQuote = buildLocalQuote();
            }
            cachedAtMillis = now;
            return cachedQuote;
        }
    }

    private HomeContentResponse.QuoteBlock buildLocalQuote() {
        int index = LocalDate.now().getDayOfYear() % LOCAL_QUOTES.length;
        return new HomeContentResponse.QuoteBlock(LOCAL_QUOTES[index], "今日短笺");
    }
}
