package com.example.tp6;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaraFragment extends Fragment {

    View miView;
    Bitmap bmpImagen;
    ProgressDialog dialogoProgreso;
    FaceServiceRestClient servicioProcImagenes;
    TextView txtResult;
    ImageView imgResultado;
    SharedPreferences preferencias;

    public CaraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        miView = inflater.inflate(R.layout.fragment_cara, container, false);
        AgregarReferencias();
        preferencias = getActivity().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        dialogoProgreso = new ProgressDialog(getContext());
        configFaceRecog();
        byte[] byteArray = getArguments().getByteArray(getString(R.string.param_img));
        bmpImagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgResultado.setImageBitmap(bmpImagen);
        procesarImagenObtenida(byteArray);
        return miView;
    }

    private void AgregarReferencias() {
        txtResult = (TextView) miView.findViewById(R.id.txtResult);
        imgResultado = (ImageView) miView.findViewById(R.id.imgResult);
    }

    private void configFaceRecog() {
        String apiEndpoint = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0";
        String subscriptionKey = "4a587307bc774143ab33f38c497fd8ad";
        try{
            servicioProcImagenes = new FaceServiceRestClient(apiEndpoint, subscriptionKey);
        } catch(Exception e){
            Log.d("Error", "Error: " + e.getMessage());
        }
    }

    //Medio metodo de Leo Lob de procesar ya no es necesario porque ya lo convertimos a byte[] para pasarlo al fragment
    private void procesarImagenObtenida(final byte[] byteArrayImg) {
        ByteArrayInputStream streamEntrada = new ByteArrayInputStream(byteArrayImg);
        ProcesarImagen procImg = new ProcesarImagen(dialogoProgreso, servicioProcImagenes, getContext());
        procImg.setOnImageFinishListener(procImg_imgFinish);
        procImg.execute(streamEntrada);
    }

    OnImageProcessFinish procImg_imgFinish = new OnImageProcessFinish() {
        @Override
        public void onFinish(Face[] faces) {
            if (faces.length > 0) {
                recuadrarCaras(bmpImagen, faces);
                procesarRecuadros(faces);
            } else {
                txtResult.setText("No se encontraron caras");
            }

        }
    };

    private void recuadrarCaras(Bitmap imagenOriginal, Face[] caras) {
        Bitmap imagenDibujar = imagenOriginal.copy(Bitmap.Config.ARGB_8888, true);
        Canvas lienzo = new Canvas(imagenDibujar);

        Paint pincel = new Paint();
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setColor(Color.RED);
        pincel.setStrokeWidth(5);

        for (Face cara:caras) {
            FaceRectangle rectanculoCara = cara.faceRectangle;
            lienzo.drawRect(rectanculoCara.left, rectanculoCara.top,
                        rectanculoCara.left + rectanculoCara.width,
                    rectanculoCara.top+rectanculoCara.width, pincel);
        }
        imgResultado.setImageBitmap(imagenDibujar);
    }

    private void procesarRecuadros(Face[] faces) {
        int cantHombres = preferencias.getInt("cantHombres", 0), cantMujeres = preferencias.getInt("cantMujeres", 0);
        String mensaje = "";
        for(int i = 0; i < faces.length; i++){
            mensaje += "Edad: " + faces[i].faceAttributes.age;
            mensaje += " - Sonrisa: " + faces[i].faceAttributes.smile;
            mensaje += " - Barba: " + faces[i].faceAttributes.facialHair.beard;
            String genero = faces[i].faceAttributes.gender.equals("male")? "hombre" : "mujer";
            mensaje += " - Género: " +  genero;
            String anteojos;
            switch (faces[i].faceAttributes.glasses) {
                case NoGlasses:
                    anteojos = "no";
                    break;
                case ReadingGlasses:
                    anteojos = "para leer";
                    break;
                case Sunglasses:
                    anteojos = "de sol";
                    break;
                case SwimmingGoggles:
                    anteojos = "antiparras";
                    break;
                default:
                    anteojos = "desconocido";
                    break;
            }
            mensaje += " - Anteojos: " + anteojos;

            if(genero == "hombre"){
                cantHombres++;
            } else {
                cantMujeres++;
            }

            SharedPreferences.Editor editorPreferencias;
            editorPreferencias = preferencias.edit();
            editorPreferencias.putInt("cantHombres", cantHombres);
            editorPreferencias.putInt("cantMujeres", cantMujeres);
            editorPreferencias.commit();

            if(i < faces.length-1){
                mensaje += "\n";
            }
        }

        mensaje += "\nCantidad de hombres: " + cantHombres + " - Cantidad de mujeres: " + cantMujeres;
        txtResult.setText(mensaje);
    }

}
