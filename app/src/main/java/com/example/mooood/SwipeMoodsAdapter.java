//===================================================================================
// Reference for implementation: https://github.com/haerulmuttaqin/SwipeViewPager
//====================================================================================
package com.example.mooood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SwipeMoodsAdapter extends PagerAdapter {

    //params
    private List<Integer> moodImages;
    private Context context;

    //for later use
    private LayoutInflater layoutInflater;

    //constructor
    public SwipeMoodsAdapter(List<Integer> moodImages, Context context){
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

        ImageView imageView;
        imageView = view.findViewById(R.id.image);
        imageView.setImageResource(moodImages.get(position));

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
