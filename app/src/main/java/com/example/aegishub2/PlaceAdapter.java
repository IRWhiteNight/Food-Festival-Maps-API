package com.example.aegishub2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private List<Place> placeList;

    public PlaceAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
    }

    @Override
    public int getCount() {
        return placeList.size();
    }

    @Override
    public Object getItem(int position) {
        return placeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.place_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.place_image);
            holder.nameView = convertView.findViewById(R.id.place_name);
            holder.addressView = convertView.findViewById(R.id.place_address);
            holder.openView = convertView.findViewById(R.id.place_open);
            holder.longitudeLatitudeView = convertView.findViewById(R.id.place_longitude_latitude); // Added
            holder.menuView = convertView.findViewById(R.id.place_menu); // Added
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place place = placeList.get(position);

        holder.nameView.setText(place.getName());
        holder.addressView.setText(place.getAddress());
        holder.openView.setText(place.getOpen());
        holder.longitudeLatitudeView.setText(place.getLongitudeLatitude()); // Added
        holder.menuView.setText(place.getMenu()); // Added

        // Load image asynchronously
        new ImageLoadTask(place.getImgUrl(), holder.imageView).execute();

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView addressView;
        TextView openView;
        TextView longitudeLatitudeView; // Added
        TextView menuView; // Added
    }

    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // Handle image load error, set a placeholder or handle accordingly
                imageView.setImageResource(R.drawable.placeholder_image);
            }
        }
    }
}
