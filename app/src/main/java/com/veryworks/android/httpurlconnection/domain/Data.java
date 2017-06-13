package com.veryworks.android.httpurlconnection.domain;

/**
 * Created by pc on 6/13/2017.
 */

public class Data {
    private SearchPublicToiletPOIService SearchPublicToiletPOIService;

    public SearchPublicToiletPOIService getSearchPublicToiletPOIService () {
        return SearchPublicToiletPOIService;
    }

    public void setSearchPublicToiletPOIService (SearchPublicToiletPOIService SearchPublicToiletPOIService) {
        this.SearchPublicToiletPOIService = SearchPublicToiletPOIService;
    }

    @Override
    public String toString() {
        return "ClassPojo [SearchPublicToiletPOIService = "+SearchPublicToiletPOIService+"]";
    }
}
