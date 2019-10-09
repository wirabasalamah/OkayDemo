package com.gyp.okaydemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gyp.okaydemo.data.JsonHandler;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    ProgressBar loadingProgressBar;
    EditText passwordEditText;
    EditText usernameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().toString().length() > 0 && passwordEditText.getText().toString().length() > 0){
                    login();
                }
            }
        });
    }

    public void login() {
        String email = this.usernameEditText.getText().toString();
        try {
            JSONObject data = new JSONObject();
            data.put("username", email);
            new SendLoginData().execute(new JSONObject[]{data});
        } catch (Exception e) {
        }
    }

    private class SendLoginData extends AsyncTask<JSONObject, Void, JSONObject> {

        public void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(showProgress());
        }

        public JSONObject doInBackground(JSONObject... params) {
            try {
                return JsonHandler.SendHttpPost("http://griyacart.com/demo-okay/login.php", params[0]);
            } catch (Exception e) {
                return null;
            }
        }

        public void onPostExecute(JSONObject resp) {
            runOnUiThread(dismissProgress);
            if (resp != null) {
                try {
                    if (resp.getString("authResponse").equalsIgnoreCase("YES")) {
                        onLoginSuccess();
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Login failed, ");
                        sb.append(resp.getString("statusMsg"));
                        runOnUiThread(showError(sb.toString()));
                    }
                } catch (Exception e) {
                }
            } else {
                runOnUiThread(showError("Login failed, please try again"));
            }
        }
    }

    public Runnable showProgress() {
        return new Runnable() {
            public void run() {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
        };
    }

    public Runnable showError(final String message) {
        return new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this.getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        };
    }

    public Runnable dismissProgress = new Runnable() {
        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    };

    public void onLoginSuccess() {
        Intent AppIntent = new Intent(this, MainActivity.class);
        AppIntent.putExtra("username", this.usernameEditText.getText().toString());
        startActivity(AppIntent);
        finish();
    }
}
