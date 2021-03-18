package com.mesalvaai.aplicativo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;
import com.mesalvaai.aplicativo.helper.Base64Custom;
import com.mesalvaai.aplicativo.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastra-se");

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);


        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // NAO DEIXAR O USUARIO CRIAR UM CADASTRO COM ALGUM CAMPO NULL, PRA ISSO É NECESSARIO
                //RECUPERAR O QUE O CLIENTE DIGITOU

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //Validação se os campos foram preenchidos

                if ( !textoNome.isEmpty() ){
                    if ( !textoEmail.isEmpty() ){
                        if ( !textoSenha.isEmpty() ){

                            //Apos fazer todos os testes, fazer o cadastro do usuario ->

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrarUsuario();

                        }else{
                            Toast.makeText(CadastroActivity.this, "Preencha sua senha", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(CadastroActivity.this, "Preencha seu e-mail", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha seu nome", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    //Metodo para cadastrar o usuario
    public void cadastrarUsuario() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful()){

                    //Salvar usuario com Base64
                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setIdUsuario( idUsuario );
                    usuario.salvar();


                    //finish pra depois de criado o usuario ir pra pilha anterior que é intro_cadastro
                    finish();
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário.", Toast.LENGTH_LONG).show();
                }else{

                    String exececao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthWeakPasswordException e){
                        exececao = "Digite uma senha mais forte.";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        exececao = "Digite um e-mail válido.";
                    }catch ( FirebaseAuthUserCollisionException e ){
                        exececao = "Essa conta já foi cadastrada.";
                    }catch (Exception e){
                        exececao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, exececao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}