package mx.ken.devf.uper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class DispatchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("CreditCard");
            query.whereEqualTo("parent", user);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        Toast.makeText(DispatchActivity.this, "size" + parseObjects.size(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DispatchActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(DispatchActivity.this, MainActivity.class));
                }
            });


        } else {
            startActivity(new Intent(DispatchActivity.this, WelcomeActivity.class));
        }
        if (user != null) {
            startActivity(new Intent(DispatchActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(DispatchActivity.this, WelcomeActivity.class));
        }

    }

}
