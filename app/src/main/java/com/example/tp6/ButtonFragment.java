package com.example.tp6;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFragment extends Fragment {

    Button btnTomarFoto, btnElegirFoto;
    View miView;
    MainActivity miActivity;

    public ButtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        miView = inflater.inflate(R.layout.fragment_button, container, false);
        miActivity = (MainActivity) getActivity();
        AgregarReferencias();
        SetearListeners();
        VerificarTomarFoto();
        return miView;
    }

    private void SetearListeners() {
        btnTomarFoto.setOnClickListener(btnTomarFoto_click);
        btnElegirFoto.setOnClickListener(btnElegirFoto_click);
    }

    private void AgregarReferencias() {
        btnTomarFoto = (Button) miView.findViewById(R.id.btnTomarFoto);
        btnElegirFoto = (Button) miView.findViewById(R.id.btnElegirFoto);
    }

    View.OnClickListener btnTomarFoto_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            miActivity.TomarFoto();
        }
    };


    View.OnClickListener btnElegirFoto_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            miActivity.ElegirFoto();
        }
    };

    private void VerificarTomarFoto() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnTomarFoto.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 14);
        } else {
            btnTomarFoto.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult (int codigoRespuesta, @NonNull String[] nombresPermisos, @NonNull int[] resultadosPermisos){
        if(codigoRespuesta == 14){
            boolean obtuvoPermisos = true;
            for (int i = 0; i < resultadosPermisos.length; i++){
                if(resultadosPermisos[i] != PackageManager.PERMISSION_GRANTED){
                    obtuvoPermisos = false;
                }
            }
            if(obtuvoPermisos) {
                btnTomarFoto.setEnabled(true);
            } else {
                Log.d("PermisosPedidos", "No obtuvo los permisos");
            }
        }
    }
}
