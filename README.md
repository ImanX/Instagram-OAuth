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

