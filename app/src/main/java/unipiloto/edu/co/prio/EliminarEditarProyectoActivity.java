package unipiloto.edu.co.prio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
}