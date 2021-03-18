package com.mesalvaai.aplicativo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.adapter.AdapterMovimentacao;
import com.mesalvaai.aplicativo.config.ConfiguracaoFirebase;
import com.mesalvaai.aplicativo.helper.Base64Custom;
import com.mesalvaai.aplicativo.model.Movimentacao;
import com.mesalvaai.aplicativo.model.Usuario;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textSaudacao;
    private TextView textSaldoGeral;
    private TextView textSaldoInvestimentos;
    private Double despesaTotal  = 0.0;
    private Double receitaTotal = 0.0;
    private Double aplicacoesTotal = 0.0;
    private Double resgatesTotal = 0.0;
    private Double resumoUsuario = 0.0;
    private Double resumoCarteira = 0.0;




    private RecyclerView recycleView;


    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;

    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;
    private  ValueEventListener valueEventListenerMovimentacoes;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


            textSaldoGeral = findViewById(R.id.textSaldoGeral);
            textSaldoInvestimentos = findViewById(R.id.textSaldoInvestimentos);
            textSaudacao = findViewById(R.id.textSaudacao);

        recycleView = findViewById(R.id.recyclerMovimentos);


           calendarView = findViewById(R.id.calendarView);
           //Chamando metodo configuraCalendarView
            configuraCalendarView();
            swipe();







            //Configurar adapter

        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);



        //Configurar RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recycleView.setLayoutManager( layoutManager );
        recycleView.setHasFixedSize( true );
        recycleView.setAdapter( adapterMovimentacao );





    }

    //Swipe

    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags );
            }

            @Override
            public boolean onMove( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao( viewHolder );
            }
        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recycleView );


    }

    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialo
        alertDialog.setTitle("Apagar transição");
        alertDialog.setMessage("Você tem certeza que deseja apagar?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get( position );

                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64( emailUsuario );

                movimentacaoRef = ConfiguracaoFirebase.getFirebaseDatabase();
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child( idUsuario )
                        .child( mesAnoSelecionado );

                movimentacaoRef.child( movimentacao.getKey() ).removeValue();
                adapterMovimentacao.notifyItemRemoved( position );
                atualizarSaldo();

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }



    public void atualizarSaldo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario );


            if ( movimentacao.getTipo().equals("r") ){
                receitaTotal = receitaTotal - movimentacao.getValor();
                usuarioRef.child("receitaTotal").setValue(receitaTotal);
            }

        if ( movimentacao.getTipo().equals("d") ){
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }

        if ( movimentacao.getTipo().equals("i") ){
            aplicacoesTotal = aplicacoesTotal - movimentacao.getValor();
            usuarioRef.child("aplicacoesTotal").setValue(aplicacoesTotal);
        }

        if ( movimentacao.getTipo().equals("ir") ){
            resgatesTotal = resgatesTotal - movimentacao.getValor();
            usuarioRef.child("resgatesTotal").setValue(resgatesTotal);
        }
    }



    //Recupera RESUMOS
    public void recuperarResumo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        Log.i("Evento","evento foi adicionado");
        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class );

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                aplicacoesTotal = usuario.getAplicacoesTotal();
                resgatesTotal = usuario.getResgatesTotal();
                resumoUsuario = (receitaTotal - despesaTotal  + ( resgatesTotal - aplicacoesTotal) );
                resumoCarteira = (aplicacoesTotal - resgatesTotal) ;


               // resumoCarteira = aplicacoesTotal - resgateTotal;

                DecimalFormat df = new DecimalFormat();
                df.applyPattern("R$ 0.00");
                String resultadoFormatado = df.format(resumoUsuario).replace(".",",");
                String resultadoCarteiraFormatado = df.format(resumoCarteira).replace(".",",");


                textSaudacao.setText("E aí, " + usuario.getNome() );
                textSaldoGeral.setText( resultadoFormatado );
                textSaldoInvestimentos.setText( resultadoCarteiraFormatado) ;



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void recuperarMovimentacoes(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );

        movimentacaoRef = ConfiguracaoFirebase.getFirebaseDatabase();
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child( idUsuario )
                                     .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                movimentacoes.clear();

                for (DataSnapshot dados: snapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    movimentacao.setKey( dados.getKey() );
                    movimentacoes.add( movimentacao );

                }

                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ;

    }


    //Configurando menu para botão SAIR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case R.id.menuSair:


               AlertDialog.Builder dialog = new AlertDialog.Builder(PrincipalActivity.this);

               //Configurar titulo e mensagem
               dialog.setTitle("Desconectar-se");
               dialog.setMessage("Você deseja sair ?");

               dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               autenticacao.signOut();
                               startActivity(new Intent(PrincipalActivity.this, MainActivity.class));
                               finish();

                           }});

                           dialog.setNegativeButton("Não", null);

                   //Exibir dialog
                                dialog.create();
                                dialog.show();


               break;

           case R.id.menuConfiguracoes:
               startActivity(new Intent(this, ConfiguracoesActivity.class));

               break;

           case R.id.menuCalculadora:
               startActivity(new Intent(this, CalcVFActivity.class));
                //Toast.makeText(this,"Abrindo calculadora", Toast.LENGTH_LONG).show();
                //finish();
               break;

           case R.id.menuCarteira:
               //Toast.makeText(this,"Abrindo Carteira de Investimentos", Toast.LENGTH_LONG).show();
               //startActivity(new Intent(this, AplicacoesActivity.class));
               //finish();
               break;


       }

        return super.onOptionsItemSelected(item);
    }




    //Metodo para o onclick (receita, despesa, investimento)

    public void adicionaReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionaDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));

    }

    public void adicionaInvestimento(View view){
       startActivity(new Intent(this, AplicacoesActivity.class));
    }

    public void configuraCalendarView(){

        CharSequence meses[] = {"Janeiro" + " |","Fevereiro" + " |","Março" + " |","Abril" + " |","Maio" + " |","Junho" + " |","Julho" + " |","Agosto" + " |","Setembro" + " |","Outubro" + " |","Novembro" + " |","Dezembro" + " |"};
        calendarView.setTitleMonths( meses );

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth()));
        mesAnoSelecionado = String.valueOf(  mesSelecionado + "" + dataAtual.getYear() );


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String mesSelecionado = String.format("%02d", (date.getMonth()));
              mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

              movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
              recuperarMovimentacoes();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recuperando resumo HEAD
        recuperarResumo();

        //Recupera movimentacoes
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
    }
}