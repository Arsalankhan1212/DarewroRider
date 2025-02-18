package com.darewro.rider.view.fragments;


import static android.os.Build.VERSION_CODES.M;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.darewro.rider.R;
import com.darewro.rider.data.api.models.allOrders.AllOrder;
import com.darewro.rider.view.models.NearestOrder;
import com.darewro.rider.view.models.Order;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends BaseFragment {

    List<NearestOrder> orders = null;

    private static MapFragment instance = null;
    private MapView osm;
    private MapController mc;
    private static final int PERMISSAO_REQUERIDA = 1;

    public static MapFragment getInstance() {
        if (instance == null) {
            instance = new MapFragment();
        }
        return instance;
    }

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissoes, PERMISSAO_REQUERIDA);
            }
        }
        //onde mostra a imagem do mapa
        Context ctx = getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        osm = (MapView) view.findViewById(R.id.mapview);
        osm.setTileSource(TileSourceFactory.MAPNIK);
        osm.setBuiltInZoomControls(true);
        osm.setMultiTouchControls(true);

        mc = (MapController) osm.getController();
        mc.setZoom(14);

        GeoPoint center = new GeoPoint(34.0151, 71.5249);
        mc.animateTo(center);
        //addMarker(center);

        // locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        /* grade no mapa
        LatLonGridlineOverlay2 overlay = new LatLonGridlineOverlay2();
        osm.getOverlays().add(overlay);
        */

        osm.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                Log.i("Script", "onScroll()");
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                Log.i("Script", "onZoom()");
                return false;
            }
        });
        return view;
    }

    public void addMarker(GeoPoint center) {

        if (osm != null) {
            Marker marker = new Marker(osm);
            marker.setPosition(center);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(getResources().getDrawable(R.drawable.payment));
//        osm.getOverlays().clear();
//        osm.getOverlays().add(marker);
//        osm.invalidate();
            marker.setTitle("");

            List<GeoPoint> circle = Polygon.pointsAsCircle(center, 5000);
            Polygon p = new Polygon();
            p.setPoints(circle);
            p.setFillColor(Color.parseColor("#66FFD1DC"));
            p.setStrokeWidth(0.5f);
            p.setStrokeColor(Color.parseColor("#D288A2"));
            p.setTitle("");
            osm.getOverlays().add(p);
            osm.getOverlays().add(marker);
            osm.invalidate();
        }
    }

    @Override
    public void initializeViews(View view) {

        setListeners();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void setData(List<Order> orders) {

    }

    @Override
    public void setData(boolean isAllOrder, List<AllOrder> orders) {

    }

    public void setData(List<NearestOrder> orders, boolean nearest) {

        this.orders = new ArrayList<>();
        if (orders.size() > 0) {
            this.orders = orders;

            //if(googleMap!=null){
            if (orders != null && orders.size() > 0) {
                for (int i = 0; i < orders.size(); i++) {

                    GeoPoint point = new GeoPoint(Double.parseDouble(orders.get(i).getLatitude()), Double.parseDouble(orders.get(i).getLongitude()));
                    addMarker(point);
                    //googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(orders.get(i).getLatitude()), Double.parseDouble(orders.get(i).getLongitude()))));
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(orders.get(i).getLatitude()), Double.parseDouble(orders.get(i).getLongitude()))).zoom(ZOOM_LEVEL).build();
                    if (mc != null)
                        mc.animateTo(point);
                }
            } else {
                GeoPoint center = new GeoPoint(34.0151, 71.5249);
                addMarker(center);
                if (mc != null)
                    mc.animateTo(center);
            }
            //}
        }
    }

    public void setError() {
//        if (swipeRefreshLayout != null) {
//            swipeRefreshLayout.setRefreshing(false);
//        }

    }

}
