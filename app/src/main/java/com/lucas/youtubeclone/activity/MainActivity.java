package com.lucas.youtubeclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.youtubeclone.R;
import com.lucas.youtubeclone.adapter.AdapterVideo;
import com.lucas.youtubeclone.api.YoutubeService;
import com.lucas.youtubeclone.helper.RetrofitConfig;
import com.lucas.youtubeclone.helper.YoutubeConfig;
import com.lucas.youtubeclone.model.Item;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements MaterialSearchView.OnQueryTextListener,
        MaterialSearchView.SearchViewListener {

    private RecyclerView recyclerVideos;

    private MaterialSearchView searchView;

    private Retrofit retrofit = RetrofitConfig.getRetrofit();

    private AdapterVideo adapterVideo = new AdapterVideo(this::clickItemList);

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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

        configurarRecyclerView();

        //Configurando metodos search view
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(this);

        recuperarVideos("");

    }

    private void recuperarVideos(String pesquisa) {

        String query = pesquisa.replaceAll(" ", "+");
        YoutubeService youtubeService = retrofit.create(YoutubeService.class);

        compositeDisposable.add(
                Completable.complete()
                        .observeOn(Schedulers.io())
                        .subscribe(() ->
                                compositeDisposable.add(youtubeService.recuperarVideos("snippet", "date", "20",
                                        YoutubeConfig.CHAVE_YOUTUBE_API, YoutubeConfig.CANAL_ID, query)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(resultado -> updateVideos(resultado.items)))));
    }

    /**
     * Função responsavel por todas as configurações
     * da listView
     */
    public void configurarRecyclerView() {
        recyclerVideos.setHasFixedSize(true);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter(adapterVideo);
    }

    /**
     * Função responsavel pelo click da listView
     *
     * @param item
     */
    private void clickItemList(Item item) {
        String idVideo = item.id.videoId;

        Intent i = new Intent(MainActivity.this, PlayerActivity.class);
        i.putExtra("idVideo", idVideo);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Atualiza a Lista de videos a partir do listener
     * do serviceAPI
     *
     * @param videos
     */
    public void updateVideos(@NotNull List<Item> videos) {
        adapterVideo.updateData(videos);
    }

    /**
     * Função responsavel pela ação da barra de pesquisa
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        recuperarVideos(query);
        return false;
    }

    /**
     * Função responsavel pela ação da barra de pesquisa
     *
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Função responsavel pela ação da barra de pesquisa
     */
    @Override
    public void onSearchViewShown() {

    }

    /**
     * Função responsavel pela ação da barra de pesquisa
     */
    @Override
    public void onSearchViewClosed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}
