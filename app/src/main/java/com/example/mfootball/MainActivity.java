package com.example.mfootball;

import static com.example.mfootball.Utils.API_KEY;
import static com.example.mfootball.Utils.API_VERSION;
import static com.example.mfootball.Utils.BASE_URL;
import static com.example.mfootball.Utils.BLANK_URL;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.mfootball.databinding.ActivityMainBinding;
import com.example.mfootball.data.Match;
import com.example.mfootball.data.MatchesResult;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText etMatch = binding.content.etMatch;
        Button btSearch = binding.content.btFind;
        WebView wvResult = binding.content.wvResult;

        wvResult.getSettings().setJavaScriptEnabled(true);
        wvResult.getSettings().setBuiltInZoomControls(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FootballService service = retrofit.create(FootballService.class);
        btSearch.setOnClickListener(view -> {
            wvResult.loadUrl(BLANK_URL);
            Call<MatchesResult> call = service.getVideos(API_VERSION, API_KEY);
            call.enqueue(new Callback<MatchesResult>() {
                @Override
                public void onResponse(@NonNull Call<MatchesResult> call, @NonNull Response<MatchesResult> response) {
                    if (response.isSuccessful()) {
                        MatchesResult matches = response.body();
                        if (matches != null && matches.response != null) {
                            for (Match m : matches.response) {
                                if (m.title.toLowerCase(Locale.ROOT).contains(etMatch.getText().toString().toLowerCase(Locale.ROOT))) {
                                    if (m.videos != null && m.videos.length > 0) {
                                        String s = m.videos[0].embed;
                                        String url = s.substring(s.indexOf("src") + 5, s.indexOf("'", s.indexOf("src") + 5));
                                        wvResult.loadUrl(url);
                                        Log.d("Tests", url);
                                        return;
                                    }
                                }
                            }
                        }
                        Toast.makeText(MainActivity.this, "Match not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MatchesResult> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_problems), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}