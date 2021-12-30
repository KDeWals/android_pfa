package be.heh.pfa;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_welcome;
    private EditText et_ap_name;
    private EditText et_ap_firstname;
    private EditText et_ap_email;
    private EditText et_ap_password;
    private EditText et_ap_password_confirm;
    private Button btn_ap_update_name;
    private Button btn_ap_update_firstname;
    private Button btn_ap_update_password;
    private String email;
    private User user;

    DatabaseHelper db;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");

        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        et_ap_name = (EditText) findViewById(R.id.et_ap_name);
        et_ap_firstname = (EditText) findViewById(R.id.et_ap_firstname);
        et_ap_email = (EditText) findViewById(R.id.et_ap_email);
        et_ap_password = (EditText) findViewById(R.id.et_ap_password_new);
        et_ap_password_confirm = (EditText) findViewById(R.id.et_ap_password_new_confirm);
        btn_ap_update_name = (Button) findViewById(R.id.btn_ap_name_apply);
        btn_ap_update_name.setOnClickListener(this);
        btn_ap_update_firstname = (Button) findViewById(R.id.btn_ap_firstname_apply);
        btn_ap_update_firstname.setOnClickListener(this);
        btn_ap_update_password = (Button) findViewById(R.id.btn_ap_password_apply);
        btn_ap_update_password.setOnClickListener(this);
        et_ap_name.addTextChangedListener(textWatcherName);
        et_ap_firstname.addTextChangedListener(textWatcherFirstname);
        et_ap_password.addTextChangedListener(textWatcherPassword);

        user = db.getUserInfo(email);

        String str = tv_welcome.getText().toString();
        tv_welcome.setText(String.format("%s%s", str, user.getName()));
        if(user.getRole().equals("admin")) tv_welcome.setTextColor(Color.rgb(255, 0, 0));
        et_ap_name.setText(user.getName());
        et_ap_name.setTag(user.getName());
        et_ap_firstname.setText(user.getFistname());
        et_ap_firstname.setTag(user.getFistname());
        et_ap_password.setTag("password");

        et_ap_email.setText(user.getEmail());

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_ap_name_apply :
                if(!et_ap_name.getText().toString().isEmpty()){
                    db.changeUserName(user.getEmail(), et_ap_name.getText().toString());
                    Toast.makeText(ProfileActivity.this, "Votre nom a été modifié", Toast.LENGTH_SHORT).show();
                    user.setName(et_ap_name.getText().toString());
                    btn_ap_update_name.setVisibility(View.INVISIBLE);
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                else {
                    et_ap_name.setError("Veuillez entrer un nom");
                }
                break;

            case R.id.btn_ap_firstname_apply :
                if(!et_ap_name.getText().toString().isEmpty()){
                db.changeUserFirstname(user.getEmail(), et_ap_firstname.getText().toString());
                Toast.makeText(ProfileActivity.this, "Votre prénom a été modifié", Toast.LENGTH_SHORT).show();
                user.setFirstname(et_ap_firstname.getText().toString());
                btn_ap_update_firstname.setVisibility(View.INVISIBLE);
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                }
                else {
                    et_ap_name.setError("Veuillez entrer un nom");
                }
                break;

            case R.id.btn_ap_password_apply :

                if(validate()){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setIcon(R.drawable.baseline_lock_24);
                            dialog.setTitle("Confirmation du changement de mot de passe");
                            dialog.setMessage("Entrez votre mot de passe actuel afin de confirmer");

                            final EditText password = new EditText(ProfileActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                                password.setLayoutParams(layoutParams);
                            dialog.setView(password);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            dialog.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if(validate()){
                                        if(db.checkUserLogin(user.getEmail(), password.getText().toString()) != null) {
                                            db.changeUserPassword(user.getEmail(), et_ap_password.getText().toString());
                                            Toast.makeText(ProfileActivity.this, "Votre mot de passe a été modifié", Toast.LENGTH_SHORT).show();
                                            btn_ap_update_password.setVisibility(View.INVISIBLE);
                                            finish();
                                            startActivity(getIntent());
                                            overridePendingTransition(0, 0);
                                        }
                                        else {
                                            Toast.makeText(ProfileActivity.this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            dialog.setCancelable(true);
                            dialog.setNegativeButton("Annuler", null);
                            dialog.create().show();
                }
                break;
        }
    }

    private TextWatcher textWatcherName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(et_ap_name.getTag() != null) btn_ap_update_name.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherFirstname = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(et_ap_firstname.getTag() != null) btn_ap_update_firstname.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(et_ap_password.getTag() != null) btn_ap_update_password.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public boolean validate(){
        boolean isValid = false;

        String password = et_ap_password.getText().toString();
        String passwordConfirm = et_ap_password_confirm.getText().toString();

        if(password.equals(passwordConfirm)){
            if(user.getRole().equals("admin")){

                if(!password.isEmpty() && password.length() > 12){
                    if(!passwordConfirm.isEmpty() && passwordConfirm.length() > 12){
                        if(isValidPasswordAdmin(password)){
                            if(isValidPasswordAdmin(passwordConfirm)){
                                isValid = true;
                            }
                            else et_ap_password_confirm.setError("Entrez un mot de passe comportant au moins un chiffre, une lettre majuscule et un caractère spécial");
                        }
                        else et_ap_password.setError("Entrez un mot de passe comportant au moins un chiffre, une lettre majuscule et un caractère spécial");
                    }
                    else et_ap_password_confirm.setError("Entrez un mot de passe d'au moins 13 caractères");
                }
                else et_ap_password.setError("Entrez un mot de passe d'au moins 13 caractères");

            }

        else {
            if(!password.isEmpty() && password.length() > 7){
                if(!passwordConfirm.isEmpty() && passwordConfirm.length() > 7){
                    if(isValidPasswordLambda(password)){
                        if(isValidPasswordLambda(passwordConfirm)){
                            isValid = true;
                        }
                        else et_ap_password_confirm.setError("Entrez un mot de passe comportant au moins un chiffre, une lettre majuscule et un caractère spécial");
                    }
                    else et_ap_password.setError("Entrez un mot de passe comportant au moins un chiffre, une lettre majuscule et un caractère spécial");
                }
                else et_ap_password_confirm.setError("Entrez un mot de passe d'au moins 8 caractères");
            }
            else et_ap_password.setError("Entrez un mot de passe d'au moins 8 caractères");
            }
        }
        else{
            Toast.makeText(ProfileActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
        }


        return isValid;
    }

    public boolean isValidPasswordAdmin(String pw){

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&+=*])(?=\\S+$).{12,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pw);

        return matcher.matches();

    }
    public boolean isValidPasswordLambda(String pw){

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%&+=*])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pw);

        return matcher.matches();

    }
}
