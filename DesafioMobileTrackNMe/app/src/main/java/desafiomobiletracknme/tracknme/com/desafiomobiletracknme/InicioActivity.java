package desafiomobiletracknme.tracknme.com.desafiomobiletracknme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class InicioActivity extends AppCompatActivity {

    private final int TAM_DATA = 3;

    private RadioButton radioGeral;
    private RadioButton radioDatado;
    private EditText textoData;
    private Button botaoConsultar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        radioGeral = (RadioButton) findViewById(R.id.radioGeral);
        radioDatado = (RadioButton) findViewById(R.id.radioDatado);
        textoData = (EditText) findViewById(R.id.textoData);
        botaoConsultar = (Button) findViewById(R.id.botaoConsultar);
        botaoConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!radioGeral.isChecked() && !radioDatado.isChecked())
                    Toast.makeText(InicioActivity.this, "Por favor, escolha uma opção" +
                            " para consultar os trajetos.", Toast.LENGTH_LONG).show();
                Intent it = new Intent(InicioActivity.this, MainActivity.class);
                if(radioGeral.isChecked()) {
                    textoData.setText("");
                    startActivity(it);
                }
                else if(radioDatado.isChecked()) {
                    String dia = textoData.getText().toString();
                    if(!dia.equals("") && validaData(dia)) {
                        String[] diaSplit = dia.split("/");
                        String diaBD = diaSplit[2] + "-" + diaSplit[1] + "-" + diaSplit[0];
                        it.putExtra("data", diaBD);
                        startActivity(it);
                    }
                    else Toast.makeText(InicioActivity.this, "Por favor, insira" +
                            " a data corretamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validaData(String data) {

        if(!(data.substring(2, 3).equals("/") && data.substring(5, 6).equals("/")))
            return false;

        String[] diaStr = data.split("/");
        int[] dia = new int[TAM_DATA];
        for(int i = 0; i < dia.length; i++) dia[i] = Integer.parseInt(diaStr[i]);
        if(dia[0] <= 0 || dia[1] > 12 || dia[1] <= 0) return false;
        switch (dia[1]) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if(dia[0] > 31) return false;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if(dia[0] > 30) return false;
                break;
            case 2:
                if((dia[2] % 4 == 0 && dia[2] % 100 != 0) || dia[2] % 400 == 0) {
                    if(dia[0] > 29) return false;
                }
                else if(dia[0] > 28) return false;
        }
        return true;

    }
}
