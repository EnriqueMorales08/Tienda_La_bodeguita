package com.example.tienda.ui.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tienda.R;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private HashMap<String, Polyline> rutaPolylines = new HashMap<>();
    private GoogleMap mMap;
    private LatLng tiendaa;
    private LatLng miUbicacion; // Variable para almacenar la ubicación actual
    private Button mTypeBtn;
    private Button miUbicacionBtn;
    private Button obtenerRutaBtn;
    private Button modoAutoBtn;
    private Button modoBicicletaBtn;
    private Button modoCaminarBtn;
    private Marker miUbicacionMarker;
    private final boolean[] ocultar = {true};
    private HashMap<String, List<LatLng>> rutaCache = new HashMap<>();
    private String selectedTransportMode = "driving";
    private static final float DISTANCIA_UMBRAL = 10.0f;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mTypeBtn = view.findViewById(R.id.btnSatelite);
        mTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ocultar[0]) {
                    mTypeBtn.setText("Normal");
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    ocultar[0] = false;
                } else {
                    mTypeBtn.setText("Satelite");
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    ocultar[0] = true;
                }
            }
        });

        miUbicacionBtn = view.findViewById(R.id.btnMiUbicacion);
        miUbicacionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocalizacion();
            }
        });


        obtenerRutaBtn = view.findViewById(R.id.btnObtenerRuta);
        obtenerRutaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ocultar[0]) {
                    modoAutoBtn.setVisibility(View.VISIBLE);
                    modoBicicletaBtn.setVisibility(View.VISIBLE);
                    modoCaminarBtn.setVisibility(View.VISIBLE);
                    ocultar[0] = false;
                } else {
                    obtenerRutaOptima();
                    modoAutoBtn.setVisibility(View.GONE);
                    modoBicicletaBtn.setVisibility(View.GONE);
                    modoCaminarBtn.setVisibility(View.GONE);
                    ocultar[0] = true;
                }
            }
        });

        modoAutoBtn = view.findViewById(R.id.btnAuto);
        modoAutoBtn.setVisibility(View.GONE);
        modoAutoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTransportMode = "driving";
                obtenerRutaOptima();
            }
        });

        modoBicicletaBtn = view.findViewById(R.id.btnBicicleta);
        modoBicicletaBtn.setVisibility(View.GONE);
        modoBicicletaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTransportMode = "transit";
                obtenerRutaOptima();
            }
        });

        modoCaminarBtn = view.findViewById(R.id.btnCaminar);
        modoCaminarBtn.setVisibility(View.GONE);
        modoCaminarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTransportMode = "walking";
                obtenerRutaOptima();
            }
        });

        return view;
    }

    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Permiso necesario")
                        .setMessage("La aplicación necesita acceder a la ubicación para mostrar su posición actual.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            // Mostrar cuadro de diálogo para solicitar permisos
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            // Permiso ya concedido, obtener la ubicación
            ubicacionActual(mMap);
        }
    }

    private void marcador(GoogleMap googleMap) {
        mMap = googleMap;
        tiendaa = new LatLng(-5.195880, -80.635402);

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(tiendaa.latitude, tiendaa.longitude, 1);
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                mMap.addMarker(new MarkerOptions().position(tiendaa).title("La Bodeguita").snippet(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tiendaa, 20), 4000, null);
    }

    private void ubicacionActual(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                actualizarMarcadorUbicacionActual(miUbicacion);

                // Obtener la distancia entre la ubicación actual y el destino
                float distanciaDestino = calcularDistancia(miUbicacion.latitude, miUbicacion.longitude, tiendaa.latitude, tiendaa.longitude);

                // Verificar si se llegó al destino (dentro de la distancia umbral)
                if (distanciaDestino <= DISTANCIA_UMBRAL) {
                    // Mostrar mensaje o notificación indicando que se llegó al destino
                    Toast.makeText(requireContext(), "¡Has llegado al destino!", Toast.LENGTH_LONG).show();
                }
            }
            private float calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
                int radioTierra = 6371000; // Radio medio de la Tierra en metros (aproximadamente 6371 km)
                double dLat = Math.toRadians(lat2 - lat1);
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                float distancia = (float) (radioTierra * c); // Distancia en metros
                return distancia;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void actualizarMarcadorUbicacionActual(LatLng miUbicacion) {
        if (miUbicacion == null) return;

        if (mMap == null) return;

        // Eliminar el marcador actual de ubicación si existe
        if (miUbicacionMarker != null) {
            miUbicacionMarker.remove();
        }

        // Agregar un nuevo marcador para la ubicación actual
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(miUbicacion.latitude, miUbicacion.longitude, 1);
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                miUbicacionMarker = mMap.addMarker(new MarkerOptions()
                        .position(miUbicacion)
                        .title("Ubicación actual")
                        .snippet(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mover la cámara a la ubicación actual
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16), 4000, null);
    }

    private String generarCacheKey(LatLng miUbicacion, String mode) {
        return miUbicacion.hashCode() + "_" + mode;
    }

    private void obtenerRutaOptima() {
        if (miUbicacion == null) {
            Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
            return;
        }

        String cacheKey = generarCacheKey(miUbicacion, selectedTransportMode);
        if (rutaCache.containsKey(cacheKey)) {
            List<LatLng> puntosRuta = rutaCache.get(cacheKey);
            dibujarRutaEnMapa(puntosRuta);
            obtenerDirecciones(miUbicacion, tiendaa, selectedTransportMode);
            if (rutaPolylines.containsKey(selectedTransportMode)) {
                Polyline polylineToRemove = rutaPolylines.get(selectedTransportMode);
                if (polylineToRemove != null) {
                    polylineToRemove.remove();
                }
            }
            return;
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                miUbicacion.latitude + "," + miUbicacion.longitude + "&destination=" +
                tiendaa.latitude + "," + tiendaa.longitude +
                "&key=AIzaSyB6XkWUXX-dPWeZVst6xMUCbF34PN8hKQI&mode=" + selectedTransportMode;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject polyline = route.getJSONObject("overview_polyline");
                            String encodedPolyline = polyline.getString("points");

                            List<LatLng> puntosRuta = decodificarPolyline(encodedPolyline);
                            dibujarRutaEnMapa(puntosRuta);
                            obtenerDirecciones(miUbicacion, tiendaa, selectedTransportMode);

                            // Almacenar la ruta en caché
                            rutaCache.put(cacheKey, puntosRuta);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);

        // Animación de la cámara
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16), 4000, null);
    }

    private void obtenerDirecciones(LatLng miUbicacion, LatLng tiendaa, String selectedTransportMode) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                miUbicacion.latitude + "," + miUbicacion.longitude + "&destination=" +
                tiendaa.latitude + "," + tiendaa.longitude +
                "&key=AIzaSyB6XkWUXX-dPWeZVst6xMUCbF34PN8hKQI&mode=" + selectedTransportMode;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject legs = route.getJSONArray("legs").getJSONObject(0);
                            String distancia = legs.getJSONObject("distance").getString("text");
                            String duracion = legs.getJSONObject("duration").getString("text");

                            // Mostrar información de la ruta en un cuadro de diálogo
                            mostrarDirecciones(miUbicacion, tiendaa, selectedTransportMode, distancia, duracion);

                            // Obtener los puntos de la ruta y dibujarla en el mapa
                            List<LatLng> puntosRuta = decodificarPolyline(route.getJSONObject("overview_polyline").getString("points"));
                            dibujarRutaEnMapa(puntosRuta);

                            // Almacenar la ruta en caché si es necesario
                            String cacheKey = generarCacheKey(miUbicacion, selectedTransportMode);
                            rutaCache.put(cacheKey, puntosRuta);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    private List<LatLng> decodificarPolyline(String encodedPolyline) {
        List<LatLng> puntosRuta = new ArrayList<>();
        int index = 0;
        int lat = 0, lng = 0;

        while (index < encodedPolyline.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat * 1e-5;
            double longitude = lng * 1e-5;
            LatLng punto = new LatLng(latitude, longitude);
            puntosRuta.add(punto);
        }

        return puntosRuta;
    }

    private void dibujarRutaEnMapa(List<LatLng> puntosRuta) {
        // Eliminar polilínea anterior
        if (!rutaPolylines.isEmpty()) {
            for (Polyline polyline : rutaPolylines.values()) {
                polyline.remove();
            }
            rutaPolylines.clear();
        }
        // Dibujar nueva polilínea
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(puntosRuta)
                .color(Color.GREEN)
                .width(4);
        Polyline rutaPolyline = mMap.addPolyline(polylineOptions);
        rutaPolylines.put(selectedTransportMode, rutaPolyline);
    }
    private void mostrarDirecciones(LatLng origen, LatLng destino, String selectedTransportMode, String distancia, String duracion) {
        String direccionOrigen = obtenerDireccion(origen);
        String direccionDestino = obtenerDireccion(destino);

        if (direccionOrigen != null && direccionDestino != null) {
            String transporte = "";
            switch (selectedTransportMode) {
                case "driving":
                    transporte = "Auto";
                    break;
                case "transit":
                    transporte = "Combi";
                    break;
                case "walking":
                    transporte = "Caminando";
                    break;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Direcciones de ruta");
            builder.setMessage("Origen: " + direccionOrigen + "\nDestino: " + direccionDestino +
                    "\nEn " + transporte + "\nDistancia: " + distancia + "\nDuración: " + duracion);
            builder.setPositiveButton("Aceptar", null);
            builder.show();
        } else {
            Toast.makeText(requireContext(), "Error al obtener las direcciones", Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerDireccion(LatLng latLng) {
        String direccion = null;
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addresses.isEmpty()) {
                direccion = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direccion;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marcador(googleMap);
    }
}
