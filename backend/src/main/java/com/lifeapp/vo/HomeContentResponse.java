package com.lifeapp.vo;

public class HomeContentResponse {

    private QuoteBlock quote;
    private HeroBlock homeHero;
    private HeroBlock foodHero;
    private HeroBlock checkinHero;
    private HeroBlock mineHero;

    public QuoteBlock getQuote() {
        return quote;
    }

    public void setQuote(QuoteBlock quote) {
        this.quote = quote;
    }

    public HeroBlock getHomeHero() {
        return homeHero;
    }

    public void setHomeHero(HeroBlock homeHero) {
        this.homeHero = homeHero;
    }

    public HeroBlock getFoodHero() {
        return foodHero;
    }

    public void setFoodHero(HeroBlock foodHero) {
        this.foodHero = foodHero;
    }

    public HeroBlock getCheckinHero() {
        return checkinHero;
    }

    public void setCheckinHero(HeroBlock checkinHero) {
        this.checkinHero = checkinHero;
    }

    public HeroBlock getMineHero() {
        return mineHero;
    }

    public void setMineHero(HeroBlock mineHero) {
        this.mineHero = mineHero;
    }

    public static class QuoteBlock {

        private String text;
        private String source;

        public QuoteBlock() {
        }

        public QuoteBlock(String text, String source) {
            this.text = text;
            this.source = source;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class HeroBlock {

        private String title;
        private String subtitle;
        private String emptyTip;

        public HeroBlock() {
        }

        public HeroBlock(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public HeroBlock(String title, String subtitle, String emptyTip) {
            this.title = title;
            this.subtitle = subtitle;
            this.emptyTip = emptyTip;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getEmptyTip() {
            return emptyTip;
        }

        public void setEmptyTip(String emptyTip) {
            this.emptyTip = emptyTip;
        }
    }
}
