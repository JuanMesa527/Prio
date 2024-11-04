package unipiloto.edu.co.prio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private PrioDatabaseHelper dbHelper;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        dbHelper = new PrioDatabaseHelper(this);
        tableLayout = findViewById(R.id.main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadUserData();
    }
    private void loadUserData() {
        List<User> users = dbHelper.getAllUsers(); // Assuming you have a method to get all users
        for (User user : users) {
            TableRow row = new TableRow(this);


            TextView emailTextView = new TextView(this);
            emailTextView.setText(user.getEmail());
            TableRow.LayoutParams emailParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
            emailTextView.setLayoutParams(emailParams);
            row.addView(emailTextView);

            TextView roleTextView = new TextView(this);
            switch (user.getRole()) {
                case 1:
                    roleTextView.setText("Ciudadano");
                    break;
                case 2:
                    roleTextView.setText("Planeador");
                    break;
                case 3:
                    roleTextView.setText("Decisor");
                    break;
            }
            TableRow.LayoutParams roleParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            roleTextView.setLayoutParams(roleParams);
            row.addView(roleTextView);

            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setOrientation(LinearLayout.VERTICAL);
            TableRow.LayoutParams actionsParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
            actionsLayout.setLayoutParams(actionsParams);

            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            Button role1Button = new Button(this);
            role1Button.setText("Ciudadano");
            role1Button.setTextSize(8);
            role1Button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            role1Button.setOnClickListener(v -> assignRole(user.getId(), 1));
            horizontalLayout.addView(role1Button);

            Button role2Button = new Button(this);
            role2Button.setText("Planeador");
            role2Button.setTextSize(8);
            role2Button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            role2Button.setOnClickListener(v -> assignRole(user.getId(), 2));
            horizontalLayout.addView(role2Button);

            actionsLayout.addView(horizontalLayout);

            Button role3Button = new Button(this);
            role3Button.setText("Decisor");
            role3Button.setTextSize(8);
            role3Button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            role3Button.setOnClickListener(v -> assignRole(user.getId(), 3));
            actionsLayout.addView(role3Button);

            row.addView(actionsLayout);
            tableLayout.addView(row);
        }
    }

    private void assignRole(int userId, int role) {
        dbHelper.updateUserRole(userId, role);
        recreate();
    }
}
