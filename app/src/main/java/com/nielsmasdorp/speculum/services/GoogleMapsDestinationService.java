package com.nielsmasdorp.speculum.services;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.TravelRoute;
import com.nielsmasdorp.speculum.models.destination.DestinationResponse;
import com.nielsmasdorp.speculum.models.destination.Leg;
import com.nielsmasdorp.speculum.models.destination.Route;
import com.nielsmasdorp.speculum.models.destination.Step;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.util.Tls12SocketFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class GoogleMapsDestinationService {

    private static final String LOG_TAG = "GoogleMapsDS";

    private GoogleMapsApi googleMapsApi;

    public GoogleMapsDestinationService() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(Tls12SocketFactory.returnClient())
                .baseUrl(Constants.GOOGLE_MAPS_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()))
                .build();

        googleMapsApi = retrofit.create(GoogleMapsApi.class);
    }

    public Observable<TravelDetails> getTravelRoutes(DestinationResponse response, String destination) {

        try {
            Log.i(LOG_TAG, "Travel report: " + response.toString());

            List<TravelRoute> travelRoutes = new ArrayList<>();

            if (response.getRoutes() != null) {
                for (Route route : response.getRoutes()) {
                    if (route.getLegs().isEmpty())
                        break;

                    Leg leg = route.getLegs().get(0);
                    String firstBus = "";

                    SpannableStringBuilder routeString = new SpannableStringBuilder();

                    int startPos = 0;

                    for (Step step : leg.getSteps()) {
                        if (step.getTravelMode().equals("TRANSIT")) {
                            routeString.append(step.getTransitDetails().getDepartureStop().getName());
                            routeString.append(" ⇨ ");

                            startPos = routeString.length();
                            routeString.append(intTo24HourTime(step.getTransitDetails().getDepartureTime().getValue()));
                            routeString.setSpan(new StyleSpan(Typeface.BOLD), startPos, routeString.length(), SPAN_INCLUSIVE_EXCLUSIVE);

                            routeString.append(" \uD83D\uDE8C ");

                            startPos = routeString.length();

                            String busName = "?";

                            if (step.getTransitDetails().getLine() != null) {
                                if (step.getTransitDetails().getLine().getShortName() != null) {
                                    busName = step.getTransitDetails().getLine().getShortName();
                                } else if (step.getTransitDetails().getLine().getName() != null) {
                                    busName = step.getTransitDetails().getLine().getName();
                                }
                            }
                            routeString.append(busName);
                            routeString.setSpan(new StyleSpan(Typeface.BOLD), startPos, routeString.length(), SPAN_INCLUSIVE_EXCLUSIVE);

                            routeString.append(" to ");
                            routeString.append(step.getTransitDetails().getHeadsign());
                            routeString.append(" ⇨ ");

                            startPos = routeString.length();
                            routeString.append(intTo24HourTime(step.getTransitDetails().getArrivalTime().getValue()));
                            routeString.setSpan(new StyleSpan(Typeface.BOLD), startPos, routeString.length(), SPAN_INCLUSIVE_EXCLUSIVE);

                            routeString.append(" ");
                            routeString.append(step.getTransitDetails().getArrivalStop().getName());
                            routeString.append(" ⇨ ");
                            if (firstBus.equals("")) {
                                firstBus = busName.split(" ")[0];
                            }
                        } else {
                            routeString.append("\uD83D\uDEB6 ⇨ ");
                        }
                    }
                    routeString.append(destination);
                    travelRoutes.add(new TravelRoute(routeString, firstBus, new Date((long) leg.getDepartureTime().getValue() * 1000), new Date((long) leg.getArrivalTime().getValue() * 1000)));
                }
            }

            Collections.sort(travelRoutes, (a, b) -> a.getArrivalTime().compareTo(b.getArrivalTime()));

            return Observable.just(new TravelDetails(travelRoutes, new Date(System.currentTimeMillis()), destination));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error", e);
            throw e;
        }
    }

    public String intTo24HourTime(int value) {
        return new SimpleDateFormat("H:mm", Locale.getDefault()).format(new Date((long) value * 1000));
    }

    public GoogleMapsApi getApi() {

        return googleMapsApi;
    }

    public interface GoogleMapsApi {

        @GET("directions/json")
        Observable<DestinationResponse> getDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("key") String apiKey, @Query("transit_mode") String transitMode, @Query("alternatives") String alternatives, @Query("transit_routing_preference") String routingPreference);
    }
}
