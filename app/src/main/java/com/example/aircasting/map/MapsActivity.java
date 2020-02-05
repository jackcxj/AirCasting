package com.example.aircasting.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aircasting.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.aircasting.models.Road;
import com.google.android.gms.common.ConnectionResult;


import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;

    private TextInputLayout _textInputDrop;
    private TextInputLayout _textInputPickup;
    private TextInputEditText _txtEdtDrop;
    private TextInputEditText _txtEdtPickup;
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final int PLAY_SERVICES_REQUEST = 101;
    private static final int REQUEST_PICKUP_LOCATION = 102;
    private static final int REQUEST_DROP_LOCATION = 103;
    //    private Location lastLocation;
//    private Handler _handler = new Handler();
    private LatLng _pickupLatLng, _dropLatLng;
    private Button _airPollutionRouting, _normalRouting, _moreAirPollutionInfo;
    LinearLayout _bottomSheet, _bottomAirPollutionSheet;
    RecyclerView _bottomSheetListView;
    private BottomSheetBehavior _bottomSheetBehavior, _bottomAirPollutionSheetBehaviour;

    //    DirectionsBottomsheetAdapter _adapter;
    LinearLayout _routeLay;
    LinearLayout _routes;
    TextView _noRoutes, _totalKMS, _totalTime;
    ProgressBar _progress;
    List<Polyline> _polylineList = new ArrayList<>();
    List<Polyline> _pollutionLineList = new ArrayList<>();
    private static final int MAP_PADDING = 80;

    //the more info button
