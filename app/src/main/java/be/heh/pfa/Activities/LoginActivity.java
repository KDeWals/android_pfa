package be.heh.pfa.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import be.heh.pfa.DatabaseHelper;
import be.heh.pfa.R;
import be.heh.pfa.User;


public class LoginActivity extends AppCompatActivity {


    private EditText login_et_email;
    private EditText login_et_pw;
    private Button login_btn_login;
    private Button login_btn_register;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_et_email =(EditText) findViewById(R.id.login_et_email);
        login_et_pw = (EditText) findViewById(R.id.login_et_pw);
        login_btn_login = (Button) findViewById(R.id.login_btn_login);
        login_btn_register = (Button) findViewById(R.id.login_btn_register);


        db = new DatabaseHelper(this);



        if(db.countUsers() < 1) {
            showStartDialog();
        }
        


        login_btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister = new Intent(LoginActivity.this, SignLambdaActivity.class);
                startActivity(goToRegister);
            }
        });

        login_btn_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {




                String email = login_et_email.getText().toString().trim();
                String pwd = login_et_pw.getText().toString().trim();
                String tmppass = null;
                /*
                try {
                    tmppass = PBKDF2_Encrypt(pwd);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                 */
                User userLogin = db.checkUserLogin(email,pwd);


                if(validate())
                {
                    if(userLogin != null) {


                        if(userLogin.getRole().compareTo(getResources().getString(R.string.admin)) == 0) {


                            login_et_email.setText("");
                            login_et_pw.setText("");

                            Intent goToHomeA = new Intent(LoginActivity.this, HomeAdminActivity.class);
                            goToHomeA.putExtra("email", email);
                            goToHomeA.putExtra("role", getResources().getString(R.string.admin));
                            finish();
                            startActivity(goToHomeA);
                            Toast.makeText(LoginActivity.this, "Vous êtes connecté. Bienvenue " + userLogin.getFistname(), Toast.LENGTH_SHORT).show();


                        }
                        else {


                                login_et_email.setText("");
                                login_et_pw.setText("");

                                Intent goToHomeL = new Intent(LoginActivity.this, HomeLambdaActivity.class);
                                goToHomeL.putExtra("email", email);
                                goToHomeL.putExtra("role", getResources().getString(R.string.lambda));
                                finish();
                                startActivity(goToHomeL);
                                Toast.makeText(LoginActivity.this, "Vous êtes connecté. Bienvenue " + userLogin.getFistname(), Toast.LENGTH_SHORT).show();

                        }

                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Veuillez vérifier votre adresse mail ou mot de passe", Toast.LENGTH_SHORT).show();

                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(300);
                    }
                }

            }

        });
    }

    private boolean validate() {
        boolean isValid = true;

        String email = login_et_email.getText().toString().toLowerCase();
        String pw = login_et_pw.getText().toString();

        if(email.isEmpty()){
            login_et_email.setError("Entrez une adresse email");
            isValid = false;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
        else {
            login_et_email.setError(null);
        }
        if(pw.isEmpty()){
            login_et_pw.setError("Entrez un mot de passe");
            isValid = false;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
        else {
            login_et_pw.setError(null);
        }

        return isValid;
    }

    private void showStartDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Compte super-utilisateur requis")
                .setMessage("Vous serez redirigé afin d'en créer un.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        finish();
                        Intent superAdminIntent = new Intent(LoginActivity.this, CreateSuperUserActivity.class);
                        startActivity(superAdminIntent);
                    }
                })
                .setCancelable(false)
                .create().show();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String PBKDF2_Encrypt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return new String(hash, StandardCharsets.UTF_8);
    }
*/


}
