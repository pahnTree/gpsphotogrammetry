package com.example.philip.mygpsapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.philip.mygpsapp.R;

/**
 * Created by Philip on 11/12/2015.
 */
public class CameraFragment extends Fragment{

    private Context mContext;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        mContext = v.getContext();
        imageView = (ImageView) v.findViewById(R.id.imageView);
        Bitmap bm = BitmapFactory.decodeFile(R.drawable.shutupandtake + ".jpg");
        imageView.setImageBitmap(bm);
        return v;
    }
}
