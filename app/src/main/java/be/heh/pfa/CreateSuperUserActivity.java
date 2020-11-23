package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateSuperUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText superuser_et_name;
    private EditText superuser_et_firstname;
    private EditText superuser_et_email;
    private EditText superuser_et_pw;
    private EditText superuser_et_pwconf;
    private Button superuser_btn_register;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_superadmin);

        db = new DatabaseHelper(this);

        superuser_et_name = findViewById(R.id.superuser_et_name);
        superuser_et_firstname = findViewById(R.id.superuser_et_firstname);
        superuser_et_email = findViewById(R.id.superuser_et_email);
        superuser_et_pw = findViewById(R.id.superuser_et_pw);
        superuser_et_pwconf = findViewById(R.id.superuser_et_pwconfirm);
        superuser_btn_register = findViewById(R.id.superuser_btn_register);

        superuser_btn_register.setOnClickListener(this);
    }

            public void onClick(View v) {

                if (v.getId() == R.id.superuser_btn_register) {
                    String name = superuser_et_name.getText().toString().trim();
                    String firstname = superuser_et_firstname.getText().toString().trim();
                    String email = superuser_et_email.getText().toString().trim();
                    String pwd = superuser_et_pw.getText().toString().trim();
                    String pwdconf = superuser_et_pwconf.getText().toString().trim();
                    String role = "admin";
                    String perm = "RW";

                    if (validate()) {
                        if (pwd.equals(pwdconf)) {


                            long val = db.addUser(name, firstname, email, pwd, role, perm);
                            if (val > 0) {
                                Toast.makeText(CreateSuperUserActivity.this, "Enregistrement réussi", Toast.LENGTH_LONG).show();
                                superuser_et_name.setText("");
                                superuser_et_firstname.setText("");
                                superuser_et_email.setText("");
                                superuser_et_pw.setText("");
                                superuser_et_pwconf.setText("");
                                Intent goToLogin = new Intent(CreateSuperUserActivity.this, LoginActivity.class);
                                startActivity(goToLogin);
                                finish();
                            } else {
                                Toast.makeText(CreateSuperUserActivity.this, "Erreur d'enregistrement :  une adresse mail identique existe déjà !", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(CreateSuperUserActivity.this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };


    public void onStart() {

        super.onStart();

    }

    private boolean validate() {
        boolean isValid = false;

        String name = superuser_et_name.getText().toString();
        String firstname = superuser_et_firstname.getText().toString();
        String email = superuser_et_email.getText().toString();
        String pw = superuser_et_pw.getText().toString();


        if (!name.isEmpty()) {
            if (!firstname.isEmpty()) {
                if (!email.isEmpty()) {
                    if (email.length() > 10) {
                        if (!pw.isEmpty() && pw.length() > 12) {
                            if (isValidPassword(pw)) {
                                isValid = true;


                            } else superuser_et_pw.setError("Entrez au moins un chiffre, une lettre majuscule, et un caractère spécial");
                        } else superuser_et_pw.setError("Entrez au minimum 12 caractères");
                    } else superuser_et_email.setError("Entrez une adresse mail comptant au moins 10 caractères ");
                } else superuser_et_email.setError("Entrez une adresse mail");
            } else superuser_et_firstname.setError("Entrez un prénom");
        } else superuser_et_name.setError("Entrez un nom");

        return isValid;
    }



    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&+=*])(?=\\S+$).{12,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
//
//    public static SecretKey generateKey(char[] pass, byte[] salt){
//        final int iterations = 1200;
//        final int outputKeyLength = 256;
//
//        SecretKeyFactory secretKeyFactory = null;
//        try {
//            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA2");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        KeySpec keySpec = new PBEKeySpec(pass, salt, iterations, outputKeyLength);
//        try {
//            return secretKeyFactory.generateSecret(keySpec);
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//    }

    // TODO - redo this method
//    public boolean isValidname(final String password) {
//
//        Pattern pattern;
//        Matcher matcher;
//
//        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$";
//
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(password);
//
//        return matcher.matches();
//
//    }
                }

