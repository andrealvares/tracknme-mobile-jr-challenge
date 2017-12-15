package desafiomobiletracknme.tracknme.com.desafiomobiletracknme;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PosicaoService {
    @GET("/posicoes")
    Call<List<Posicao>> getPosicao(@Query("data") String date);
}
