package com.lifeapp.integration;

import com.lifeapp.vo.HomeContentResponse;

public interface RemoteInspirationClient {

    HomeContentResponse.QuoteBlock fetchLatestQuote();
}
