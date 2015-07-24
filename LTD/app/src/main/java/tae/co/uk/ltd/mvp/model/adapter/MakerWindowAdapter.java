package tae.co.uk.ltd.mvp.model.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import tae.co.uk.ltd.R;

public class MakerWindowAdapter implements InfoWindowAdapter {
    LayoutInflater inflater = null;

    public MakerWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return (null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = inflater.inflate(R.layout.marker_tooltip, null);

        TextView tvTitle = (TextView) popup.findViewById(R.id.title);
        TextView tvSnippet = (TextView) popup.findViewById(R.id.snippet);

        tvTitle.setText(marker.getTitle());
        tvSnippet.setText(marker.getSnippet());

        return (popup);
    }
}
