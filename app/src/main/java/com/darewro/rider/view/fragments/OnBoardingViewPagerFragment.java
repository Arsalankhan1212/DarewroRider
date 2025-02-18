package com.darewro.rider.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.darewro.rider.R;

public class OnBoardingViewPagerFragment extends Fragment {

    private String path;

    private ImageView imageView;

    public static OnBoardingViewPagerFragment newInstance(int path) {
        OnBoardingViewPagerFragment fragment = new OnBoardingViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt("image", path);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            int path = bundle.getInt("image");
                try {
                    imageView.setImageResource(path);
//                    Glide.with(getActivity()).asBitmap().load(getResources().getDrawable(path)).into(imageView);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
        }

    }

}
