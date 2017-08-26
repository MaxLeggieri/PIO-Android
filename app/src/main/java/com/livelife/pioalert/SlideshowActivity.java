package com.livelife.pioalert;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlideshowActivity extends FragmentActivity {

    private ScrollGalleryView scrollGalleryView;
    TextView promoTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        scrollGalleryView = (ScrollGalleryView) findViewById(R.id.scroll_gallery_view);
        scrollGalleryView.setBackgroundColor(0xf6f6f6);

        String promoTitle = getIntent().getStringExtra("promoTitle");
        promoTitleTextView = (TextView) findViewById(R.id.promoTitleTextView);
        promoTitleTextView.setText(promoTitle);

        String images = getIntent().getStringExtra("images");
        String[] imgArr = images.split(",");
        ArrayList<String> ssi = new ArrayList<String>(Arrays.asList(imgArr));


        List<MediaInfo> infos = new ArrayList<>(ssi.size());
        for (String url: ssi) {
            infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(url)));
        }

        int position = getIntent().getIntExtra("position",0);
        Log.v("scrollGalleryView","POSITION: "+position);
        scrollGalleryView
                .setThumbnailSize(200)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos);

        scrollGalleryView.setCurrentItem(position);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
