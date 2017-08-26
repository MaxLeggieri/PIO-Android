package com.livelife.pioalert;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Max on 27/05/2017.
 */

public class ScrollMapView extends MapView {
    public ScrollMapView(Context context) {
        super(context);


    }


    public ScrollMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    public ScrollMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);

    }


    /*
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }
    */



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }



}
