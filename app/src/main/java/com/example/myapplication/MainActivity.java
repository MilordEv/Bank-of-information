package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnMoveToRegistration, btnLogIn;

    EditText loginEmail, password;

    TextView errorMesage;

    ImageView imageHello;

    DatabaseHelper databaseHelper;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMoveToRegistration = (Button) findViewById(R.id.btnRegistration);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.password);

        errorMesage = (TextView) findViewById(R.id.errorMesage);

        imageHello = (ImageView) findViewById(R.id.imageHello);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        databaseHelper.create_db();

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        loginEmail.setText("");
        password.setText("");
    }

    public void moveToRegistration(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void moveToLastSolutions(View view) {

        String loginEmailValue = loginEmail.getText().toString();
        String passwordValue = password.getText().toString();

        String error = "";
        boolean errorFlag = true;

        errorMesage.setVisibility(View.GONE);

        if (loginEmailValue.equals("")) {
            error += "\n- вы не ввели имя пользователя";
            errorFlag = false;
        }

        if (passwordValue.equals("")) {
            error += "\n- вы не ввели пароль";
            errorFlag = false;
        }

        if (errorFlag) {

            errorFlag = false;

            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int idName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int idemail = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);

                do {
                    if (cursor.getString(idName).equals(loginEmailValue) || cursor.getString(idemail).equals(loginEmailValue)) {
                        errorFlag = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

            if (errorFlag) {

                errorFlag = false;

                cursor = db.query(DatabaseHelper.TABLE_USERS, null, null, null, null, null, null);

                int id_user = 0;

                if (cursor.moveToFirst()) {
                    id_user = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USER);
                    int idName = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                    int idemail = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
                    int idpassword = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);

                    do {
                        if ((cursor.getString(idName).equals(loginEmailValue) || cursor.getString(idemail).equals(loginEmailValue)) && cursor.getString(idpassword).equals(passwordValue)) {
                            id_user = cursor.getInt(id_user);
                            errorFlag = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();

                if (errorFlag) {

                    Intent intent = new Intent(this, LastSolutions.class);
                    intent.putExtra("id_user", id_user);
                    startActivity(intent);

                } else {
                    error += "\n- была допущена ошибка в имени или пароле";
                }
            } else {
                error += "\n- пользователь с таким  именем не найден";
            }
        }

        if (!errorFlag) {
            error = "При регистрации возникли ошибки:" + error;
            errorMesage.setText(error);
            errorMesage.setPaddingRelative(20, 20, 20, 20);
            imageHello.setImageResource(R.drawable.graymag);
            errorMesage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение
        db.close();
    }

}