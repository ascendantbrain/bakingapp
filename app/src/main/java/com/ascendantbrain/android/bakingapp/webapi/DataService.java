package com.ascendantbrain.android.bakingapp.webapi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ascendantbrain.android.bakingapp.webapi.RemoteContract.Recipe;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataService {
    private static final String TAG = "DataService";
    private static final boolean DEBUG = true;

    public static void asyncFetch(Context context) {
        final Context appContext = context.getApplicationContext();

        ResultListener listener = new ResultListener() {
            @Override
            public void onLoad(List<Recipe> recipes) {
                RemoteDbHelper.addToDatabase(appContext,recipes);
            }

            @Override
            public void onError(Throwable t) {
                if(DEBUG) Log.e(TAG,"Failed to download recipes\n"+Log.getStackTraceString(t));
            }
        };

        RemoteWebApi remoteWebApi = ServiceGenerator.createService(RemoteWebApi.class);
        Call<List<RemoteContract.Recipe>> call = remoteWebApi.fetch();
        call.enqueue(new FetchCallback(listener));
    }

    public interface ResultListener {
        void onLoad(List<Recipe> recipes);
        void onError(Throwable t);
    }

    private static class FetchCallback implements Callback<List<Recipe>> {
        private WeakReference<ResultListener> mListenerRef;

        FetchCallback(@NonNull ResultListener listener) {
            mListenerRef = new WeakReference<ResultListener>(listener);
        }

        @Override
        public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
            ResultListener listener = mListenerRef.get();
            if(listener==null) return;

            if(response.isSuccessful()) {
                List<Recipe> recipes = response.body();
                listener.onLoad(recipes);
            } else {
                listener.onError(new RuntimeException(String.format(Locale.US,
                        "Failed fetching recipes! code=%d msg=%s",
                        response.code(),
                        response.message())
                ));
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Recipe>> call, Throwable t) {
            ResultListener listener = mListenerRef.get();
            if(listener!=null) listener.onError(t);
        }
    }
}
