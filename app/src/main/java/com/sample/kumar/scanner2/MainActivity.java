package com.sample.kumar.scanner2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class
MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ArrayAdapter<String> adapter;
    private static final int REQUEST_CAMERA = 1;
    final static String serverUrl = "http://117.218.124.167:8080/khadi/qr.php?code=";
    private ZXingScannerView scannerView;
    String res;
    ImageButton img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.e("OnCreate", "OnCreated");

        //ZXingScannerView zxing=(ZXingScannerView)findViewBy Id(R.id.zxscan);
        scannerView = (ZXingScannerView) findViewById(R.id.zxscan);
        Log.e("Cameraaaa","Zxing camera");
        //img=(ImageButton)findViewById(R.id.img1);
        //scannerView.setResultHandler(this);
        //scannerView.startCamera();

        //zxing.addView(scannerView);
        //setContentView(scannerView);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkPermission()) {
                Log.e("M", "Permission");
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            } else {
                Log.e("R", "Permission");
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    Log.e("Successfull", "Success");

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Log.e("accpt", "Accpt");
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("ntaccpt", "Not Acpt");
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int capi = android.os.Build.VERSION.SDK_INT;
        if (capi >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
        scannerView = null;
    }

    @Override
    public void handleResult(final Result result) {

        res = result.getText();
        Log.d("Scanner Result", result.getText());
        Log.d("Scanner Result", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
        builder.setNeutralButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GetText();

                ArrayList<String> arrayList=new ArrayList<String>();

                arrayList.add(res);

                Intent g = new Intent(MainActivity.this, ModelActivity.class);
                g.putStringArrayListExtra("array list",(ArrayList<String>)arrayList);
               startActivity(g);
                // startActivityForResult(g, 2);
                scannerView.resumeCameraPreview(MainActivity.this);
            }
            //@Override

        });


        builder.setMessage(result.getText());
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            data.getStringExtra("Message");
            Toast.makeText(MainActivity.this, data.getStringExtra("Message"), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetText()  //throws UnsupportedEncodingException
    {

        String data = res;
        String text = "";
        BufferedReader reader = null;
        Log.e("Datafirst", data);
        String requesturi = serverUrl + data;
        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL(requesturi);
            Log.e("DataSecond", requesturi);

            // Send POST data request
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            Log.e("Data Third", data);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            Toast.makeText(MainActivity.this, "Readed Successfully", Toast.LENGTH_SHORT).show();

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
                Toast.makeText(MainActivity.this, "Readed Successfully", Toast.LENGTH_SHORT).show();
            }
            text = sb.toString();
        } catch (Exception ex) {
            Log.e("Exceptionio", "Error", ex);
            ex.printStackTrace();
        } finally {
            try {

                reader.close();
                Log.e("Closed", "Closed");
            } catch (Exception ex) {
                Log.e("Except2", "Error", ex);
                ex.printStackTrace();
            }
            String dd = requesturi;
        }

        // Show response on activity
        //name.setText(text);
        Log.e("setText", data);

    }


}

