package fr.corentinbringer.endunav.views;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import java.util.List;

import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.FragmentMapBinding;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_WALKING;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MapFragment extends Fragment implements MapboxMap.OnMapClickListener, PermissionsListener
{
    private MapboxMap mapboxMap;
    private MapView mapView;
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private Button button;

    SessionManager sessionManager;
    private FragmentMapBinding b;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mapView = b.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            mapboxMap = map;
            mapboxMap.setStyle(Style.OUTDOORS, style -> {
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                mapboxMap.addOnMapClickListener(MapFragment.this);

                button = b.startButton;
                button.setOnClickListener(v -> {
                    boolean simulateRoute = true;
                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                            .directionsRoute(currentRoute)
                            .shouldSimulateRoute(simulateRoute)
                            .build();
                    NavigationLauncher.startNavigation(getActivity(), options);
                });
            });
        });
    }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle)
    {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");

        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );

        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                             locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);

        button.setEnabled(true);
        button.setBackgroundResource(R.color.orange);

        return true;
    }


    private void getRoute(Point origin, Point destination)
    {
        NavigationRoute.builder(getContext())
                       .accessToken(Mapbox.getAccessToken())
                       .origin(origin)
                       .destination(destination)
                       .profile(PROFILE_WALKING)
                       .build()
                       .getRoute(new Callback<DirectionsResponse>() {
                           @Override
                           public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                               // You can get the generic HTTP info about the response
                               Log.d(TAG, "Response code: " + response.code());
                               if (response.body() == null) {
                                   Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                   return;
                               } else if (response.body().routes().size() < 1) {
                                   Log.e(TAG, "No routes found");
                                   return;
                               }

                               currentRoute = response.body().routes().get(0);

                               // Draw the route on the map
                               if (navigationMapRoute != null) {
                                   navigationMapRoute.removeRoute();
                               } else {
                                   navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                               }
                               navigationMapRoute.addRoute(currentRoute);
                           }

                           @Override
                           public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                               Log.e(TAG, "Error: " + throwable.getMessage());
                           }
                       });
    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle)
    {
        //Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            //Get an instance of the LocationComponent.
            locationComponent = mapboxMap.getLocationComponent();
            //Activate the LocationComponent
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
            //Enable the LocationComponent so that it's actually visible on the map
            locationComponent.setLocationComponentEnabled(true);
            //Set the LocationComponent's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            //Set the LocationComponent's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
            //Center camera
            double latitude = mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude();
            double longitude = mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude();

            //double latitude = 44.07017678895298;
            //double longitude = 3.058430097282878;

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(8)
                    .build();

            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted) {
            //enableLocationComponent(mapboxMap.getStyle());
            mapboxMap.getStyle(style -> enableLocationComponent(style));
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();

        //Check if user is signed
        try {
            if (sessionManager.checkLogin()) {
                //navController.navigate(R.id.action_addPicturesStolenMotorcyclesFragment_to_loginFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}