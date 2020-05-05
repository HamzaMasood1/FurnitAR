package com.razi.furnitar.API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.Map;

public class LoginService extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String response = "";
    private String TAG = getClass().getSimpleName();
    private Map<String, Object> body;
    private String url;
    private OnResultReceived mListner;

    public interface OnResultReceived {
        void onResult(String result);
    }

    public LoginService(Context mContext, String url, Map<String, Object> body, OnResultReceived mListner) {
        this.mContext = mContext;
        this.body = body;
        this.mListner = mListner;
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {
            APIResponse apiResponse = APIService.POST(url, new JSONObject(body).toString());
            APIService.responseCode = apiResponse.getStatusCode();
            response = apiResponse.getResponse();
        } catch (Exception e) {
            response = APIService.RESPONSE_UNWANTED;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (!response.equals(APIService.RESPONSE_UNWANTED)) {
            if (mListner != null) mListner.onResult(result);
        } else {
            if (mListner != null) mListner.onResult(result);
        }
    }
}
