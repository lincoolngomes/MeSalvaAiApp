package com.mesalvaai.aplicativo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;
import com.mesalvaai.aplicativo.helper.Base64Custom;
import com.mesalvaai.aplicativo.helper.Permissao;
import com.mesalvaai.aplicativo.helper.UsuarioFirebase;
import com.mesalvaai.aplicativo.model.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    //ButtomImage Foto
    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView imagemPerfil;
    private EditText editPerfilNome;
    private ImageView botaoSalvarPerfil;

    private StorageReference storageReference;
    private String identificadorUsuario;
    private Usuario usuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //Configura????es iniciais
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Validar permiss??es
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        //Botoes foto perfil
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imagemPerfil = findViewById(R.id.profile_image);
        editPerfilNome = findViewById(R.id.editPerfilNome);
        botaoSalvarPerfil = findViewById(R.id.botaoSalvarPerfil);

        //Recuperando dados usuario
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if ( url != null ){
            Glide.with(ConfiguracoesActivity.this)
                    .load( url )
                    .into( imagemPerfil );
        }else{
            imagemPerfil.setImageResource(R.drawable.padrao);
        }

        editPerfilNome.setText( usuario.getDisplayName() );

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( i.resolveActivity( getPackageManager() ) != null ){
                    startActivityForResult( i, SELECAO_CAMERA );
                }

            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if ( i.resolveActivity( getPackageManager() ) != null ){
                    startActivityForResult( i, SELECAO_GALERIA );
                }

            }
        });


        botaoSalvarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editPerfilNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario( nome );
                if ( retorno ){

                    usuarioLogado.setNome( nome );
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this, "Nome alterado com sucesso.",Toast.LENGTH_LONG ).show();
                }



            }
        });




    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {

                switch ( requestCode ){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                if ( imagem != null ) {
                    imagemPerfil.setImageBitmap( imagem );


                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();


                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child( identificadorUsuario )
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this, "Sucesso ao fazer o upload da imagem", Toast.LENGTH_LONG).show();


                            //RECUPERANDO IMAGEM NO FIREBASE
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizaFotoUsuario( url );
                                }
                            });

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public void atualizaFotoUsuario(Uri url){
        boolean retorno =  UsuarioFirebase.atualizarFotoUsuario(url);
        if ( retorno ){
            usuarioLogado.setFoto( url.toString() );
            usuarioLogado.atualizar();

            Toast.makeText(ConfiguracoesActivity.this,"Sua foto foi alterada.",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado : grantResults ){
            if ( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            };
        }

    }

    //ALERTDIALOG - Aceitar permisss??o
    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permiss??es negadas");
        builder.setMessage("Para utilizar o app ?? necess??rio aceitar as permiss??es");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}