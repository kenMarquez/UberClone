package mx.ken.devf.uper.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import mx.ken.devf.uper.Async.GeocoderAsyncTask;
import mx.ken.devf.uper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutocompleteFragment extends Fragment implements AdapterView.OnItemClickListener {


    private AutoCompleteTextView autocopletePlaces;
    public static final String ARG_PARAM1 = "param1";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyAXHB4cV21VQR0s8CkcLiisbD-q9rXSnUA";

    private onAddressRecived mAddressListener;
    private String mArgumentAddress;

    public AutocompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_autocomplete, container, false);

        autocopletePlaces = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        autocopletePlaces.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.item_list_map_autocomplete));
        autocopletePlaces.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAddressListener = (onAddressRecived) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " class dont implements onAddressRecived");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddressListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);

        String str = (String) parent.getItemAtPosition(position);

        //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        if (mAddressListener != null) {
            //mAddressListener.onAddressLoaded(str);
            //getActivity().getSupportFragmentManager().popBackStackImmediate();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .replace(R.id.container, MapFragmentUper.newInstance(str, ""))
                    .commit();
        }
    }

    public interface onAddressRecived {
        public void onAddressLoaded(String recived);
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (position != (resultList.size() - 1))
                view = inflater.inflate(R.layout.item_list_map_autocomplete, null);
            else
                view = inflater.inflate(R.layout.autocomplete_google_logo, null);


            if (position != (resultList.size() - 1)) {
                TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
                autocompleteTextView.setText(resultList.get(position));
            } else {
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                // not sure what to do <img src="http://codetheory.in/wp-includes/images/smilies/icon_biggrin.gif" alt=":D" class="wp-smiley">
            }

            return view;
        }

    }


    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&components=country:mx");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.i("myLog", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("myLog", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("myLog", "Cannot process JSON results", e);
        }
        if (resultList.size() > 0)
            Log.i("myLog", "cad =" + resultList.get(0));
        return resultList;
    }
}
