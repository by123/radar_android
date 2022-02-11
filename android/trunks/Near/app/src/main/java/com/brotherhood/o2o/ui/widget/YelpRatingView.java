package com.brotherhood.o2o.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.util.ViewUtil;

/**
 * Created by laimo.li on 2016/1/22.
 */
public class YelpRatingView extends FrameLayout {

    private ImageView ivYelpRating;
    private ImageView ivIcon;
    private TextView tvReviews;

    public YelpRatingView(Context context) {
        super(context, null);
    }

    public YelpRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.yelp_rating_view, this);
        ivYelpRating = (ImageView) findViewById(R.id.ivYelpRating);
        ivIcon = (ImageView) findViewById(R.id.ivIcon);
        tvReviews = (TextView) findViewById(R.id.tvReviews);
        setVisibility(View.GONE);
    }

    public void rating(String rating) {
        if (TextUtils.isEmpty(rating)) {
            setVisibility(View.GONE);
            return;
        }
        rating(Float.valueOf(rating));
    }

    public void rating(float rating) {
        ivYelpRating.setImageResource(getYelpRatingImage(rating));
        setVisibility(VISIBLE);
    }

    public void rating(String rating, boolean showIcon) {
        rating(rating);
        if (showIcon) {
            ViewUtil.toggleView(ivIcon, true);
        } else {
            ViewUtil.toggleView(ivIcon, false);
        }
    }

    public void reviews(String reviews) {
        if (TextUtils.isEmpty(reviews)) {
            return;
        }
        if (Integer.valueOf(reviews) > 99) {
            reviews = "99+";
        }
        tvReviews.setText(getContext().getString(R.string.oversea_food_detail_reviews, reviews));
    }

    public int getYelpRatingImage(float rating) {
        String imageName = "0";
        if (rating == 1) {
            imageName = "1";
        } else if (rating == 1.5) {
            imageName = "1_5";
        } else if (rating == 2) {
            imageName = "2";
        } else if (rating == 2.5) {
            imageName = "2_5";
        } else if (rating == 3) {
            imageName = "3";
        } else if (rating == 3.5) {
            imageName = "3_5";
        } else if (rating == 4) {
            imageName = "4";
        } else if (rating == 4.5) {
            imageName = "4_5";
        } else if (rating == 5) {
            imageName = "5";
        }
        return getContext().getResources().getIdentifier("yelp_rating_" + imageName, "mipmap", getContext().getPackageName());
    }


}
