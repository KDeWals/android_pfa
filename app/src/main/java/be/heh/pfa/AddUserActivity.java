package be.heh.pfa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText add_user_et_name;
    private EditText add_user_et_firstname;
    private EditText add_user_et_email;
    private EditText add_user_et_pw;
    private EditText add_user_et_pwconf;
    private CheckBox add_user_cb_rw_access_rights;
    private Button user_btn_add;
    private String perm = "R";
    private String email;
    private String role;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        role = bundle.getString("role");

        add_user_et_name = findViewById(R.id.add_user_et_name);
        add_user_et_firstname = findViewById(R.id.add_user_et_firstname);
        add_user_et_email = findViewById(R.id.add_user_et_email);
        add_user_et_pw = findViewById(R.id.add_user_et_pw);
        add_user_et_pwconf = findViewById(R.id.add_user_et_pwconfirm);
        add_user_cb_rw_access_rights = findViewById(R.id.add_user_perm);
        user_btn_add = findViewById(R.id.user_btn_add);
        user_btn_add.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void onClick(View v) {

        if (v.getId() == R.id.user_btn_add) {
            String name = add_user_et_name.getText().toString().trim();
            String firstname = add_user_et_firstname.getText().toString().trim();
            String email = add_user_et_email.getText().toString().trim();
            String pwd = add_user_et_pw.getText().toString().trim();
            String pwdconf = add_user_et_pwconf.getText().toString().trim();
            String role = "lambda";
            if(add_user_cb_rw_access_rights.isChecked()) perm = "RW";


            if (validate()) {
                if (pwd.equals(pwdconf)) {
                    String tmppass = null;
                    try {
                        tmppass = PBKDF2_Encrypt(pwd);
                        Log.i("AddUserActivity", tmppass);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                    long val = db.addUser(name, firstname, email, pwd, role, perm);
                    if (val > 0) {
                        Toast.makeText(AddUserActivity.this, "Utilisateur ajouté !", Toast.LENGTH_LONG).show();
                        Intent goToManageUsers = new Intent(AddUserActivity.this, ManageUsersActivity.class);
                        finish();
                        goToManageUsers.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        goToManageUsers.putExtra("email", email);
                        goToManageUsers.putExtra("role", role);
                        startActivity(goToManageUsers);


                    } else {
                        Toast.makeText(AddUserActivity.this, "Erreur d'ajout :  une adresse mail identique existe déjà !", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(AddUserActivity.this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_LONG).show();
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

        String name = add_user_et_name.getText().toString();
        String firstname = add_user_et_firstname.getText().toString();
        String email = add_user_et_email.getText().toString().trim();
        String pw = add_user_et_pw.getText().toString().trim();


        if (!name.isEmpty()) {
            if (!firstname.isEmpty()) {
                if (!email.isEmpty()) {
                    if (isValidEmail(email)) {
                        if (!pw.isEmpty() && pw.length() > 7) {
                            if (isValidPassword(pw)) {
                                isValid = true;


                            } else add_user_et_pw.setError("Entrez au moins un chiffre, une lettre majuscule, et un caractère spécial");
                        } else add_user_et_pw.setError("Entrez au minimum 8 caractères");
                    } else add_user_et_email.setError("Entrez une adresse mail valide");
                } else add_user_et_email.setError("Entrez une adresse mail");
            } else add_user_et_firstname.setError("Entrez un prénom");
        } else add_user_et_name.setError("Entrez un nom");

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String PBKDF2_Encrypt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return new String(hash, StandardCharsets.UTF_8);
    }
}