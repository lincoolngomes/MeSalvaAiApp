package com.mesalvaai.aplicativo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;
import com.mesalvaai.aplicativo.helper.Base64Custom;
import com.mesalvaai.aplicativo.helper.DateCustom;
import com.mesalvaai.aplicativo.model.Movimentacao;
import com.mesalvaai.aplicativo.model.Usuario;

public class AplicacoesActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    private Double aplicacoesTotal;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicacoes);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a date atual
        campoData.setText( DateCustom.dataAtual() );
        recuperarAplicacoesTotal();





    }

    //Salvando aplicacão no Firebase
    public void salvarAplicacoes(View view){

        if ( validarCamposAplicacoes() ) {

            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("i");



            Double aplicacoesAtualizada = aplicacoesTotal + valorRecuperado;
            atualizarAplicacoes( aplicacoesAtualizada  );


            movimentacao.salvar(data);

            finish();
        }

    }


    public Boolean validarCamposAplicacoes(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();


        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty() ){
                    if ( !textoDescricao.isEmpty() ){
                        return true;
                    }else{
                        Toast.makeText(AplicacoesActivity.this, "Descrição não foi preenchido", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else{
                    Toast.makeText(AplicacoesActivity.this, "Categoria não foi preenchido", Toast.LENGTH_LONG).show();
                    return false;
                }
            }else{
                Toast.makeText(AplicacoesActivity.this, "Data não foi preenchido", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(AplicacoesActivity.this, "Valor não foi preenchido", Toast.LENGTH_LONG).show();
            return false;
        }


    }


    public void recuperarAplicacoesTotal(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class );
                aplicacoesTotal = usuario.getAplicacoesTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void atualizarAplicacoes(Double aplicacoes){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );


        usuarioRef.child("aplicacoesTotal").setValue(aplicacoes);
    }

    public void botaoResgatar(View view){
        Intent intent = new Intent(this, ResgatesActivity.class);
        startActivity(intent);
        finish();
    }




}