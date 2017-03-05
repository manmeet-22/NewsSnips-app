package com.androidexample.newssnips;

/**
 * Created by MANI on 2/6/2017.
 */

public class News {

    private String mHeadline;

    private String mContent;

    private String mPublishDate;

    private String mUrl;

    private String mImage;

    private String mContributor;

    private String mSection;

    //  private String mStar;

    public News(String headline, String content, String publishDate, String url, String image, String contributor) {

        mHeadline = headline;
        mContent = content;
        mPublishDate = publishDate;
        mUrl = url;
        mImage = image;
        mContributor = contributor;
        //    mSection= section;
        //     mStar= star;
    }


    public String getHeadline() {
        return mHeadline;
    }

    public String getContent() {
        return mContent;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }

    public String getContributor() {
        return mContributor;
    }

    // public String getSection() {return mSection; }

    // public String getStar(){ return mStar; }
}


