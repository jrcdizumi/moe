package com.example.moe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;



public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private Button buttonSelectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonSelectImage = findViewById(R.id.button_select_image);
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) {
                        Log.e("MainActivity", "No image selected");
                        return;
                    }
                    // 在这里处理结果
                    try {
                        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                        if (parcelFileDescriptor == null) {
                            Log.e("MainActivity", "No image selected");
                            return;
                        }
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        InputStream inputStream = new FileInputStream(fileDescriptor);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), readAllBytes(inputStream));
                        TraceMoeService service = RetrofitClient.getClient("https://api.trace.moe/").create(TraceMoeService.class);
                        Call<TraceMoeResponse> call = service.searchAnime(requestBody);
                        call.enqueue(new Callback<TraceMoeResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<TraceMoeResponse> call, @NonNull Response<TraceMoeResponse> response) {
                                if (response.isSuccessful()) {
                                    TraceMoeResponse traceMoeResponse = response.body();
                                    assert traceMoeResponse != null;
                                    adapter = new ResultAdapter(traceMoeResponse.getResult());
                                    recyclerView.setAdapter(adapter);
                                }
                                else{
                                    Log.e("MainActivity", "Server returned error " + response.code() + ": " + response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<TraceMoeResponse> call, Throwable t) {
                                // Handle failure
                                Log.e("MainActivity", "Network request failed", t);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
    }


    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}