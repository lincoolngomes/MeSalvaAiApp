package com.mesalvaai.aplicativo.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;
import com.mesalvaai.aplicativo.helper.UsuarioFirebase;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String foto;
    private String nome_completo;
    private String cpf;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;
    private Double aplicacoesTotal = 0.00;
    private Double resgatesTotal = 0.00;


    public Usuario() {
    }


    //Salvar usuario no Firebase
    public void salvar(){
     DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
     firebase.child("usuarios")
             .child( this.idUsuario )
             .setValue( this );
    }

    public Double getResgatesTotal() {
        return resgatesTotal;
    }

    public void setResgatesTotal(Double resgatesTotal) {
        this.resgatesTotal = resgatesTotal;
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    public Double getAplicacoesTotal() {
        return aplicacoesTotal;
    }

    public void setAplicacoesTotal(Double aplicacoesTotal) {
        this.aplicacoesTotal = aplicacoesTotal;
    }

    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = database.child("usuarios")
                .child( identificadorUsuario );

        Map<String, Object> valoresUsuario = converterParaMap();

        usuarioRef.updateChildren( valoresUsuario );

    }


    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());
        usuarioMap.put("nome_completo", getNome_completo());
        usuarioMap.put("cpf", getCpf());

        return usuarioMap;
    }

    public String getNome_completo() {
        return nome_completo;
    }

    public void setNome_completo(String nome_completo) {
        this.nome_completo = nome_completo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
