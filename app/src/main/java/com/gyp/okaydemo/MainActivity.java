package com.gyp.okaydemo;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gyp.okaydemo.data.JsonHandler;

import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText amountEditText;
    String currentToken;
    ProgressBar loadingProgressBar;
    TextView theToken;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Page");
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            this.username = extra.getString("username");
        }
        this.amountEditText = (EditText) findViewById(R.id.amount);
        this.theToken = (TextView) findViewById(R.id.token);
        Button processButton = (Button) findViewById(R.id.process);
        this.loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        processButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.amountEditText.getText().toString().length() > 0) {
                    Random random = new Random();
                    currentToken = String.format("%04d", new Object[]{Integer.valueOf(random.nextInt(10000))});
                    runOnUiThread(setToken("Harap masukkan token: " + currentToken));
                    login();
                }
            }
        });
    }

    public void login() {
        String amount = this.amountEditText.getText().toString();
        try {
            JSONObject data = new JSONObject();
            data.put("username", this.username);
            data.put("amount", "Rp. " + amount);
            new SendTransactionData().execute(new JSONObject[]{data});
        } catch (Exception e) {
        }
    }

    private class SendTransactionData extends AsyncTask<JSONObject, Void, JSONObject> {
        public void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(showProgress);
        }

        public JSONObject doInBackground(JSONObject... params) {
            try {
                return JsonHandler.SendHttpPost("http://griyacart.com/demo-okay/trx.php", params[0]);
            } catch (Exception e) {
                return null;
            }
        }

        public void onPostExecute(JSONObject resp) {
            runOnUiThread(dismissProgress);
            if (resp != null) {
                try {
                    if (resp.getString("authResponse").equalsIgnoreCase("NO")) {
                        runOnUiThread(trxError);
                        runOnUiThread(showError("Transaction failed, " + resp.getString("statusMsg")));
                    } else if (!resp.getBoolean("error")) {
                        if (resp.getString("authResponse").equalsIgnoreCase(currentToken)) {
                            runOnUiThread(trxFinish);
                        } else {
                            runOnUiThread(showError("Transaction failed, Token salah"));
                            runOnUiThread(trxError);
                        }
                    } else {
                        runOnUiThread(trxError);
                        runOnUiThread(showError("Transaction failed, " + resp.getString("statusMsg")));
                    }
                } catch (Exception e) {
                }
            } else {
                runOnUiThread(showError("Transaction failed, please try again"));
            }
        }
    }

    public Runnable showProgress = new Runnable() {
        public void run() {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
    };

    public Runnable dismissProgress = new Runnable() {
        public void run() {
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    };


    public Runnable showError(final String message){

        Runnable aRunnable = new Runnable(){
            public void run(){
                Toast.makeText(MainActivity.this.getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        };

        return aRunnable;

    }

    private Runnable trxFinish = new Runnable() {

        @Override
        public void run() {
            showSuccess();
            amountEditText.setText("");
            theToken.setText("");
        }
    };

    private Runnable trxError = new Runnable() {

        @Override
        public void run() {
            amountEditText.setText("");
            theToken.setText("");
        }
    };

    public void showSuccess() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Transaction Success");
        dialogBuilder.setMessage("Transaksi sebesar Rp. " + amountEditText.getText().toString() + "\n Sukses");

        dialogBuilder
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public Runnable setToken(final String message){

        Runnable aRunnable = new Runnable(){
            public void run(){
                theToken.setText(message);
            }
        };

        return aRunnable;

    }
}
