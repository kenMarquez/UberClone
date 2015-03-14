package mx.ken.devf.uper.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import mx.ken.devf.uper.R;
import mx.ken.devf.uper.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String PREFS_NAM = "MyPreferences";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logout = (Button) v.findViewById(R.id.logout_button);
        TextView name = (TextView) v.findViewById(R.id.name_tv);
        TextView lastName = (TextView) v.findViewById(R.id.lata_name_text_view);
        TextView mail = (TextView) v.findViewById(R.id.mail_tv);
        TextView tel = (TextView) v.findViewById(R.id.tel_tv);


        name.setText(getPreference(getString(R.string.key_first_name), "Default"));
        lastName.setText(getPreference(getString(R.string.key_last_name), "Default"));
        mail.setText(getPreference(getString(R.string.key_mail), "Default"));
        tel.setText(getPreference(getString(R.string.key_tel), "Default"));


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser parseUser = ParseUser.getCurrentUser();
                if (parseUser != null) {
                    parseUser.logOut();
                    SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.key_saved_info), getString(R.string.key_notsaved_info));
                    editor.commit();
                    startActivity(new Intent(getActivity(), WelcomeActivity.class));
                }
            }
        });


        return v;
    }


    public void savePreference(String key, String value) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
        String name = preferences.getString(key, defaultValue);
        return name;
    }


}
