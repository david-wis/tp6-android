package com.example.tp6;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.preference.Preference;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

import java.io.ByteArrayOutputStream;
import java.util.jar.Pack200;


public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    //TextView txtResultado;
    //Button btnTomarFoto, btnElegirFoto;
    //ImageView imgResultado;
    FrameLayout frmLyt;
    FragmentManager adminFragments;
    FragmentTransaction transacFragments;
    ButtonFragment buttonFragment = new ButtonFragment();
    CaraFragment caraFragment;

    //4a587307bc774143ab33f38c497fd8ad
    //d3d05d085e5a4f88baaec60547c8935b
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        AgregarReferencias();
        adminFragments = getFragmentManager();
        InicializarBotones();
    }



    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        transacFragments = adminFragments.beginTransaction();
        transacFragments.replace(R.id.frmLyt, fragment, tag);
        if (addToBackStack){
            transacFragments.addToBackStack(tag);
        }
        transacFragments.commit();
    }

    private void AgregarReferencias() {
        frmLyt = (FrameLayout) findViewById(R.id.frmLyt);
    }

    private void InicializarBotones() {
        doFragmentTransaction( buttonFragment, getString(R.string.button_fragment), false, "");
    }

    public final static int CODIGO_TOMAR_FOTO = 15;
    public void TomarFoto() {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentTomarFoto, CODIGO_TOMAR_FOTO);
    }

    public final static int CODIGO_OBTENER_FOTO = 16;
    public void ElegirFoto() {
        Intent intentObtenerFoto = new Intent(Intent.ACTION_GET_CONTENT);
        intentObtenerFoto.setType("image/*");
        startActivityForResult(Intent.createChooser(intentObtenerFoto, "Seleccione una foto"), CODIGO_OBTENER_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: Podemos poner un cosito de cargando aca
        if (requestCode == CODIGO_TOMAR_FOTO && resultCode == RESULT_OK) {
            Bitmap fotoRecibida = (Bitmap) data.getExtras().get("data");
            caraFragment = new CaraFragment();
            Bundle datos = new Bundle();
            datos.putByteArray("PNG CARA", bitmapToByteArray(fotoRecibida));
            caraFragment.setArguments(datos);
            doFragmentTransaction(caraFragment, "CARA FRAGMENT", true, "");
        } else if (requestCode == CODIGO_OBTENER_FOTO && resultCode == RESULT_OK && data != null) {
            Uri ubicacion = data.getData();
            Bitmap imagenFoto = null;
            try {
                imagenFoto = MediaStore.Images.Media.getBitmap(getContentResolver(), ubicacion);
            } catch (Exception e) {
                Log.d("Foto", "No se pudo obtener la foto");
            }
            if (imagenFoto != null) {
                caraFragment = new CaraFragment();
                Bundle datos = new Bundle();
                datos.putByteArray(getString(R.string.param_img), bitmapToByteArray(imagenFoto)); //Hay que convertir la foto a byte[] para pasarla en el bundle
                caraFragment.setArguments(datos);
                doFragmentTransaction( caraFragment, getString(R.string.cara_fragment), true, "");
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
