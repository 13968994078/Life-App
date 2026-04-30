package com.lifeapp.service.impl;

import com.lifeapp.integration.RemoteInspirationClient;
import com.lifeapp.vo.HomeContentResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentServiceImplTest {

    @Test
    void getHomeContentUsesRemoteQuoteWhenAvailable() {
        RemoteInspirationClient remoteInspirationClient = mock(RemoteInspirationClient.class);
        when(remoteInspirationClient.fetchLatestQuote()).thenReturn(new HomeContentResponse.QuoteBlock("把日子过成喜欢的样子", "hitokoto"));

        ContentServiceImpl service = new ContentServiceImpl(remoteInspirationClient);

        HomeContentResponse result = service.getHomeContent();

        assertNotNull(result);
        assertEquals("把日子过成喜欢的样子", result.getQuote().getText());
        assertEquals("hitokoto", result.getQuote().getSource());
        assertFalse(result.getHomeHero().getTitle().isEmpty());
        assertFalse(result.getFoodHero().getSubtitle().isEmpty());
    }

    @Test
    void getHomeContentFallsBackToLocalQuoteWhenRemoteFails() {
        RemoteInspirationClient remoteInspirationClient = mock(RemoteInspirationClient.class);
        when(remoteInspirationClient.fetchLatestQuote()).thenThrow(new RuntimeException("network error"));

        ContentServiceImpl service = new ContentServiceImpl(remoteInspirationClient);

        HomeContentResponse result = service.getHomeContent();

        assertNotNull(result);
        assertNotNull(result.getQuote());
        assertFalse(result.getQuote().getText().isEmpty());
        assertEquals("今日短笺", result.getQuote().getSource());
    }
}
