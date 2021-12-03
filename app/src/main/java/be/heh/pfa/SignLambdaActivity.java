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

public class SignLambdaActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText user_et_name;
    private EditText user_et_firstname;
    private EditText user_et_email;
    private EditText user_et_pw;
    private EditText user_et_pwconf;
    private Button user_btn_register;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_lambda);

        db = new DatabaseHelper(this);

        user_et_name = findViewById(R.id.user_et_name);
        user_et_firstname = findViewById(R.id.user_et_firstname);
        user_et_email = findViewById(R.id.user_et_email);
        user_et_pw = findViewById(R.id.user_et_pw);
        user_et_pwconf = findViewById(R.id.user_et_pwconfirm);
        user_btn_register = findViewById(R.id.user_btn_register);

        user_btn_register.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.user_btn_register) {
            String name = user_et_name.getText().toString().trim();
            String firstname = user_et_firstname.getText().toString().trim();
            String email = user_et_email.getText().toString().trim();
            String pwd = user_et_pw.getText().toString().trim();
            String pwdconf = user_et_pwconf.getText().toString().trim();
            String role = "lambda";
            String perm = "R";

            if (validate()) {
                if (pwd.equals(pwdconf)) {


                    long val = db.addUser(name, firstname, email, pwd, role, perm);
                    if (val > 0) {
                        Toast.makeText(SignLambdaActivity.this, "Enregistrement réussi", Toast.LENGTH_LONG).show();
                        user_et_name.setText("");
                        user_et_firstname.setText("");
                        user_et_email.setText("");
                        user_et_pw.setText("");
                        user_et_pwconf.setText("");
                        Intent goToLogin = new Intent(SignLambdaActivity.this, LoginActivity.class);
                        startActivity(goToLogin);
                        finish();
                    } else {
                        Toast.makeText(SignLambdaActivity.this, "Erreur d'enregistrement :  une adresse mail identique existe déjà !", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(SignLambdaActivity.this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    ;


    public void onStart() {

        super.onStart();

    }

    private boolean validate() {
        boolean isValid = false;

        String name = user_et_name.getText().toString();
        String firstname = user_et_firstname.getText().toString();
        String email = user_et_email.getText().toString();
        String pw = user_et_pw.getText().toString();


        if (!name.isEmpty()) {
            if (!firstname.isEmpty()) {
                if (!email.isEmpty()) {
                    if (isValidEmail(email)) {
                        if (!pw.isEmpty() && pw.length() > 7) {
                            if (isValidPassword(pw)) {
                                isValid = true;


                            } else
                                user_et_pw.setError("Entrez au moins un chiffre, une lettre majuscule, et un caractère spécial");
                        } else user_et_pw.setError("Entrez au minimum 8 caractères");
                    } else
                        user_et_email.setError("Entrez une adresse mail valide");
                } else user_et_email.setError("Entrez une adresse mail");
            } else user_et_firstname.setError("Entrez un prénom");
        } else user_et_name.setError("Entrez un nom");

        return isValid;
    }


    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&+=*])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[a-z0-9._-]+@[a-z0-9.-].+[a-z]{2,4}$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}