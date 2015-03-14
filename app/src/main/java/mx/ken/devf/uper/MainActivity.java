package mx.ken.devf.uper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.ken.devf.uper.Fragments.AutocompleteFragment;
import mx.ken.devf.uper.Fragments.EjemploFragment;
import mx.ken.devf.uper.Fragments.MapFragmentUper;
import mx.ken.devf.uper.Fragments.PaymentFragment;
import mx.ken.devf.uper.Fragments.ProfileFragment;
import mx.ken.devf.uper.Fragments.PromotionsFragment;
import mx.ken.devf.uper.Fragments.ShareFragment;
import mx.ken.devf.uper.Fragments.SupportFragment;
import mx.ken.devf.uper.Interfaces.GeocoderCallBack;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, EjemploFragment.OnFragmentInteractionListener, AutocompleteFragment.onAddressRecived,
        GeocoderCallBack {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static final String PREFS_NAM = "MyPreferences";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        retriveData();
//        ParseObject creditCard = new ParseObject(getString(R.string.key_credit_card_object));
//        creditCard.put(getString(R.string.key_no_tarjeta), "1287877677797");
//        creditCard.put(getString(R.string.key_aa), "12");
//        creditCard.put(getString(R.string.key_mm), "21");
//        creditCard.put(getString(R.string.key_country), getString(R.string.countrt));
//        creditCard.put(getString(R.string.key_cvv), "123");
//        creditCard.put(getString(R.string.key_cp), "123");
//        creditCard.put(getString(R.string.relation_card_user), ParseUser.getCurrentUser());
//        creditCard.saveInBackground();


    }

    private void retriveData() {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.loading_info));
        dialog.show();
        String savedInfo = getPreference(getString(R.string.key_saved_info), getString(R.string.key_notsaved_info));
        if (savedInfo.equals(getString(R.string.key_notsaved_info))) {

            ParseUser parseUser = ParseUser.getCurrentUser();
            savePreference(getString(R.string.key_first_name), parseUser.getString(getString(R.string.key_first_name)));
            savePreference(getString(R.string.key_last_name), parseUser.getString(getString(R.string.key_last_name)));
            savePreference(getString(R.string.key_mail), parseUser.getEmail());
            savePreference(getString(R.string.key_tel), parseUser.getString(getString(R.string.key_tel)));

            ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.key_credit_card_object));
            query.whereEqualTo("parent", ParseUser.getCurrentUser());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    dialog.dismiss();
                    Set<String> set = new HashSet<String>();
                    if (e == null) {

                        for (int i = 0; i < parseObjects.size(); i++) {
                            ParseObject object = parseObjects.get(i);
                            String creditCard = object.getString(getString(R.string.key_no_tarjeta)) + "," +
                                    object.getString(getString(R.string.key_aa)) + "," +
                                    object.getString(getString(R.string.key_mm)) + "," +
                                    object.getString(getString(R.string.key_cvv)) + "," +
                                    object.getString(getString(R.string.key_cp));
                            set.add(creditCard);
                        }
                        //TODO quitar esta mierda
                        SharedPreferences preferences = getSharedPreferences(PREFS_NAM, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(getString(R.string.key_credit_card_set), set);
                        editor.putString(getString(R.string.key_saved_info), getString(R.string.saved_info));
                        editor.commit();

                    } else {
                        SharedPreferences preferences = getSharedPreferences(PREFS_NAM, MODE_PRIVATE);
                        set.add(preferences.getString(getString(R.string.key_credit_default), "1111111"));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(getString(R.string.key_credit_card_set), set);
                        editor.putString(getString(R.string.key_saved_info), getString(R.string.saved_info));
                        editor.commit();
                    }
                }
            });
        } else {
            dialog.dismiss();//ya ha tomado la informacion
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                transaction
                        .replace(R.id.container, new MapFragmentUper(R.id.container, fragmentManager))
                        .addToBackStack("map")
                        .commit();
                break;
            case 1:
                transaction
                        .replace(R.id.container, ProfileFragment.newInstance("", ""))
                        .addToBackStack("profile")
                        .commit();
                break;
            case 2:
                invalidateOptionsMenu();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PaymentFragment.newInstance("", ""))
                        .addToBackStack("map")
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PromotionsFragment.newInstance("", ""))
                        .addToBackStack("promotions")
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ShareFragment.newInstance("", ""))
                        .addToBackStack("share")
                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SupportFragment.newInstance("", ""))
                        .addToBackStack("support")
                        .commit();
                break;
            case 6:
                fragmentManager.beginTransaction()
                        //.replace(R.id.container, AboutFragment.newInstance("a", "al"))
                        .replace(R.id.container, EjemploFragment.newInstance("arg1", "arg2"))
                        .addToBackStack("about")
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();
        } else if (fragmentManager.getBackStackEntryCount() <= 1) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String cad) {
        Log.i("myLog", "listener :" + cad);
    }

    @Override
    public void onAddressLoaded(String recived) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction
//                .replace(R.id.container, MapFragmentUper.newInstance(recived, ""))
//                .commit();
    }

    @Override
    public void onResponseSuccess(LatLng latitud) {
        Log.i("myLog", "succes");
    }

    @Override
    public void onError() {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void clicked(View view) {
        Log.i("myLog", "clicked");
    }

    public void savePreference(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
        String name = preferences.getString(key, defaultValue);
        return name;
    }


}
