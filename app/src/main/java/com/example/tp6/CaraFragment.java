package com.example.tp6;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaraFragment extends Fragment {

    View miView;
    Bitmap bmpImagen;

    public CaraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        miView = inflater.inflate(R.layout.fragment_cara, container, false);
        //Medio metodo de Leo Lob de procesar ya no es necesario porque ya lo convertimos a byte[] para pasarlo al fragment
        byte[] byteArray = getArguments().getByteArray(getString(R.string.param_img));
        bmpImagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        procesarImagenObtenida(byteArray);
        return miView;
    }

    private void procesarImagenObtenida(final byte[] byteArrayImg) {
        
    }



}
