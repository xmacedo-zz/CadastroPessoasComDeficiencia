package br.com.cadastropessoascomdeficiencia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FormActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editDtNascimento;
    private EditText editEmail;
    private EditText editTelefone;
    private CheckBox checkDefFisica;
    private CheckBox checkDefAuditiva;
    private CheckBox checkDefVisual;
    private CheckBox checkDefMental;
    private RadioButton radioMasculino;
    private RadioButton radioFeminino;
    private EditText editDetalhesDef;
    private EditText editEnderecoCompleto;
    private EditText editCidadeUF;
    private EditText editCep;
    private ProgressDialog progressDialog;

    private static String URL_REGIST = "http://192.168.0.106/android/save_form.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().hide();

        editNome = (EditText) findViewById(R.id.nome);
        editDtNascimento = (EditText) findViewById(R.id.dataNascimento);
        editEmail = (EditText) findViewById(R.id.email);
        editTelefone = (EditText) findViewById(R.id.telefone);
        radioMasculino = (RadioButton) findViewById(R.id.rdb_masculino);
        radioFeminino = (RadioButton) findViewById(R.id.rdb_feminino);
        checkDefFisica = (CheckBox) findViewById(R.id.checkDefFisica);
        checkDefAuditiva = (CheckBox) findViewById(R.id.checkDefAuditiva);
        checkDefVisual = (CheckBox) findViewById(R.id.checkDefVisual);
        checkDefMental = (CheckBox) findViewById(R.id.checkDefMental);
        editDetalhesDef = (EditText) findViewById(R.id.detalhesDeficiencia);
        editEnderecoCompleto = (EditText) findViewById(R.id.endereco);
        editCidadeUF = (EditText) findViewById(R.id.cidadeUf);
        editCep = (EditText) findViewById(R.id.cep);

        editCep.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TemConexao()) {
                    String cepEntrada = editCep.getText().toString();
                    String urlapi = "https://viacep.com.br/ws/" + cepEntrada + "/json/";
                    if (cepEntrada.length() == 9) {
                        progressDialog = ProgressDialog.show(FormActivity.this, "Caregando . . . ", "", true);
                        progressDialog.setCancelable(true);
                        new ProcessJSON().execute(urlapi);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor verifique a conectividade de seu dispositivo.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            return stream;
        }

        protected void onPostExecute(String stream) {
            //Toast.makeText(getApplicationContext(), "stream -" + stream, Toast.LENGTH_SHORT).show();

            if (stream != null) {
                try {
                    JSONObject reader = new JSONObject(stream);

                    String logradouro = reader.getString("logradouro");
                    String localidade = reader.getString("localidade");
                    String uf = reader.getString("uf");

                    progressDialog.dismiss();
                    if (logradouro != " ") {
                        editEnderecoCompleto.setText(logradouro);
                    } else {
                        editEnderecoCompleto.setText("SEM ENDERECO");
                    }
                    if (localidade != " ") {
                        if (uf != " ") {
                            editCidadeUF.setText(localidade + "/" + uf);
                        } else {
                            editCidadeUF.setText(localidade);
                        }

                    } else {
                        editCidadeUF.setText("");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean TemConexao() {
        boolean lblnRet = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                lblnRet = true;
            } else {
                lblnRet = false;
            }
        } catch (Exception e) {

        }
        return lblnRet;
    }


    public void btnSaveOnClick(View v) {
        if (validaCamposPreenchidos()) {
            Regist();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(FormActivity.this, "Você precisa preencher todos os campos do formulário para ENVIAR.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validaCamposPreenchidos() {
        if (this.editNome.getText().toString().equals("")) {
            return false;
        } else if (this.editDtNascimento.getText().toString().equals("")) {
            return false;
        } else if (this.editEmail.getText().toString().equals("")) {
            return false;
        } else if (this.editTelefone.getText().toString().equals("")) {
            return false;
        } else if (!radioMasculino.isChecked() && !radioFeminino.isChecked()) {
            return false;
        } else if (getTiposDeficiencias().equals("")) {
            return false;
        } else if (this.editDetalhesDef.getText().toString().equals("")) {
            return false;
        } else if (this.editEnderecoCompleto.getText().toString().equals("")) {
            return false;
        } else if (this.editCidadeUF.getText().toString().equals("")) {
            return false;
        } else if (this.editCep.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    public void findAddressByCepChange(View v) {
        Bundle resultadoQuiz = new Bundle();
        //resultadoQuiz.putInt("idSigIn", idSigin);
    }

    private void Regist() {

        final String var_nome = this.editNome.getText().toString();
        final String var_dtNascimento = this.editDtNascimento.getText().toString();
        final String var_email = this.editEmail.getText().toString();
        final String var_telefone = this.editTelefone.getText().toString();
        final String var_sexo = radioMasculino.isChecked() ? "Masculino" : "Feminino";
        final String var_tiposDef = getTiposDeficiencias();
        final String var_detalhes = this.editDetalhesDef.getText().toString();
        final String var_endereco = this.editEnderecoCompleto.getText().toString();
        final String var_cidadeUf = this.editCidadeUF.getText().toString();
        final String var_cep = this.editCep.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(FormActivity.this, "Cadastro Salvo com Sucesso!", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(FormActivity.this, "ERRO: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FormActivity.this, "Volley ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nome", var_nome);
                params.put("nascimento", var_dtNascimento);
                params.put("email", var_email);
                params.put("telefone", var_telefone);
                params.put("detalhes", var_detalhes);
                params.put("endereco", var_endereco);
                params.put("cidade", var_cidadeUf);
                params.put("cep", var_cep);
                params.put("sexo", var_sexo);
                params.put("tipos_def", var_tiposDef);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String getTiposDeficiencias() {
        String result = "";

        boolean var_defFisica = this.checkDefFisica.isChecked();
        boolean var_defVisual = this.checkDefVisual.isChecked();
        boolean var_defAuditiva = this.checkDefAuditiva.isChecked();
        boolean var_defMental = this.checkDefMental.isChecked();

        if (var_defFisica) {
            result = "Fisica";
        }

        if (var_defAuditiva) {
            result = result.equals("") ? "Auditiva" : result + ", Auditiva";
        }

        if (var_defVisual) {
            result = result.equals("") ? "Visual" : result + ", Visual";
        }

        if (var_defMental) {
            result = result.equals("") ? "Mental" : result + ", Mental";
        }
        return result;
    }


}
