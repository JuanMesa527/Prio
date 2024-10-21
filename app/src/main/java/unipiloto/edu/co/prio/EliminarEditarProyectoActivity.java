package unipiloto.edu.co.prio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class EliminarEditarProyectoActivity extends AppCompatActivity {

    private PrioDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);



        setContentView(R.layout.activity_eliminar_editar_proyecto);
        dbHelper = new PrioDatabaseHelper(this);
        mostrarProyecto();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_anadir);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_icon) {
            logout(null);
            return true;
        } else if (item.getItemId() == R.id.map_icon) {
            Intent mapIntent = new Intent(EliminarEditarProyectoActivity.this, MapsActivity.class);
            startActivity(mapIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void mostrarProyecto() {
        List<String> proyectos = dbHelper.getAllNameProjects();
        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No hay proyectos", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView id_proyectos = findViewById(R.id.id_proyectos);
        for (String proyecto : proyectos) {
            id_proyectos.append(proyecto + "\n");
        }
    }
    public void eliminar_proyecto(View view) {
        EditText id = findViewById(R.id.idProyectos);
        String idText = id.getText().toString();

        if (idText.isEmpty() ) {
            Toast.makeText(this, "Por favor, complete el id del Project", Toast.LENGTH_SHORT).show();
            return;
        }
        int idInt = Integer.parseInt(idText);
        boolean eliminado = dbHelper.deleteProject(idInt);
        if (eliminado) {
            Toast.makeText(this, "Proyecto Eliminado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EliminarEditarProyectoActivity.this, ManageProyectActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Id de proyecto no existente", Toast.LENGTH_SHORT).show();
        }
    }

    public void editar_proyecto(View view) {
        EditText id = findViewById(R.id.idProyectos);
        String idText = id.getText().toString();

        if (idText.isEmpty() ) {
            Toast.makeText(this, "Por favor, complete el id del Project", Toast.LENGTH_SHORT).show();
            return;
        }
        int idInt = Integer.parseInt(idText);
        boolean isProject = dbHelper.isProject(idInt);
        if (isProject) {
            Intent intent = new Intent(EliminarEditarProyectoActivity.this, EditarProyectoActivity.class);
            intent.putExtra("id", idText);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Id de proyecto no existente", Toast.LENGTH_SHORT).show();
        }
    }
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.apply();

        Intent loginIntent = new Intent(EliminarEditarProyectoActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}