package unipiloto.edu.co.prio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.titulo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new PrioDatabaseHelper(this);
    }

    public void login(View v) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        String[] infoLogin = dbHelper.getLogin(emailText, passwordText);
        if (infoLogin[0] != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userEmail", emailText);
            editor.putInt("userId", Integer.parseInt(infoLogin[2]));
            System.out.println("userId: " + infoLogin[2]);
            editor.putInt("userRole", Integer.parseInt(infoLogin[1]));

            editor.apply();
            if (Integer.parseInt(infoLogin[1]) == 0) {
                Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(loginIntent);
                finish();
            } else if (Integer.parseInt(infoLogin[1]) == 1) {
                Intent loginIntent = new Intent(LoginActivity.this, ManageProyectActivity.class);
                startActivity(loginIntent);
                finish();
            } //else {
                //login del decisor
//                Intent loginIntent = new Intent(LoginActivity.this, .class);
//                startActivity(loginIntent);
//                finish();
            //}
        } else {
            Toast.makeText(this, "Credenciales incorrectas o usuario inexistente", Toast.LENGTH_SHORT).show();
        }
    }
}