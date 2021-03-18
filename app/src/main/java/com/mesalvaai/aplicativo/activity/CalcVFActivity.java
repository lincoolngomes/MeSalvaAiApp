package com.mesalvaai.aplicativo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mesalvaai.aplicativo.R;

import java.text.DecimalFormat;

public class CalcVFActivity extends AppCompatActivity {

    //FORMATAÇÃO DECIMAL
    DecimalFormat df = new DecimalFormat("0.00");

    public TextInputEditText editarCapital;
    public TextInputEditText editarJuros;
    public TextInputEditText editarPeriodo;
    public TextInputEditText editarPMT;
    public TextInputEditText editarJurosCompostos;
    public TextView resultadoVF;
    public Button botaoCalcular, botaoVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_v_f);


        editarCapital = findViewById(R.id.editarCapital);
        editarJuros = findViewById(R.id.editarJuros);
        editarPeriodo = findViewById(R.id.editarPeriodo);
        editarPMT = findViewById(R.id.editarPMT);
        resultadoVF = findViewById(R.id.resultadoVF);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void validarDados(View view){


        String txJurosString = editarJuros.getText().toString();
        String periodoString = editarPeriodo.getText().toString();
        String capitalString = editarCapital.getText().toString();
        String pmtString = editarPMT.getText().toString();


        if ( !periodoString.isEmpty()){
            if ( !txJurosString.isEmpty() ) {
                if (!pmtString.isEmpty() & !capitalString.isEmpty()){
                    calcularCapitalePMT();
                                                                    }
                else                                                {
                    Toast.makeText(CalcVFActivity.this, "Por favor, insira um valor de capital inicial e valor aplicado mensalmente.", Toast.LENGTH_LONG).show();
                                                                    }

                                            }else{
                Toast.makeText(CalcVFActivity.this, "Por favor, insira uma taxa de juros ao mês.",Toast.LENGTH_LONG).show();
            }


                                       }else {
            Toast.makeText(CalcVFActivity.this, "Por favor, insira um período em meses.",Toast.LENGTH_LONG).show();
        }

            }


    // double capital = editarCapital.getDouble;
    //  double txJuros = Double.parseDouble(editarJuros.getText().toString());
//        double periodo = Double.parseDouble(editarPeriodo.getText().toString());
//        double pmt = Double.parseDouble(editarPMT.getText().toString());
//        double juros = txJuros / 100;

//        if ( txJuros) {
//            Toast.makeText(this, "Perido vazio", Toast.LENGTH_LONG);
//        }
//
//
//        //Calculos
//        double jurosSimples = capital + (capital * juros * periodo);
//        double jurosCompostosPV = capital * Math.pow(1 + juros, periodo);
//        double jurosCompostosPMT = pmt * (Math.pow(1 + juros, periodo) - 1) / juros;
//        double jurosCompostoResultado1 = jurosCompostosPV + jurosCompostosPMT;



    //        //Calculos
//        double jurosSimples = capital + (capital * juros * periodo);

    public void calcularCapital(){

//        Double txJuros = Double.parseDouble(editarJuros.getText().toString());
//        Double periodo = Double.parseDouble(editarPeriodo.getText().toString());
//        Double pmt = Double.parseDouble(editarPMT.getText().toString());
//        Double capital = Double.parseDouble(editarCapital.getText().toString());
//        Double juros = txJuros / 100;
////
//        Double jcCapital = capital * Math.pow(1 + juros, periodo);
////        Double jurosCompostosPMT = pmt * (Math.pow(1 + juros, periodo) - 1) / juros;
////        Double jcCapitalPmt = jcCapital + jurosCompostosPMT;

        resultadoVF.setText("Você terá: R$ Capital"  );

    }

    public void calcularPmt(){

        resultadoVF.setText(" R$ PMT"  );
    }


    public void calcularCapitalePMT() {




        double txJuros = Double.parseDouble(editarJuros.getText().toString());
        double periodo = Double.parseDouble(editarPeriodo.getText().toString());
        double pmt = Double.parseDouble(editarPMT.getText().toString());
        double capital = Double.parseDouble(editarCapital.getText().toString());
        double juros = txJuros / 100;

        Double jurosCompostosCapital = capital * Math.pow(1 + juros, periodo);
        Double jurosCompostosPMT = pmt * (Math.pow(1 + juros, periodo) - 1) / juros;
        Double jcCapitalPmt = jurosCompostosCapital + jurosCompostosPMT;


        resultadoVF.setText("Seu resultado será de: \n R$ " + df.format(jcCapitalPmt)  );

    }
}