package com.mesalvaai.aplicativo.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64( email );

        return identificadorUsuario;
    }

}
