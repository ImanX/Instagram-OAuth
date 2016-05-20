package imansoft.ir.instagramoauthapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import imansoft.ir.instagramoauthapi.Auhtentication.Authentication;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashMap<String, String> clientInfoParams = new HashMap<>();
        clientInfoParams.put("client_id", "47178b7af1f2439ca80e58fda6e1d25c");
        clientInfoParams.put("client_secret", "dcdc4e56d7d54035bae988eea17ac61d");
        clientInfoParams.put("grant_type", "authorization_code");
        clientInfoParams.put("redirect_uri", "imanx://content");

        new Authentication(this)
                .setClientInfoParameters(clientInfoParams)
                .authorize(new Authentication.OAuthCallBack() {
                    @Override
                    public void onSuccessAuthenticate(String accessToken) {
                        Log.i("TAG Token", accessToken);
                    }

                    @Override
                    public void onFailureAuthenticate() {

                    }
                });
    }
}
