package com.mesalvaai.aplicativo.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;
    //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);


        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_1)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_2)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_3)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_light)
                .fragment(R.layout.intro_4)
                .build());

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.background_light)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build());

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Verificar se o usuario está logado, logo ao abrir o app
        verificarUsuarioLogado();
    }

    public  void btEntrar(View view){
       startActivity(new Intent(this, LoginActivity.class));

    }

    public  void btCadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));

    }


    //Metodo para veirificar se o usuario esta logado
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //deslogar usuario
     // autenticacao.signOut();

        if ( autenticacao.getCurrentUser() != null ){
            abrirTelaPrincipal();
        }

    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }

}