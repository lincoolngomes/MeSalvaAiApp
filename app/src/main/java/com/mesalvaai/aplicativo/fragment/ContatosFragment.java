package com.mesalvaai.aplicativo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.adapter.AdapterMovimentacao;
import com.mesalvaai.aplicativo.adapter.ContatosAdapter;
import com.mesalvaai.aplicativo.model.Usuario;

import java.util.ArrayList;


public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);


        //Configurações iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);

        //Configurar adapter

        adapter = new ContatosAdapter( listaContatos, getActivity() );



        //Configurar RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager );
        recyclerViewListaContatos.setHasFixedSize( true );
        recyclerViewListaContatos.setAdapter( adapter );


        return  view;
    }
}