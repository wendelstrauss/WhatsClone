package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.santalu.maskara.widget.MaskEditText;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import java.util.concurrent.TimeUnit;

public class CadastroTelefoneActivity extends AppCompatActivity {

    private MaskEditText campoTelefone;
    private EditText campoCodigo;
    private Button btnCadastro;
    private Button btnReenviar;
    private ProgressBar progress;
    private FirebaseAuth autenticacao;
    private String telefone;
    private String codigoVerificacao;
    private String codigoSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_telefone);

        //inicializar componentes
        campoTelefone = findViewById(R.id.editCadastroTelTelefone);
        campoCodigo = findViewById(R.id.editCadastroTelCodigo);
        btnCadastro = findViewById(R.id.btnCadastroTel);
        btnReenviar = findViewById(R.id.btnCadastroTelReenviar);
        progress = findViewById(R.id.progCadastroTel);

        //configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getAutenticacao();

        //listener do botao reenviar
        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarInterface(0);
            }
        });

    }

    public void validarCampoTel(View view){
        telefone = "+" + campoTelefone.getUnMasked();
        if( !telefone.isEmpty() ){
            verificaTelefone( telefone );
            abrirProgressBar(true);
        }else{
            avisoToast("Insira seu telefone!");
        }
    }

    private void verificaTelefone(String telefone){

        //passo 2 - Vendo se a autenticaco foi possivel ou se e necessario digitar o codigo
        PhoneAuthProvider.OnVerificationStateChangedCallbacks metodosCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                avisoToast("verificacao realizada");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                avisoToast("Verificação Falhou");
                btnReenviar.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                btnCadastro.setEnabled(false);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                abrirProgressBar(false);
                avisoToast("Código Enviado!");
                //pega o codigo enviado pro usuario
                codigoVerificacao = s;
                //mudando a interface
                mudarInterface(1);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                btnReenviar.setVisibility(View.VISIBLE);
                btnCadastro.setEnabled(false);
                campoCodigo.setEnabled(false);
            }
        };

        //passo 1 - configurando e enviando codigo de verificacao
        PhoneAuthOptions configuracoes =
                PhoneAuthOptions.newBuilder(autenticacao)
                        .setPhoneNumber(telefone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(metodosCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        //enviando codigo
        PhoneAuthProvider.verifyPhoneNumber(configuracoes);

    }

    private void verificarCodigo(String codigoDigitado){
        //cria a credencial
        PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(codigoVerificacao, codigoDigitado);
        //depois de checar envia pro cadastro
        cadastrarTelefone(credencial);
    }

    private  void cadastrarTelefone(PhoneAuthCredential credencial){
        autenticacao.signInWithCredential( credencial ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //após cadastrar redireciona pra mainactivity
                    proximaEtapa();

                } else {
                    Log.i("ALERTAFalhou: ", task.getException().getMessage());
                }
            }
        });

    }

    private void mudarInterface(int estado){

        if(estado == 0){//se pediu para reenviar o código
            campoTelefone.setEnabled(true);
            campoCodigo.setVisibility(View.GONE);
            btnCadastro.setText(getText(R.string.enviar_codigo));
            btnCadastro.setEnabled(true);
            btnCadastro.setOnClickListener(view -> validarCampoTel(view));
            btnReenviar.setVisibility(View.GONE);
        }

        if(estado == 1) {//pedir pro usuario digitar o codigo
            campoTelefone.setEnabled(false);
            campoCodigo.setVisibility(View.VISIBLE);
            campoCodigo.requestFocus();
            btnCadastro.setText("VERIFICAR CÓDIGO");
            btnCadastro.setOnClickListener(view -> {
                String codigoDigitado = campoCodigo.getText().toString();
                if (!codigoDigitado.isEmpty()) {
                    verificarCodigo(codigoDigitado);
                    abrirProgressBar(true);
                } else {
                    avisoToast("Digite o código enviado por SMS!");
                }
            });
        }

    }

    private void abrirProgressBar(boolean abrir){
        if(abrir){
            progress.setVisibility(View.VISIBLE);
        }else{
            progress.setVisibility(View.GONE);
        }
    }

    private void avisoToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    private void proximaEtapa(){
        Intent i = new Intent(CadastroTelefoneActivity.this, CadastroFinalizarActivity.class);
        i.putExtra("telefone", telefone);
        startActivity(i);
        finish();
    }

    private void redirecionar(){
        startActivity(new Intent(CadastroTelefoneActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(autenticacao.getCurrentUser()!=null){
            redirecionar();
        }
    }
}

// +1 555-521-5554