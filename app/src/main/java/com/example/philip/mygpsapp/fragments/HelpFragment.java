package com.example.philip.mygpsapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.philip.mygpsapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Phil on 11/12/2015.
 */
public class HelpFragment extends Fragment{

    private TextView textHelp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        textHelp = (TextView) v.findViewById(R.id.textHelp);
        textHelp.setText(readTXTfile());
        return v;
    }

    private String readTXTfile() {
        InputStream inputStream = getResources().openRawResource(R.raw.help);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while ( i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
