package mx.ken.devf.uper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class SignUpActivity extends ActionBarActivity {

    EditText nameET;
    EditText lastNameET;
    EditText mailET;
    EditText phoneEt;
    EditText passwortdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameET = (EditText) findViewById(R.id.name_edit_text);
        mailET = (EditText) findViewById(R.id.mail_et);
        lastNameET = (EditText) findViewById(R.id.last_name_et);
        phoneEt = (EditText) findViewById(R.id.phone_et);
        passwortdET = (EditText) findViewById(R.id.password_et);
        findViewById(R.id.button_siguiente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
        findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, PaymentActivity.class));
            }
        });

    }

    private void validateInputs() {
        final String name;
        final String lastName;
        final String mail;
        final String phone;
        final String password;


        boolean error = false;

        name = nameET.getText().toString();
        if (name.length() == 0) {
            error = true;
            nameET.setError("name not valid");
        } else {
            nameET.setError(null);
        }

        lastName = lastNameET.getText().toString();
        if (lastName.length() == 0) {
            error = true;
            lastNameET.setError("Incorrect last name");
        } else {
            lastNameET.setError(null);
        }

        mail = mailET.getText().toString();
        if (mail.length() == 0) {
            error = true;
            mailET.setError("Invalid email");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            error = true;
            mailET.setError("Invalid email");
        }

        phone = phoneEt.getText().toString();
        if (phone.length() != 10) {
            error = true;
            phoneEt.setError("Invalid number phone");
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            error = true;
            phoneEt.setError("Invalid number phone");
        }

        password = passwortdET.getText().toString();
        if (password.length() == 0) {
            error = true;
            passwortdET.setError("Invalid password ");
        }

        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();
        if (!error) {
            final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
            dialog.setMessage(getString(R.string.progress_signup));
            dialog.show();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", mail);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    dialog.dismiss();
                    if (parseUsers != null && parseUsers.size() > 0) {
                        mailET.setError("Mail ya registrado NOOOO PUTO");
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, PaymentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(getString(R.string.key_tel), phone);
                        intent.putExtra(getString(R.string.key_pass), password);
                        intent.putExtra(getString(R.string.key_first_name), name);
                        intent.putExtra(getString(R.string.key_last_name), lastName);
                        intent.putExtra(getString(R.string.key_mail), mail);
                        startActivity(intent);
                    }
                }
            });


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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


}
