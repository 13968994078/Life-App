package com.lifeapp.controller;

import com.lifeapp.auth.AuthInterceptor;
import com.lifeapp.auth.JwtUtil;
import com.lifeapp.common.GlobalExceptionHandler;
import com.lifeapp.service.ContentService;
import com.lifeapp.vo.HomeContentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContentControllerSecurityTest {

    private ContentService contentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        contentService = mock(ContentService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ContentController(contentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor(new JwtUtil("01234567890123456789012345678901")))
                .build();
    }

    @Test
    void homeContentDoesNotRequireAuthorizationHeader() throws Exception {
        when(contentService.getHomeContent()).thenReturn(buildResponse());

        mockMvc.perform(get("/api/content/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quote.text").value("把今天这一页过顺一点"))
                .andExpect(jsonPath("$.data.homeHero.title").value("今天这一页，从饭点和作息开始"));
    }

    private HomeContentResponse buildResponse() {
        HomeContentResponse response = new HomeContentResponse();
        response.setQuote(new HomeContentResponse.QuoteBlock("把今天这一页过顺一点", "今日短笺"));
        response.setHomeHero(new HomeContentResponse.HeroBlock("今天这一页，从饭点和作息开始", "先吃稳一餐，再记下一次起床时间。"));
        response.setFoodHero(new HomeContentResponse.HeroBlock("这一餐，让转盘提个醒", "选个大概方向，剩下的交给一点运气。", "菜单还是空的，先写下几样常吃的。"));
        response.setCheckinHero(new HomeContentResponse.HeroBlock("把清晨留一笔", "起床时间写下来，作息会慢慢露出自己的样子。", "这个月还没有记录。"));
        response.setMineHero(new HomeContentResponse.HeroBlock("你的生活小账本", "饭点、签到、提醒，都收在这一页。"));
        return response;
    }
}
