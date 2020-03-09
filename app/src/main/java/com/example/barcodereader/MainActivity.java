package com.example.barcodereader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import java.net.URI;
import java.security.Permission;
import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static android.net.Uri.parse;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    private static final int Request_Camera=1;
    private ZXingScannerView scannerView;
    private Object Camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermissions();
            }

        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{CAMERA},Request_Camera);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case Request_Camera:
                if(grantResults.length >0)
                {
                   boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   if(cameraAccepted)
                   {
                       Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                   }
                   else
                   {
                       Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                       {
                           if(shouldShowRequestPermissionRationale(CAMERA))
                           {
                               displayAlertMessage("You need to allow access for both permission", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which)
                                   {
                                       requestPermissions(new String[]{CAMERA},Request_Camera);
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

    private void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
    {
       new AlertDialog.Builder(MainActivity.this)
               .setTitle("Alert dialog")
               .setMessage(message)
               .setCancelable(true)
               .setIcon(R.drawable.nirmal)
               .setPositiveButton("OK",listener)
               .setNegativeButton("Cancel",null)
               .create()
               .show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                if(scannerView==null)
                {
                    scannerView=new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();

            }
            else
            {
                requestPermissions();
            }
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();

    }

    @Override
    public void handleResult(final Result rawResult)
    {
        final String email="https://mail.google.com/mail/u/0/#inbox";
        final String address="https://www.bsesdelhi.com/web/brpl/about-bses";

       final String scanResult=rawResult.getText();
       AlertDialog.Builder builder=new AlertDialog.Builder(this);
       builder.setTitle("scan Result");
       builder.setIcon(R.drawable.nirmal);
       builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which)
           {
             //  scannerView.resumeCameraPreview(MainActivity.this);
               Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(email));

               startActivity(intent);

           }
       });
       builder.setNegativeButton("Visit", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
             //  Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
               startActivity(intent);
           }
       });
       builder.setMessage(scanResult);
       AlertDialog alert=builder.create();
       alert.show();

    }
}
