package desafiomobiletracknme.tracknme.com.desafiomobiletracknme;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String POSICOES_URL= "https://private-ea0aa4-desafiotracknme.apiary-mock.com";
    private GoogleMap mMap;
    private List<Posicao> listaPosicao = new ArrayList<>();
    private List<PosicaoBD> listaPontos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String dia = ""; // Variável que recebe o dia inserido pelo usuário.

        Bundle diaBundle = getIntent().getExtras(); //Importação da Intent de InicioActivity.
        if (diaBundle != null) dia = diaBundle.getString("data");

        if (dia.equals("")) { //Task 1
            Call<List<Posicao>> pontos = consultaAPI(dia);
            pontos.enqueue(new Callback<List<Posicao>>() {
            @Override
            public void onResponse(Call<List<Posicao>> call, Response<List<Posicao>> response) {

                if(response.isSuccessful()) listaPosicao = response.body();
                ArrayList<LatLng> trajeto = new ArrayList<LatLng>(); //Posições para trilha.
                LatLng ponto = null; //Marcação de cada posição.

                try {
                    SQLiteDatabase bancoDeDados = openOrCreateDatabase("tracknme",
                            MODE_PRIVATE, null);
                    bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS posicao(dateTime VARCHAR(19)," +
                            " latitude DOUBLE(4, 15), longitude DOUBLE(4, 15), data Date)");
                    for (Posicao pos : listaPosicao) {
                        Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM posicao WHERE" +
                                        " dateTime = '" + pos.getDateTime() + "'",null);
                        if (cursor.getCount() == 0) { //Posição não está inclusa no banco de dados
                            String ins = "INSERT INTO posicao (dateTime, latitude, longitude,"
                                    + " data) VALUES ('" + pos.getDateTime() + "', " +
                                    pos.getLatitude() + ", " + pos.getLongitude() + ", " +
                                    pos.getDateTime().substring(0, 10) + ")";
                            bancoDeDados.execSQL(ins); //Inclusão da posição no banco de dados.
                        }
                        PosicaoBD posicaoBD = new PosicaoBD(pos.getDateTime(), pos.getLatitude(),
                                pos.getLongitude(),
                                Date.valueOf(pos.getDateTime().substring(0, 10)));
                        listaPontos.add(posicaoBD);
                    }
                    bancoDeDados.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (listaPontos != null) {
                    for (PosicaoBD pos : listaPontos) { //Marcação de pontos.
                        ponto = new LatLng(pos.getLatitude(), pos.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(ponto).title(pos.getDateTime()));
                        trajeto.add(ponto); //Adiciona posição ao desenho de trajeto.
                    }
                    mMap.addPolyline(new PolylineOptions().addAll(trajeto).width(5).color(Color.RED));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ponto));
                } else { //Nenhuma posição listada.
                    Toast.makeText(MainActivity.this, "Não há posições a marcar",
                            Toast.LENGTH_LONG);
                    LatLng sydney = new LatLng(-34, 151); //Posição default do Google Maps.
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            }

            @Override
            public void onFailure(Call<List<Posicao>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha ao obter resposta da" +
                        " API", Toast.LENGTH_LONG).show();
            }
        });

        } else { //Task 2

            try { //Consulta ao banco de dados.

                SQLiteDatabase bancoDeDados = openOrCreateDatabase("tracknme",
                        MODE_PRIVATE, null);
                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS posicao(dateTime VARCHAR(19)," +
                        " latitude DOUBLE(4, 15), longitude DOUBLE(4, 15), data Date)");
                String sql = "SELECT * FROM posicao WHERE data = " + dia;
                Cursor cursor = bancoDeDados.rawQuery(sql, null);

                if(cursor.getCount() > 0) {
                    ArrayList<LatLng> trajeto = new ArrayList<LatLng>();

                    LatLng ponto = null;

                    criarPosicao(dia);
                    cursor.close();

                    for(PosicaoBD posbd : listaPontos) {
                        ponto = new LatLng(posbd.getLatitude(), posbd.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(ponto).title(posbd.getDateTime()));
                        trajeto.add(ponto);
                    }
                    mMap.addPolyline(new PolylineOptions().addAll(trajeto).width(5).color(Color.RED));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ponto));
                }
                else { //Consulta na API após não encontrar o trajeto no Banco de Dados.

                    Call<List<Posicao>> pontos = consultaAPI(dia);
                    pontos.enqueue(new Callback<List<Posicao>>() {
                        @Override
                        public void onResponse(Call<List<Posicao>> call, Response<List<Posicao>> response) {
                            if (response.isSuccessful()) listaPosicao = response.body();
                            ArrayList<LatLng> trajeto = new ArrayList<LatLng>();
                            LatLng ponto = null;
                            if (listaPosicao != null) {
                                SQLiteDatabase bancoDeDados = openOrCreateDatabase(
                                        "tracknme", MODE_PRIVATE, null);
                                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS posicao(" +
                                        "dateTime VARCHAR(19), latitude DOUBLE(4, 15)," +
                                        " longitude DOUBLE(4, 15), data Date)");
                                for (Posicao pos : listaPosicao) {
                                    bancoDeDados.execSQL("INSERT INTO posicao (dateTime," +
                                            " latitude, longitude, data) VALUES ('" +
                                            pos.getDateTime() + "', " + pos.getLatitude() +
                                            ", " + pos.getLongitude() + ", " +
                                            Date.valueOf(pos.getDateTime().substring(0 ,10)) +
                                            ")");
                                    ponto = new LatLng(pos.getLatitude(), pos.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(ponto).title(pos.getDateTime()));
                                    trajeto.add(ponto);
                                }
                                mMap.addPolyline(new PolylineOptions().addAll(trajeto).width(5).color(Color.RED));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(ponto));
                                bancoDeDados.close();
                            } else { //Nenhuma posição listada.
                                Toast.makeText(MainActivity.this, "Não há posições a marcar",
                                        Toast.LENGTH_LONG);
                                LatLng sydney = new LatLng(-34, 151); //Posição default do Google Maps.
                                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Posicao>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Falha ao obter resposta da" +
                                    " API", Toast.LENGTH_LONG).show();
                        }
                    });

                } //else -> cursor == null
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } //onMapReady()

    public Call<List<Posicao>> consultaAPI(String dia) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(POSICOES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PosicaoService posicaoService = retrofit.create(PosicaoService.class);

        Call<List<Posicao>> pontos = posicaoService.getPosicao(dia);

        return pontos;
    }

    public void criarPosicao(String dia) {
        ArrayList<String> dateTimeList = new ArrayList<String>();
        ArrayList<Double> latList = new ArrayList<Double>();
        ArrayList<Double> longList = new ArrayList<Double>();

        SQLiteDatabase banco = openOrCreateDatabase("tracknme", MODE_PRIVATE, null);
        Cursor cursor = banco.rawQuery("SELECT dateTime, latitude, longitude FROM posicao WHERE data = " + dia, null);

        int indiceColunaDateTime = cursor.getColumnIndex("dateTime");
        int indiceColunaLatitude = cursor.getColumnIndex("latitude");
        int indiceColunaLongitude = cursor.getColumnIndex("longitude");

        int c = cursor.getCount();
        if(c > 0) {
            cursor.moveToFirst();
            for(int i = 0; i < c; i++) {
                dateTimeList.add(cursor.getString(indiceColunaDateTime));
                latList.add(cursor.getDouble(indiceColunaLatitude));
                longList.add(cursor.getDouble(indiceColunaLongitude));
                PosicaoBD posicaoBD = new PosicaoBD(dateTimeList.get(i), latList.get(i),
                        longList.get(i), Date.valueOf(dia));
                listaPontos.add(posicaoBD);
                cursor.moveToNext();
            }
        }

    }

}
