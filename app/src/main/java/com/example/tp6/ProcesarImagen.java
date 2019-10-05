package com.example.tp6;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

import static java.security.AccessController.getContext;

public class ProcesarImagen extends AsyncTask<InputStream, String, Face[]> {

    private ProgressDialog _dialogoProgreso;
    private FaceServiceRestClient _servicioProcImagenes;
    private Context _context;
    private OnImageProcessFinish _eventoImageProcess;

    public ProcesarImagen(ProgressDialog dialogoProgreso, FaceServiceRestClient servicioProcImagenes, Context context) {
        _dialogoProgreso = dialogoProgreso;
        _servicioProcImagenes = servicioProcImagenes;
        _context = context;
        _eventoImageProcess = null;
    }

    public void setOnImageFinishListener(OnImageProcessFinish evt) {
        _eventoImageProcess = evt;
    }

    @Override
    protected Face[] doInBackground(InputStream... imagenes) {
        publishProgress("Detectando caras...");
        Face[] resultado = null;
        try {
            FaceServiceClient.FaceAttributeType[] atributos;
            atributos = new FaceServiceClient.FaceAttributeType[] {
                    FaceServiceClient.FaceAttributeType.Age,
                    FaceServiceClient.FaceAttributeType.Glasses,
                    FaceServiceClient.FaceAttributeType.Smile,
                    FaceServiceClient.FaceAttributeType.FacialHair,
                    FaceServiceClient.FaceAttributeType.Gender
            };

            resultado = _servicioProcImagenes.detect(imagenes[0], true, false, atributos);
        } catch (Exception e) {

        }
        return resultado;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _dialogoProgreso.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        _dialogoProgreso.setMessage(values[0]);
    }

    @Override
    protected void onPostExecute(Face[] faces) {
        super.onPostExecute(faces);
        _dialogoProgreso.dismiss();
        if (faces == null) {
            Toast.makeText(_context, "Hubo un error", Toast.LENGTH_LONG).show();
        } else {
            _eventoImageProcess.onFinish(faces);
        }
    }
}

interface OnImageProcessFinish {
    void onFinish(Face[] faces);
}