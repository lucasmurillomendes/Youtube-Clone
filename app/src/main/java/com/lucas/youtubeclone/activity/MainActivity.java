package com.lucas.youtubeclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.youtubeclone.R;
import com.lucas.youtubeclone.adapter.AdapterVideo;
import com.lucas.youtubeclone.api.YoutubeService;
import com.lucas.youtubeclone.helper.RetrifitConfig;
import com.lucas.youtubeclone.helper.YoutubeConfig;
import com.lucas.youtubeclone.listener.RecyclerItemClickListener;
import com.lucas.youtubeclone.model.Item;
import com.lucas.youtubeclone.model.Resultado;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerVideos;

    private List<Item> videos = new ArrayList<>();
    private Resultado resultado;
    private AdapterVideo adapterVideo;

    private MaterialSearchView searchView;

    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("YouTube");
        setSupportActionBar(toolbar);

        //inicializar Componentes
        recyclerVideos = findViewById(R.id.recyclerVideos);
        searchView = findViewById(R.id.searchView);
        //Retrofit
        retrofit = RetrifitConfig.getRetrofit();
        //recupera os videos

        //Configurando metodos search view
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /**
                 * como é uma pesquisa grande, o melhor caminho é esperar a confirmação do usuario
                 */
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recuperarVideos(newText);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        recuperarVideos("");

    }

    private void recuperarVideos(String pesquisa) {

        String query = pesquisa.replaceAll(" ", "+");
        YoutubeService youtubeService = retrofit.create(YoutubeService.class);

        youtubeService.recuperarVideos("snippet", "date", "20",
                YoutubeConfig.CHAVE_YOUTUBE_API, YoutubeConfig.CANAL_ID, query
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultado -> {
                    videos = resultado.items;
                    configurarRecyclerView();
                });
    }

    public void configurarRecyclerView() {
        adapterVideo = new AdapterVideo(videos, this);
        recyclerVideos.setHasFixedSize(true);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter(adapterVideo);

        //configurando evento de clique
        recyclerVideos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerVideos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Item video = videos.get(position);
                                String idVideo = video.id.videoId;

                                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                                i.putExtra("idVideo", idVideo);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }


}
