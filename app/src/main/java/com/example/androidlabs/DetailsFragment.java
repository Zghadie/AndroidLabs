package com.example.androidlabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_details, container, false);

        Bundle data = getArguments();

        if (data != null) {
            TextView nameText = result.findViewById(R.id.nameText);
            TextView heightText = result.findViewById(R.id.heightText);
            TextView massText = result.findViewById(R.id.massText);

            nameText.setText(data.getString("name"));
            heightText.setText(data.getString("height"));
            massText.setText(data.getString("mass"));
        }

        return result;
    }
}