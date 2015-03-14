package mx.ken.devf.uper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class PaymentActivity extends ActionBarActivity {

    public static final String PREFS_NAM = "MyPreferences";
    private String name;
    private String lastName;
    private String mail;
    private String phone;
    private String password;
    EditText creditCardET;
    EditText aaET;
    EditText mmET;
    EditText cvvET;
    EditText codPostalET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment3);
        creditCardET = (EditText) findViewById(R.id.credit_card_et);
        aaET = (EditText) findViewById(R.id.aa_et);
        mmET = (EditText) findViewById(R.id.mm_et);
        cvvET = (EditText) findViewById(R.id.cvv_Et);
        codPostalET = (EditText) findViewById(R.id.cp_et);
        getUserExtras();
        findViewById(R.id.register_button_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });


    }

    private void validateInputs() {
        final String creditCard;
        final String mm;
        final String aa;
        final String cvv;
        final String codigoPostal;

        boolean error = false;

        creditCard = creditCardET.getText().toString();
        if (creditCard.length() == 0) {
            error = true;
            creditCardET.setError("incorrect field");
        } else {
            creditCardET.setError(null);
        }

        mm = mmET.getText().toString();
        if (mm.length() != 2) {
            error = true;
            mmET.setError("aa could be have 2 numbers");
        } else {
            mmET.setError(null);
        }

        aa = aaET.getText().toString();
        if (aa.length() != 2) {
            error = true;
            aaET.setError("mm could be have 2 numbers");
        } else {
            aaET.setError(null);
        }

        cvv = cvvET.getText().toString();
        if (cvv.length() != 3) {
            error = true;
            cvvET.setError("noooo!!");
        } else {
            cvvET.setError(null);
        }

        codigoPostal = codPostalET.getText().toString();
        if (codigoPostal.length() == 0) {
            error = true;
            codPostalET.setError("noo!! por favor");
        } else {
            codPostalET.setError(null);
        }

        if (!error) {
            final ProgressDialog dialog = new ProgressDialog(PaymentActivity.this);
            dialog.setMessage(getString(R.string.progress_signup));
            dialog.show();

            final ParseUser user = new ParseUser();
            user.setUsername(mail);
            user.setPassword(password);
            user.setEmail(mail);
            user.put(getString(R.string.key_tel), phone);
            user.put(getString(R.string.key_pass), password);
            user.put(getString(R.string.key_first_name), name);
            user.put(getString(R.string.key_last_name), lastName);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if (e != null) {
                        dialog.dismiss();
                        Log.i("myLog", "Error Mail:" + e.toString());
                        Log.i("myLog", "code:" + e.getCode());
                        Log.i("myLog", "tostring:" + e.toString());
                        Log.i("myLog", "message:" + e.getMessage());

                    } else {
                        saveCreditCard(creditCard, aa, mm, cvv, codigoPostal, user, dialog);
                    }

                }
            });

        }
    }

    private void saveCreditCard(String noCard, String aa, String mm, String cvv, String codigoPostal, ParseUser user, ProgressDialog dialog) {
        ParseObject creditCard = new ParseObject(getString(R.string.key_credit_card_object));
        creditCard.put(getString(R.string.key_no_tarjeta), noCard);
        creditCard.put(getString(R.string.key_aa), aa);
        creditCard.put(getString(R.string.key_mm), mm);
        creditCard.put(getString(R.string.key_country), getString(R.string.countrt));
        creditCard.put(getString(R.string.key_cvv), cvv);
        creditCard.put(getString(R.string.key_cp), codigoPostal);
        creditCard.put(getString(R.string.relation_card_user), user);
        savePreference(getString(R.string.key_credit_default), noCard + "," + aa + "," + mm + "," + cvv + "," + codigoPostal);
        dialog.dismiss();

        creditCard.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    changeActivity();
                } else {
                    Log.i("myLog", "code:" + e.getCode());
                    Log.i("myLog", "tostring:" + e.toString());
                    Log.i("myLog", "message:" + e.getMessage());
                    Toast.makeText(PaymentActivity.this, getString(R.string.error_sing_up), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void changeActivity() {
        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.key_credit_card_object), "");
        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
    }

    private void getUserExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            name = extras.getString(getString(R.string.key_first_name));
            lastName = extras.getString(getString(R.string.key_last_name));
            mail = extras.getString(getString(R.string.key_mail));
            phone = extras.getString(getString(R.string.key_tel));
            password = extras.getString(getString(R.string.key_pass));
            Log.i("myLog", name);
            Log.i("myLog", lastName);
            Log.i("myLog", mail);
            Log.i("myLog", phone);
            Log.i("myLog", password);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
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

    public void savePreference(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAM,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAM,Context.MODE_PRIVATE);
        String name = preferences.getString(key, defaultValue);
        return name;
    }
}
