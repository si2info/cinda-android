package info.si2.iista.volunteernetworks.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.AdapterProfile;
import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.apiclient.ItemProfile;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 13/1/16
 * Project: Cinda
 */
public class TrackingContributions extends Fragment {

    public TrackingContributions() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // View
        View view = inflater.inflate(R.layout.recycler, container, false);

        // Extras
        ArrayList<ItemProfile> items = getArguments().getParcelableArrayList("items");

        // RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterProfile adapter = new AdapterProfile(getContext(), items);
        recyclerView.setAdapter(adapter);

        return view;

    }

    /**
     * Instance fragment
     * @return View Fragment
     */
    public static TrackingContributions newInstance(ArrayList<ItemProfile> items) {

        TrackingContributions fragment = new TrackingContributions();

        Bundle args = new Bundle();
        args.putParcelableArrayList("items", items);
        fragment.setArguments(args);

        return fragment;

    }

}
