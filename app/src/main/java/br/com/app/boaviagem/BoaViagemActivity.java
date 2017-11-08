package br.com.app.boaviagem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends AppCompatActivity {

    private static final String MANTER_CONECTADO = "manter_conectado";
    private EditText usuario, senha;
    private CheckBox manterConectado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boa_viagem);
        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);

        manterConectado = (CheckBox)findViewById(R.id.manterConectado);

        SharedPreferences preferencias =
                getPreferences(MODE_PRIVATE);
        boolean conectado =
                preferencias.getBoolean(MANTER_CONECTADO, false);
        if(conectado){
            startActivity(new Intent(this, DashboardActivity.class));
        }

    }

    public void entrarOnClick(View view) {
        String usuarioDigitado = usuario.getText().toString();
        String senhaDigitada = senha.getText().toString();

        if ("magno".equals(usuarioDigitado) && "123".equals(senhaDigitada)){

            SharedPreferences preferences =
                    getPreferences(MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(MANTER_CONECTADO, manterConectado.isChecked());
            editor.commit();

            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }else{
            String erroAutenticacao = getString(R.string.erro_autenticacao);
            Toast.makeText(this, erroAutenticacao, Toast.LENGTH_SHORT).show();
        }

    }
}
