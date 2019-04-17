package com.nielsmasdorp.speculum.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nielsmasdorp.speculum.R;
import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.TravelRoute;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteView extends FrameLayout {
    @BindView(R.id.route_1_start) TextView tvRoute1Start;
    @BindView(R.id.route_1_details) TextView tvRoute1Details;
    @BindView(R.id.route_1_end) TextView tvRoute1End;
    @BindView(R.id.route_2_start) TextView tvRoute2Start;
    @BindView(R.id.route_2_details) TextView tvRoute2Details;
    @BindView(R.id.route_2_end) TextView tvRoute2End;
    @BindView(R.id.route_3_start) TextView tvRoute3Start;
    @BindView(R.id.route_3_details) TextView tvRoute3Details;
    @BindView(R.id.route_3_end) TextView tvRoute3End;
    @BindView(R.id.route_4_start) TextView tvRoute4Start;
    @BindView(R.id.route_4_details) TextView tvRoute4Details;
    @BindView(R.id.route_4_end) TextView tvRoute4End;

    @BindView(R.id.tv_route_last_updated) TextView tvLastUpdated;

    public RouteView(Context context) {
        super(context);
        init();
    }

    public RouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RouteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.route_view, this);
        ButterKnife.bind(this);
    }

    public void updateRoutes(TravelDetails routeList) {
        SimpleDateFormat format = new SimpleDateFormat("H:mm", Locale.getDefault());

        TextView[] startViews = { tvRoute1Start, tvRoute2Start, tvRoute3Start, tvRoute4Start };
        TextView[] detailViews = { tvRoute1Details, tvRoute2Details, tvRoute3Details, tvRoute4Details };
        TextView[] endViews = { tvRoute1End, tvRoute2End, tvRoute3End, tvRoute4End };

        for (int i = 0; i < 4; i++) {
            if (i >= routeList.getRoutes().size()) {
                startViews[i].setText("");
                detailViews[i].setText("");
                endViews[i].setText("");
            } else {
                TravelRoute travelRoute = routeList.getRoutes().get(i);

                String text = format.format(travelRoute.getDepartureTime()) + " \uD83D\uDE8C " + travelRoute.getFirstBus();
                startViews[i].setText(text);
                detailViews[i].setText(travelRoute.getRoute());
                detailViews[i].setSelected(true);
                endViews[i].setText(format.format(travelRoute.getArrivalTime()));
            }
        }

        String text = "Route to " + routeList.getDestination() + "; Last updated: " + format.format(routeList.getLastUpdated());
        tvLastUpdated.setText(text);
    }
}
