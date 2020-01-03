package com.example.restaurante_inteligente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class PerfilUsuario extends AppCompatActivity {

    TextView txt_id, txt_name, txt_email, txt_provider_id, txt_phone_number, txt_disponibilidad;
    ImageView imv_photo;
    Button btn_logout;
    DatabaseReference db_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        Intent intent = getIntent();
        HashMap<String, String> info_user = (HashMap<String, String>)intent.getSerializableExtra("info_user");

        txt_id = findViewById(R.id.txt_userId);
        txt_name = findViewById(R.id.txt_nombre);
        txt_email = findViewById(R.id.txt_correo);
        imv_photo = findViewById(R.id.imv_foto);
        txt_provider_id = findViewById(R.id.txt_provider_id);
        txt_phone_number = findViewById(R.id.txt_phone_number);

        txt_id.setText(info_user.get("user_id"));
        txt_name.setText(info_user.get("user_name"));
        txt_email.setText(info_user.get("user_email"));
        txt_provider_id.setText(info_user.get("user_provider_id"));
        txt_phone_number.setText(info_user.get("user_phone_number"));
        String photo = info_user.get("user_photo");
        Picasso.with(getApplicationContext()).load(photo).into(imv_photo);

        proceso();
    }

    public void proceso (){
        iniciarBaseDeDatos();
        //leerTweets();
        consultarSilla("1", "1");
        //consultarMesa("1");
        enviarInformacionMesa("1", true);
        //escribirTweets(info_user.get("user_name"), info_user.get("user_provider_id"), info_user.get("user_phone_number"));
    }

    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference().child("Grupo");
    }

    public void leerTweets(){
        db_reference.child("Grupo 2").child("tweets")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    public void consultarSilla(String idMesa, String idSilla){
        DatabaseReference sillas = db_reference.child("Mesa").child(idMesa).child("Sillas");
        sillas.child(idSilla).child("Disponible")
                .addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        txt_disponibilidad = findViewById(R.id.txt_disponibilidad);
                        System.out.println(dataSnapshot.getValue());
                        if ((boolean)dataSnapshot.getValue()) {
                            //if (dataSnapshot.getValue() == 000000000070000000) {
                            txt_disponibilidad.setText("Silla disponible");
                            System.out.println("Silla disponible");
                        } else {
                            txt_disponibilidad.setText("Silla ocupada");
                            System.out.println("Silla ocupada");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });;
    }

    public void consultarMesa(String id){
        DatabaseReference mesas = db_reference.child("Mesa");
        mesas.child(id).child("Disponible")
                .addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getValue());
                        txt_disponibilidad = findViewById(R.id.txt_disponibilidad);
                        if ((boolean)dataSnapshot.getValue()) {
                            txt_disponibilidad.setText("Mesa disponible");
                            System.out.println("Mesa disponible");

                        } else {
                            txt_disponibilidad.setText("Mesa ocupada");
                            System.out.println("Mesa ocupada");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });;
    }

    public void consultarMesas(){
        DatabaseReference mesas = db_reference.child("Mesa");
        mesas
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, String> mesa = (HashMap<String, String>) snapshot.getValue();
                            System.out.println(mesa.get("Disponible"));

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void enviarInformacionMesa(String id, boolean disponible){
        DatabaseReference mesas = db_reference.child("Mesa");
        mesas.child(id).child("Disponible").setValue(disponible);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                proceso();
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    public void escribirTweets(String autor, String providerId, String phoneNumber){
        String tweet = "hola mundo firebase 2";
        String fecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Map<String, String> hola_tweet = new HashMap<String, String>();
        hola_tweet.put("autor", autor);
        hola_tweet.put("fecha", fecha);
        hola_tweet.put("provider ID", providerId);
        hola_tweet.put("telefono", phoneNumber);
        DatabaseReference tweets = db_reference.child("Grupo 2").child("tweets");
        tweets.setValue(tweet);
        tweets.child(tweet).child("autor").setValue(autor);
        tweets.child(tweet).child("fecha").setValue(fecha);
        tweets.child(tweet).child("provider ID").setValue(providerId);
        tweets.child(tweet).child("telefono").setValue(phoneNumber);
    }

}
