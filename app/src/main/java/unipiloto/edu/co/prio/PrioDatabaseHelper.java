package unipiloto.edu.co.prio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PrioDatabaseHelper  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "prio";
    public static final int DATABASE_VERSION = 8;


    public PrioDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Role (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL UNIQUE," +
                "DESCRIPTION TEXT NOT NULL" + ")");
        db.execSQL("CREATE TABLE Category (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL UNIQUE" + ")");
        db.execSQL("CREATE TABLE Locality (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL UNIQUE," +
                "AREA REAL NOT NULL," +
                "POPULATION INTEGER NOT NULL" + ")");
        db.execSQL("CREATE TABLE User (" +
                "ID INTEGER PRIMARY KEY," +
                "FIRST_NAME TEXT NOT NULL," +
                "LAST_NAME TEXT NOT NULL," +
                "AGE INTEGER NOT NULL," +
                "EMAIL TEXT NOT NULL UNIQUE," +
                "PASSWORD TEXT NOT NULL," +
                "ROLE_ID INTEGER NOT NULL," +
                "LOCALITY_ID INTEGER NOT NULL," +
                "FOREIGN KEY (ROLE_ID) REFERENCES Role(ID)," +
                "FOREIGN KEY (LOCALITY_ID) REFERENCES Locality(ID)" + ")");
        db.execSQL("CREATE TABLE Project (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL UNIQUE," +
                "DESCRIPTION TEXT NOT NULL," +
                "BUDGET REAL NOT NULL," +
                "START_DATE TEXT NOT NULL," +
                "END_DATE TEXT NOT NULL," +
                "CATEGORY_ID INTEGER NOT NULL," +
                "LOCALITY_ID INTEGER NOT NULL," +
                "ADDRESS TEXT NOT NULL," +
                "FOREIGN KEY (CATEGORY_ID) REFERENCES Category(ID)," +
                "FOREIGN KEY (LOCALITY_ID) REFERENCES Locality(ID)" + ")");
        db.execSQL("CREATE TABLE Vote_type (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL UNIQUE" + ")");
        db.execSQL("CREATE TABLE Vote (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID INTEGER NOT NULL," +
                "PROYECT_ID INTEGER NOT NULL," +
                "VOTE_ID INTEGER NOT NULL," +
                "OPINION TEXT," +
                "FOREIGN KEY (USER_ID) REFERENCES User(ID)," +
                "FOREIGN KEY (PROYECT_ID) REFERENCES Proyect(ID)," +
                "FOREIGN KEY (VOTE_ID) REFERENCES Vote_type(ID)" + ")");

        db.execSQL("INSERT INTO Category (NAME) VALUES ('Ambiente y Bienestar Animal')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Cultura, Recracion y Deportes')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Derechos de las mujeres')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Instalaciones sociales, escuelas y juntas de acción comunal')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Justicia, Seguridad, Paz y Convivencia')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Movilidad local')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Parques')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Participacion ciudadana')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Reactivacion economica')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Utilizacion del espacio publico')");
        db.execSQL("INSERT INTO Category (NAME) VALUES ('Atencion a riesgos y emergencias')");

        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Usaquen', 65.54, 503767)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Chapinero', 35.78, 139701)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Santa Fe', 44.82, 109195)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('San Cristobal', 48.83, 404697)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Usme', 122.63, 457302)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Tunjuelito', 10.79, 217139)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Bosa', 24.22, 681234)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Kennedy', 38.72, 1092110)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Fontibon', 33.32, 394648)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Engativa', 36.06, 887080)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Suba', 101.07, 1124692)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Barrios Unidos', 11.92, 243874)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Teusaquillo', 14.2, 153025)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Los Martires', 6.53, 99174)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Antonio Narino', 4.99, 108996)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Puente Aranda', 17.24, 258287)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('La Candelaria', 1.83, 22150)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Rafael Uribe Uribe', 13.44, 374246)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Ciudad Bolivar', 130.0, 707569)");
        db.execSQL("INSERT INTO Locality (NAME, AREA, POPULATION) VALUES ('Sumapaz', 780.96, 6529)");


        db.execSQL("INSERT INTO Role (NAME, DESCRIPTION) VALUES ('Ciudadano', 'Revisa las propuestas de proyectos de su zona de residencia')");
        db.execSQL("INSERT INTO Role (NAME, DESCRIPTION) VALUES ('Planeador', 'Encargado de gestionar las propuestas de proyectos de espacio público\n" +
                "agrupadas por zona de la ciudad (localidad)')");
        db.execSQL("INSERT INTO Role (NAME, DESCRIPTION) VALUES ('decisor', 'Consulta los resultados de las votaciones en las diversas zonas.\n" +
                "Analiza la información en un mapa según la ubicación de los votantes\n" +
                "Generar estadísticas como el impacto porcentual del Project en términos\n" +
                "del número de participantes y el número de habitantes.')");
        db.execSQL("INSERT INTO Role (NAME, DESCRIPTION) VALUES ('Admin', 'Administrador del sistema')");

        db.execSQL("INSERT INTO Vote_type (NAME) VALUES ('A Favor')");
        db.execSQL("INSERT INTO Vote_type (NAME) VALUES ('Indiferente')");
        db.execSQL("INSERT INTO Vote_type (NAME) VALUES ('En Contra')");

        db.execSQL("INSERT INTO User (ID,FIRST_NAME, LAST_NAME, AGE, EMAIL, PASSWORD, ROLE_ID, LOCALITY_ID) VALUES (1,'pruebas', 'pruebas', 20, 'pruebas@gmail.com', '123', 2, 1)");
        db.execSQL("INSERT INTO User (ID,FIRST_NAME, LAST_NAME, AGE, EMAIL, PASSWORD, ROLE_ID, LOCALITY_ID) VALUES (2,'admin', 'admin', 20, 'admin@admin.com', 'admin', 4, 1)");
        db.execSQL("INSERT INTO User (ID,FIRST_NAME, LAST_NAME, AGE, EMAIL, PASSWORD, ROLE_ID, LOCALITY_ID) VALUES (4,'decisor', 'decisor', 20, 'decisor@decisor.com', '123', 3, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TEMPORARY TABLE Role_backup AS SELECT * FROM Role");
        db.execSQL("CREATE TEMPORARY TABLE Category_backup AS SELECT * FROM Category");
        db.execSQL("CREATE TEMPORARY TABLE Locality_backup AS SELECT * FROM Locality");
        db.execSQL("CREATE TEMPORARY TABLE User_backup AS SELECT * FROM User");
        db.execSQL("CREATE TEMPORARY TABLE Project_backup AS SELECT * FROM Project");
        db.execSQL("CREATE TEMPORARY TABLE Vote_type_backup AS SELECT * FROM Vote_type");
        db.execSQL("CREATE TEMPORARY TABLE Vote_backup AS SELECT * FROM Vote");

        db.execSQL("DROP TABLE IF EXISTS Role");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Locality");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Project");
        db.execSQL("DROP TABLE IF EXISTS Vote_type");
        db.execSQL("DROP TABLE IF EXISTS Vote");

        onCreate(db);

        db.execSQL("INSERT OR IGNORE INTO Role SELECT * FROM Role_backup");
        db.execSQL("INSERT OR IGNORE INTO Category SELECT * FROM Category_backup");
        db.execSQL("INSERT OR IGNORE INTO Locality SELECT * FROM Locality_backup");
        db.execSQL("INSERT OR IGNORE INTO User SELECT * FROM User_backup");
        db.execSQL("INSERT OR IGNORE INTO Project SELECT * FROM Project_backup");
        db.execSQL("INSERT OR IGNORE INTO Vote_type SELECT * FROM Vote_type_backup");
        db.execSQL("INSERT OR IGNORE INTO Vote SELECT * FROM Vote_backup");

        db.execSQL("DROP TABLE Role_backup");
        db.execSQL("DROP TABLE Category_backup");
        db.execSQL("DROP TABLE Locality_backup");
        db.execSQL("DROP TABLE User_backup");
        db.execSQL("DROP TABLE Project_backup");
        db.execSQL("DROP TABLE Vote_type_backup");
        db.execSQL("DROP TABLE Vote_backup");
    }

    public void initData(){
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 1, 1);
    }

    public boolean insertUser(int id, String firstName, String lastName, int age, String email, String password, int roleId, int localityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("FIRST_NAME", firstName);
        contentValues.put("LAST_NAME", lastName);
        contentValues.put("AGE", age);
        contentValues.put("EMAIL", email);
        contentValues.put("PASSWORD", password);
        contentValues.put("ROLE_ID", roleId);
        contentValues.put("LOCALITY_ID", localityId);
        long result = db.insert("User", null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean insertProject(String name, String description, double budget, String startDate, String endDate, int categoryId, int localityId, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("DESCRIPTION", description);
        contentValues.put("BUDGET", budget);
        contentValues.put("START_DATE", startDate);
        contentValues.put("END_DATE", endDate);
        contentValues.put("CATEGORY_ID", categoryId);
        contentValues.put("LOCALITY_ID", localityId);
        contentValues.put("ADDRESS", address);
        long result = db.insert("Project", null, contentValues);
        if (result == 0) {
            return false;
        }
        return true;
    }

    public boolean deleteProject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Project", "ID = "+id, null);
        if (result == 0) {
            return false;
        }
        return true;
    }

    public String[] getLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE EMAIL = "+"'"+email+"'"+" AND PASSWORD = "+"'"+password+"'", null);
        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex("ID");
            int roleId = cursor.getColumnIndex("ROLE_ID");
            System.out.println("id: " + cursor.getInt(id));
            return new String[]{"true", cursor.getString(roleId), String.valueOf(cursor.getInt(id))};

        }
        return new String[]{null, null, null};
    }

    public List<String> getAllLocalities() {
        List<String> localities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NAME FROM Locality", null);
        if (cursor.moveToFirst()) {
            do {
                localities.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return localities;
    }
    public List<String> getAllNameProjects() {
        List<String> Projects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID,NAME FROM Project", null);
        if (cursor.moveToFirst()) {
            do {
                Projects.add(cursor.getString(0)+" - "+cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Projects;
    }

    public boolean updateProject(int id, String name, String description, double budget, String startDate, String endDate, int categoryId, int localityId, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("DESCRIPTION", description);
        contentValues.put("BUDGET", budget);
        contentValues.put("START_DATE", startDate);
        contentValues.put("END_DATE", endDate);
        contentValues.put("CATEGORY_ID", categoryId);
        contentValues.put("LOCALITY_ID", localityId);
        contentValues.put("ADDRESS", address);
        long result = db.update("Project", contentValues, "ID = "+id, null);
        if (result == 0) {
            return false;
        }
        return true;
    }

    public boolean isProject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Project WHERE ID = "+"'"+id+"'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public ArrayList<Project> getAllProjects() {
        ArrayList<Project> Projects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Project", null);
        if (cursor.moveToFirst()) {
            do {
                Project project = new Project(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
                Projects.add(project);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Projects;
    }

    public int getLocalityId(String localityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Locality WHERE NAME = "+"'"+localityName+"'", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public String getLocalityName(int localityId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NAME FROM Locality WHERE ID = "+"'"+localityId+"'", null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();
        return name;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NAME FROM Category", null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public int getCategoryId(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Category WHERE NAME = "+"'"+categoryName+"'", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public String getCategoryName(int CategoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NAME FROM Category WHERE ID = "+"'"+CategoryId+"'", null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();
        return name;
    }

    public Project getProjectById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Project WHERE ID = "+"'"+id+"'", null);
        cursor.moveToFirst();
        Project project = new Project(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
        cursor.close();
        db.close();
        return project;
    }

    public int getVoteTypeId(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Vote_type WHERE NAME = "+"'"+name+"'", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    public boolean insertVote(int userId, int projectId, int voteId, String opinion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER_ID", userId);
        contentValues.put("PROYECT_ID", projectId);
        contentValues.put("VOTE_ID", voteId);
        contentValues.put("OPINION", opinion);
        long result = db.insert("Vote", null, contentValues);
        if (result == 0) {
            return false;
        }
        return true;
    }

    public boolean isVoteable(int userId, int projectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Vote WHERE USER_ID = "+"'"+userId+"'"+" AND PROYECT_ID = "+"'"+projectId+"'", null);
        if (cursor.moveToFirst()) {
            return false;
        }
        return true;
    }
}
