package com.androidexample.newssnips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by MANI on 2/6/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {


//    private static final String LOCATION_SEPARATOR = " of ";

    public NewsAdapter(Context context , List<News> news){
        super(context,0,news);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the book at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID bookTitle
        TextView titleView = (TextView) listItemView.findViewById(R.id.newsHeadline);
        String  headline= currentNews.getHeadline();
        // Display the Title of the current book in that TextView
        titleView.setText(headline);

        TextView authorView = (TextView) listItemView.findViewById(R.id.newsContent);
        String content = currentNews.getContent();
        authorView.setText(content);
/*

        TextView sectionView = (TextView) listItemView.findViewById(R.id.newsSection);
        String section = currentNews.getSection();
        sectionView.setText(section);
*/

        TextView dopView = (TextView) listItemView.findViewById(R.id.newsPublishDate);
        String dop = currentNews.getPublishDate();
        dopView.setText(dop);

        TextView contriView = (TextView) listItemView.findViewById(R.id.newsContributor);
        String contri = currentNews.getContributor();
        contriView.setText(contri);
/*
        RatingBar starView = (RatingBar) listItemView.findViewById(R.id.newsRating);
        String starString = currentNews.getStar();
        float star=Float.valueOf(starString);
        starView.setRating(star);*/

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.newsImageThumb);
        Picasso.with(getContext()).load(currentNews.getImage()).into(imageView);

        return listItemView;
    }

}
