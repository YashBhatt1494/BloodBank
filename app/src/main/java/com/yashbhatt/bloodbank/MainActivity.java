package com.yashbhatt.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.yashbhatt.bloodbank.fragments.AddDonorFragment;
import com.yashbhatt.bloodbank.fragments.FindDonorFragment;
import com.yashbhatt.bloodbank.fragments.LoginFragment;
import com.yashbhatt.bloodbank.fragments.ProfileFragment;
import com.yashbhatt.bloodbank.fragments.SignUpFragment;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;
import com.yashbhatt.bloodbank.models.Person;
import com.firebase.geofire.core.GeoHash;
'com.rengwuxian.materialedittext:library:2.1.4'


import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private SessionManager session;
    private TextView tvHeader, tvDesc;

    private FirebaseFirestore mfirestore;
    private GoogleMap mMap;
    // Declare a variable for the cluster manager.
    private ClusterManager<Person> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = ((BloodBank) getApplication()).getSession();
        mfirestore = ((BloodBank) getApplication()).getFirestore();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentBAck(new AddDonorFragment(), AddDonorFragment.TAG);
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getHeaderView(0)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFragmentBAck(new ProfileFragment(), ProfileFragment.TAG);
                        drawer.closeDrawers();
                    }
                });

        tvHeader = navigationView.getHeaderView(0).findViewById(R.id.text_title_name);
        tvDesc = navigationView.getHeaderView(0).findViewById(R.id.text_desc_email);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setProfile();
    }

    private void setProfile() {
        if (session != null) {
            tvHeader.setText(session.getFirstName() + " " + session.getLastName());
            tvDesc.setText(session.getEmail());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (item.getItemId() == R.id.nav_find_donor) {
            item.setVisible(true);
        } else if (item.getItemId() == R.id.nav_settings) {
            item.setVisible(true);
        } else if (item.getItemId() == R.id.nav_home) {
            item.setVisible(true);
        }


        if (id == R.id.nav_find_donor) {
            openFragment(new FindDonorFragment(), FindDonorFragment.TAG);
        } else if (id == R.id.nav_settings) {
            session.clear();
            Intent splashIntent = new Intent(this, SplashActivity.class);
            splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(splashIntent);
        } else if (id == R.id.nav_home) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(Fragment fragment, String tag) {
//                        .addToBackStack(tag)

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, fragment)
                .commit();
    }


    private void openFragmentBAck(Fragment fragment, String tag) {
//                        .addToBackStack(tag)

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        configureMap();
    }

    /**
     * function to get donors
     */
    private void getDonors() {
        mfirestore.collection(Constants.COLLECTION_DONORS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    boolean ismapanimated = false;

                    if (task.getResult() != null) {

                        if (!task.getResult().isEmpty()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                try {

                                    double lat = documentSnapshot.getDouble(Constants.LAT);
                                    double longt = documentSnapshot.getDouble(Constants.LONGT);
                                    String name = documentSnapshot.getString(Constants.PROFILE_FNAME);
                                    String bloodgroup = documentSnapshot.getString(Constants.PROFILE_BLOOD_GROUP);
                                    String email = documentSnapshot.getString(Constants.PROFILE_EMAIL);


                                    String title = name + " wants to donate " + bloodgroup;
                                    String desc = "Contact : " + email;
                                    final Person person = new Person(lat, longt, title, desc);

                                    // Position the map.
                                    if (!ismapanimated) {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longt), 10));
                                        ismapanimated = true;
                                    }
                                    mClusterManager.addItem(person);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }
        });
    }

    private void configureMap() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.map_margin));
        setUpClusterer();
    }

    private class CustomMapClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
        CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
            //start clustering if 2 or more items overlap
            return cluster.getSize() >= 2;
        }

        @Override
        protected void onBeforeClusterItemRendered(T item,
                                                   MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blood_drop));
        }
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<Person>(this, mMap);
        mClusterManager.setAnimation(true);
        CustomMapClusterRenderer renderer = new CustomMapClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        getDonors();

    }

}
