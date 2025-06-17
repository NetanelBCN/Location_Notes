package dev.netanelbcn.locationnotes.fragmentViews.map;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import dev.netanelbcn.locationnotes.databinding.FragmentMapBinding;
import dev.netanelbcn.locationnotes.models.NoteItem;
import dev.netanelbcn.locationnotes.utilities.DataManager;
import dev.netanelbcn.locationnotes.views.Note_Screen;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap myGoogleMap;
    public final double DEFAULT_LAT = 31.771959;
    public final double DEFAULT_LON = 35.217018;
    private final LatLng DEFAULT_LOC = new LatLng(DEFAULT_LAT, DEFAULT_LON);
    private MarkerOptions markerOptions;
    private FragmentMapBinding binding;
    private DataManager dataManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postponeEnterTransition();

        //MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataManager = DataManager.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(binding.MapFCVMap.getId());
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.myGoogleMap = googleMap;
        markerOptions = new MarkerOptions();
        this.myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOC, 8));

        if (dataManager != null && dataManager.getNotes() != null) {
            for (NoteItem note : dataManager.getNotes()) {
                LatLng location = note.getNote_location();
                if (location != null) {
                    String title = note.getNote_title();
                    String snippet = getLocationString(location);
                    Marker marker = this.myGoogleMap.addMarker(
                            new MarkerOptions()
                                    .position(location)
                                    .title(title)
                                    .snippet(snippet)
                    );
                    if (marker != null) {
                        marker.setTag(note);
                    }
                }
            }
        }
        this.myGoogleMap.setOnInfoWindowClickListener(marker -> {
            NoteItem note = (NoteItem) marker.getTag();
            if (note != null)
                editNote(note);
        });
    }

    private void editNote(NoteItem item) {
        DataManager.getInstance().setCurrent(item);
        startActivity(new Intent(requireContext(), Note_Screen.class));
    }

    public void focusOnLocation(LatLng location) {
        if (this.myGoogleMap == null) return;

        this.myGoogleMap.clear();
        markerOptions.position(location).title(getLocationString(location));
        this.myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        this.myGoogleMap.addMarker(markerOptions);
    }

    public String getLocationString(LatLng location) {
        Geocoder geoCoder = new Geocoder(requireContext());
        String result = "Unknown location";
        try {
            List<Address> list = geoCoder.getFromLocation(location.latitude, location.longitude, 1);
            if (list != null && !list.isEmpty()) {
                Address address = list.get(0);
                result = address.getLocality() + ", " + address.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void refreshMapMarkers() {
        myGoogleMap.clear();
        for (NoteItem note : dataManager.getNotes()) {
            LatLng location = note.getNote_location();
            if (location != null) {
                String title = note.getNote_title();
                String snippet = getLocationString(location);
                Marker marker = this.myGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(location)
                                .title(title)
                                .snippet(snippet)
                );
                if (marker != null) {
                    marker.setTag(note);
                }
            }
        }
        myGoogleMap.setOnInfoWindowClickListener(marker -> {
            NoteItem note = (NoteItem) marker.getTag();
            if (note != null)
                editNote(note);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (myGoogleMap != null) {
            refreshMapMarkers();
        }
    }

}

