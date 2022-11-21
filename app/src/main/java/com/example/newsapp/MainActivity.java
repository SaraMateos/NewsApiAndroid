package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newsapp.Models.NewsApiResponse;
import com.example.newsapp.Models.NewsHeadlines;

import java.util.List;

//TODO: txt
//TODO: webView --> https://www.youtube.com/watch?v=e0qKG8cUAp8

public class MainActivity extends AppCompatActivity implements SelectListener, View.OnClickListener {

    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btnSave, btnShow, btnDelete;
    SearchView searchView;
    SharedPreferences sharedPreferences;
    public static final String myPreference = "myPredf";
    public static final String Busqueda = "busquedaKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);

        //Realiza la busqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                dialog.setTitle(getString(R.string.progressDialogCargaCategoria) + query);
                dialog.show();

                RequestManager manager = new RequestManager(MainActivity.this);
                manager.getNewsHeadlines(listener, "general", query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.progressDialogInicio);
        dialog.show();

        btn1 = findViewById(R.id.btn_1);
        btn1.setOnClickListener(this);
        btn2 = findViewById(R.id.btn_2);
        btn2.setOnClickListener(this);
        btn3 = findViewById(R.id.btn_3);
        btn3.setOnClickListener(this);
        btn4 = findViewById(R.id.btn_4);
        btn4.setOnClickListener(this);
        btn5 = findViewById(R.id.btn_5);
        btn5.setOnClickListener(this);
        btn6 = findViewById(R.id.btn_6);
        btn6.setOnClickListener(this);
        btn7 = findViewById(R.id.btn_7);
        btn7.setOnClickListener(this);

        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, "general", null);

        //Shared Preferences
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(Busqueda)) {
            searchView.setQuery(sharedPreferences.getString(Busqueda, ""), false);
        }

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Delete();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
            }
        });

        btnShow = (Button) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show();
            }
        });
    }

    //Metodos SharedPreferences
    public void Delete() {
        searchView.setQuery("", false);
    }

    public void Show() {
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(Busqueda)) {
            searchView.setQuery(sharedPreferences.getString(Busqueda, ""), false);
        }
    }

    public void Save() {
        String b = searchView.getQuery().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Busqueda, b);
        editor.commit();
    }

    //A la hora de buscar, mostrará un Toast en caso de que ocurra un error o no encuentre datos
    //Sino, mostrará las noticias
    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if (list.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.toastNoData, Toast.LENGTH_SHORT).show();
            } else {
                showNews(list);
                dialog.dismiss();
            }
        }

        @Override
        public void onError(int message) {
            Toast.makeText(MainActivity.this, R.string.toastError, Toast.LENGTH_SHORT).show();
        }
    };

    //Muestra la lista de noticias
    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.recylcer_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    //Al clicar en una noticia muestra parte de su contenido
    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(MainActivity.this, DetailsActivity.class).putExtra("data", headlines));
    }

    //Al clicar sobre un botón de la categoria, sale un ProgessDialog para saber que está cargando
    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String category = button.getText().toString();

        dialog.setTitle(getString(R.string.progressDialogCargaCategoria) + category);
        dialog.show();

        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, category, null);
    }
}