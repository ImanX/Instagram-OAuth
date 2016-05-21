# Instagram-OAuth
Instagram Authentication Service on Android 
Simply Request to Instgram Authentication API and get Access Token

- Need Volley Library:
```Gradle
      compile 'eu.the4thfloor.volley:com.android.volley:2015.05.28'
 ```
 - Need Internet Permission:
 
```XML
    <uses-permission android:name="android.permission.INTERNET"/>
```
 
- Example :
 ```Java

        HashMap<String, String> clientInfoParams = new HashMap<>();
        clientInfoParams.put("client_id", "47178b7af1f2439ca80e58fda6e1d25c");
        clientInfoParams.put("client_secret", "dcdc4e56d7d54035bae988eea17ac61d");
        clientInfoParams.put("grant_type", "authorization_code");
        clientInfoParams.put("redirect_uri", "imanx://content");

        new InstagramOAuthService(this)
                .setClientInfoParameters(clientInfoParams)
                .authorize(new InstagramOAuthService.OAuthCallBack() {
                    @Override
                    public void onSuccessAuthenticate(String accessToken) {
                        Log.i("TAG Token", accessToken);
                    }

                    @Override
                    public void onFailureAuthenticate() {

                    }
                });
 ```

