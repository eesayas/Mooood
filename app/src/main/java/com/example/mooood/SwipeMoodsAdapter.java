package com.example.mooood;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

/**
 * This is an adapter so that moods can be swipe through in CreateEventActivity
 * Reference for implementation: https://github.com/haerulmuttaqin/SwipeViewPager
 */

public class SwipeMoodsAdapter extends PagerAdapter {

    //params
    private List<Emoticon> moodImages;
    private Context context;

    //for later use
    private LayoutInflater layoutInflater;

    //constructor
    public SwipeMoodsAdapter(List<Emoticon> moodImages, Context context){
        this.moodImages = moodImages;
        this.context = context;
    }

    //necessary according to reference and android norms
    @Override
    public int getCount() {
        return moodImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position){
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.content_swipe_moods, container, false);

        final ImageView imageView;
        imageView = view.findViewById(R.id.image);
        imageView.setAdjustViewBounds(true);
        imageView.setImageResource(moodImages.get(position).getImageLink());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
