# Instagram-OAuth
Instagram Authentication Service on Android 
Simply Request to Instgram Authentication API and get Access Token

- Need Volley Library:
```Gradle
      compile 'eu.the4thfloor.volley:com.android.volley:2015.05.28'
 ```
 - Need Internet Permission:
 ```xml
    <uses-permission android:name="android.permission.INTERNET"/>
 ```
 
- Example :
 ```xml

  HashMap<String, String> clientInfoParams = new HashMap<>();
        clientInfoParams.put("client_id", "471xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        clientInfoParams.put("client_secret", "dcdxxxxxxxxxxxxxxxxxxxxxx");
        clientInfoParams.put("grant_type", "authorization_code");
        clientInfoParams.put("redirect_uri", "redirect://content");

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
 ```