//    private TextView btn_icon;
//    private Drawable drawable;

    //parameters of listener for location changes
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1500; /* 1.5 sec */
    private final int MAX_TIME = 10; //
    private int time;

    //the current location
    private double _currentLat, _currentLng; // yuan

    //use current location button
    private ImageButton _use_current_as_pickup;

    Marker _pickupMarker, _dropMarker;

    String TAG = MapsActivity.class.getName();

    //    all roads in the map
    public static ArrayList<Road> _allRoads = new ArrayList<>();

    // total pollution
    private TextView _totalPollution;
    private TextView _totalGreen;
    private TextView _totalYellow;
    private TextView _totalRed;

    //const
    public double GREEN_UPPER_BOUND = 2.0;
    public double YELLOW_UPPER_BOUND = 5.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupUI();
    }

    private void setupUI() {
        _textInputPickup = findViewById(R.id.txt_input_pickup_location);
        _textInputDrop = findViewById(R.id.txt_input_drop_location);
        _txtEdtPickup = findViewById(R.id.pickup_location);
        _txtEdtDrop = findViewById(R.id.drop_location);
//        _routes = findViewById(R.id.routes);
        _noRoutes = findViewById(R.id.no_routes);
        _routeLay = findViewById(R.id.route_lay);
//        _progress = findViewById(R.id.progress);
//
//        _totalKMS = findViewById(R.id.total_kms);
//        _totalTime = findViewById(R.id.total_time);

//        _bottomSheet = findViewById(R.id.directions_sheet);
//        _bottomSheetListView = findViewById(R.id.navigation_directions_list);
//        _bottomSheetListView.setLayoutManager(new LinearLayoutManager(this));


        //yuan the 2 button
//        _airPollutionRouting = findViewById(R.id.air_pollution_routing_button);
//        _normalRouting = findViewById(R.id.normal_routing);
//        _airPollutionRouting.setVisibility(View.GONE);
//        _normalRouting.setVisibility(View.GONE);
//
//        _bottomSheetBehavior = BottomSheetBehavior.from(_bottomSheet);
//        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        _bottomSheetBehavior.setHideable(false);
//        _bottomSheetBehavior.setHideable(true);


        //yuan the bottom air pollution sheet
//        _moreAirPollutionInfo = findViewById(R.id.more_pollution_info);
//        _moreAirPollutionInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                _bottomAirPollutionSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
//                _bottomAirPollutionSheet.setVisibility(View.VISIBLE);
//
//                double totalPollution = 0.0;
//                double greenDistance = 0.0;
//                double yellowDistance = 0.0;
//                double redDistance = 0.0;
//                for (Road road : AStarSearch.pathRoadList) {
//                    totalPollution += road.getPollutionIndex() * road.getDistance();
//                    if (road.getPollutionIndex() < GREEN_UPPER_BOUND) {
//                        greenDistance += road.getDistance();
//                    } else if (road.getPollutionIndex() < YELLOW_UPPER_BOUND) {
//                        yellowDistance += road.getDistance();
//                    } else {
//                        redDistance += road.getDistance();
//                    }
//                }
//                String totalPollutionString = String.format("%.2f", totalPollution);
//                _totalPollution.setText(String.valueOf(totalPollutionString));
//
//                String totalGreenString = String.format("%.2f", greenDistance);
//                _totalGreen.setText(String.valueOf(totalGreenString));
//
//                String totalYellowString = String.format("%.2f", yellowDistance);
//                _totalYellow.setText(String.valueOf(totalYellowString));
//
//                String totalRedString = String.format("%.2f", redDistance);
//                _totalRed.setText(String.valueOf(totalRedString));
//
//
//            }
//        });
//        _bottomAirPollutionSheet = findViewById(R.id.air_pollution_bottom_sheet);
//        _bottomAirPollutionSheetBehaviour = BottomSheetBehavior.from(_bottomAirPollutionSheet);
//        _bottomAirPollutionSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
//        _bottomAirPollutionSheetBehaviour.setHideable(false);
//        _bottomAirPollutionSheetBehaviour.setHideable(true);


        //yuan set onclick listeners
        _txtEdtPickup.setOnClickListener(this);
        _txtEdtDrop.setOnClickListener(this);
//        _routeLay.setOnClickListener((View.OnClickListener) this);
//        _airPollutionRouting.setOnClickListener((View.OnClickListener) this);
//        _normalRouting.setOnClickListener((View.OnClickListener) this);

        //air pollution detail sheet
//        _totalPollution = findViewById(R.id.total_pollution);
//        _totalGreen = findViewById(R.id.total_green_length);
//        _totalYellow = findViewById(R.id.total_yellow_length);
//        _totalRed = findViewById(R.id.total_red_length);

        //more info icon
//        btn_icon = findViewById(R.id.more_info);
//        drawable = getResources().getDrawable(R.drawable.ic_more_info);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        btn_icon.setCompoundDrawables(drawable, null, null, null);
//        btn_icon.setText("More info");
//        btn_icon.setTextSize(15);
//        btn_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MapsActivity.this, MoreInfoActivity.class);
//                startActivity(intent);
//            }
//        });

        //use current location button
        _use_current_as_pickup = findViewById(R.id.use_current_location_pickup);
        _use_current_as_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_pickupMarker != null) {
                    _pickupMarker.remove();
                }
                getCurrentLatlng();
                LatLng currentLatlng = new LatLng(_currentLat, _currentLng);
                _pickupMarker = mMap.addMarker(new MarkerOptions().position(currentLatlng)
                        .title("Pickup")
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(getSmallerSize(R.drawable.origine))));

                _txtEdtPickup.setText("Your Location");
                moveMarker(currentLatlng.latitude, currentLatlng.longitude);
                _pickupLatLng = currentLatlng;

            }
        });
    }

    public void moveMarker(Double lat, Double lng) {
        Log.d(TAG, "moving marker to lat : " + lat + ", lng : " + lng);
        LatLng place = new LatLng(lat, lng);
        // remove if any previous markers
        if (_pickupMarker != null) _pickupMarker.remove();
        _pickupMarker = mMap.addMarker(new MarkerOptions().position(place).title("Pickup")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(getSmallerSize(R.drawable.origine))));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 17));
    }

    public Bitmap getSmallerSize(@DrawableRes int res) {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(res);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    private void getCurrentLatlng() {
        /**
         * get the latlng of the current location.
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (null != lastLocation) {
            _currentLat = lastLocation.getLatitude();
            _currentLng = lastLocation.getLongitude();

        } else {
            regLocationUpdates();
        }
    }

    private void regLocationUpdates() {
        if (!isLocationPermissionGranted()) {
            Log.d(TAG, "regLocationUpdates: the permission is not granted");
            ;
            return;
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        time++;

                        if (null != location) {
                            String latitudeStr, longitudeStr;
                            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

                            _currentLat = lastLocation.getLatitude();
                            _currentLng = lastLocation.getLongitude();
                            return;
                        }

                        if (time == MAX_TIME) {

                            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

                        }
                    }
                });
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override

    public void onLocationChanged(Location location) {

        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (this));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        Intent placeActivity = new Intent(this, PlaceSearchActivity.class);
        switch (v.getId()) {
            case R.id.pickup_location:
                //startActivityForResult(placeActivity, REQUEST_PICKUP_LOCATION);
                findPlace(REQUEST_PICKUP_LOCATION);
                break;
            case R.id.drop_location:
                //startActivityForResult(placeActivity, REQUEST_DROP_LOCATION);
                findPlace(REQUEST_DROP_LOCATION);
                break;
        }
    }

    private void findPlace(int requestId) {
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        try {
//            Intent intent =
//                    new Autocomplete.IntentBuilder(Autocomplete.getPlaceFromIntent())
//                            .build(this);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, requestId);
/*
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, this, 1000).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, this, 1000).show();
            }
        }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Places search activity result");

        //yuan
        if (requestCode == REQUEST_PICKUP_LOCATION || requestCode == REQUEST_DROP_LOCATION) {
            if (resultCode == RESULT_OK) {
                // get place
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "Place: " + place.getName());
                Log.d(TAG, "Place detail: " + place);
                LatLng latlng = getLocationFromAddress(MapsActivity.this, place.getName());

                // set marker for pickup
                if (requestCode == REQUEST_PICKUP_LOCATION) {
                    // clearing pickup marker and adding new Pickup point
                    if (_pickupMarker != null) {
                        _pickupMarker.remove();
                    }
                    Log.d(TAG, "Place location pick: " + place.getName());
                    _pickupMarker = mMap.addMarker(new MarkerOptions().position(latlng)
                            .title("Pickup")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(getSmallerSize(R.drawable.origine))));

                    _pickupLatLng = latlng;
                    moveMarker(latlng.latitude, latlng.longitude);
                    _txtEdtPickup.setText(place.getName());

                } else {

                    // clearing drop marker and adding new drop point
                    if (_dropMarker != null) {
                        _dropMarker.remove();
                    }
                    Log.d(TAG, "Place location drop: " + place.getName());
                    _dropMarker = mMap.addMarker(new MarkerOptions().position(latlng)
                            .title("Drop")
                            .icon(BitmapDescriptorFactory
                                    .fromBitmap(getSmallerSize(R.drawable.destination))));

                    _dropLatLng = latlng;
                    moveMarker(latlng.latitude, latlng.longitude);
                    _txtEdtDrop.setText(place.getName());
                }

                // if both pickup and drop avalible find the Directions
                if (!Utils.isStringEmpty(_txtEdtPickup.getText().toString()) &&
                        !Utils.isStringEmpty(_txtEdtDrop.getText().toString())) {
                    Log.d(TAG, "Calling getPossible routes");

                    // remove ploylines drawn before if any
                    removeAllPolylines();

                    //zoom the camera to the pickup location and end location
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(_pickupLatLng);
                    builder.include(_dropLatLng);
                    LatLngBounds bounds = builder.build();
                    // create the camera with bounds and padding to set into map
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
                    mMap.animateCamera(cu);

//                    _airPollutionRouting.setVisibility(View.VISIBLE);
//                    _airPollutionRouting.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            removeAllPolylines();
//                            GetDirectionApiService.getPossibleDirections(view.getContext(), _pickupLatLng, _dropLatLng,
//                                    getString(R.string.google_maps_key), "AIR");
//                        }
//                    });


//                    _normalRouting.setVisibility(View.VISIBLE);
//                    _normalRouting.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            removeAllPolylines();
//                            GetDirectionApiService.getPossibleDirections(view.getContext(), _pickupLatLng, _dropLatLng,
//                                    getString(R.string.google_maps_key), "NORMAL");
//
//
//                        }
//                    });
                }


            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
//                showNoRoutesFound();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "User canceled the action");
            }
        } else {
            showNoRoutesFound();
        }
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        
        try {
            if(coder.isPresent()){
                // May throw an IOException
                address = coder.getFromLocationName(strAddress, 5);
                Log.d(TAG, "Address: " + address.toString());
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void showNoRoutesFound() {
        Log.d(TAG, "No routes found");
        _routeLay.setVisibility(View.VISIBLE);
        _noRoutes.setVisibility(View.VISIBLE);
//        _progress.setVisibility(View.GONE);
        _routes.setVisibility(View.GONE);
    }

    /**
     * Method remove all polylines from map
     */
    private void removeAllPolylines() {
        if (_polylineList != null) {
            for (Polyline line : _polylineList) {
                line.remove();
            }
            _polylineList.clear();
        }
    }
}

