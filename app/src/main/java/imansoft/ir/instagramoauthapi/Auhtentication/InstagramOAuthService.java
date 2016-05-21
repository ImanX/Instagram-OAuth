package imansoft.ir.instagramoauthapi.Auhtentication;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import imansoft.ir.instagramoauthapi.R;


/**
 * Created by Alireza Tarazani ImanX
 * Copyright 2016 Alireza Tarazani ImanX. All rights reserved.
 * <P>
 *     Instagram Authentication API Service Handler
 * </P>
 */
public class InstagramOAuthService {

    private Context                 context;
    private String                  contentDialog;
    private String                  clientId;
    private String                  redirectUri;
    private HashMap<String, String> clientInfoParameters;
    private OAuthCallBack           listener;


    private static final String AUTHENTICATION_URL = "https://api.instagram.com/oauth/authorize/?";
    private static final String ACCESS_TOKEN_URL   = "https://api.instagram.com/oauth/access_token";
    public static final  String CLIENT_ID          = "client_id";
    public static final  String CLIENT_SECRET      = "client_secret";
    public static final  String REDIRECT_URI       = "redirect_uri";
    public static final  String CODE               = "code";


    public interface OAuthCallBack {
        void onSuccessAuthenticate(String accessToken);

        void onFailureAuthenticate();


    }

    public interface DialogAuthenticationListener {
        void onAuthorizationCode(String authorizeCode);
    }

    public interface HttpRequestListener {
        void onSuccessHttpRequest(JSONObject jsonObject);

        void onFailureHttpRequest(Integer errorStatusCode);

        void onConnectionError();
    }


    public InstagramOAuthService(Context context) {
        this.context = context;
    }

    public InstagramOAuthService setClientInfoParameters(HashMap<String, String> clientInfoParameters) {
        this.clientId = clientInfoParameters.get(CLIENT_ID);
        this.redirectUri = clientInfoParameters.get(REDIRECT_URI);
        this.clientInfoParameters = clientInfoParameters;
        return this;
    }

    public InstagramOAuthService setContentAuthDialog(String contentDialog) {
        this.contentDialog = contentDialog;
        return this;
    }


    public void authorize(OAuthCallBack listener) {
        this.listener = listener;
        new InstagramOAuthDialog(context, contentDialog)
                .setDialogAuthenticationListener(new DialogAuthenticationListener() {
                    @Override
                    public void onAuthorizationCode(String authorizeCode) {
                        InstagramOAuthService.this.clientInfoParameters.put(CODE, authorizeCode);
                        requestAccessToken();
                    }
                }).show();
    }


    private void requestAccessToken() {
        new HttpRequest()
                .setRequestMethod(Request.Method.POST)
                .setUrl(getAccessTokenUrl())
                .execute(new HttpRequestListener() {
                    @Override
                    public void onSuccessHttpRequest(JSONObject jsonObject) {
                        if (jsonObject.has("access_token")) {
                            try {
                                listener.onSuccessAuthenticate(jsonObject.getString("access_token"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFailureAuthenticate();
                            }
                            return;
                        }
                        listener.onFailureAuthenticate();
                    }

                    @Override
                    public void onFailureHttpRequest(Integer errorStatusCode) {
                        listener.onFailureAuthenticate();
                    }

                    @Override
                    public void onConnectionError() {
                        listener.onFailureAuthenticate();
                    }
                });
    }

    private String getAuthenticationApi() {
        return (AUTHENTICATION_URL + "client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code");
    }

    private String getAccessTokenUrl() {
        return ACCESS_TOKEN_URL;
    }

    class InstagramOAuthDialog extends Dialog {

        private ProgressBar                  progressBar;
        private TextView                     txtPercentDialog;
        private TextView                     txtContentDialog;
        private WebView                      webView;
        private DialogAuthenticationListener listener;

        private String DEFAULT_CONTENT_AUTH_DIALOG = "Connecting to Instagram Server...";


        public InstagramOAuthDialog(Context context, String contentDialog) {
            super(context);
            View view = configurationDialog(context);
            this.webView = (WebView) view.findViewById(R.id.webView);
            this.txtContentDialog = (TextView) view.findViewById(R.id.txtContentDialog);
            this.txtPercentDialog = (TextView) view.findViewById(R.id.txtPercentDialog);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            this.txtContentDialog.setText(contentDialog == null ? DEFAULT_CONTENT_AUTH_DIALOG : contentDialog);

        }


        public InstagramOAuthDialog setDialogAuthenticationListener(DialogAuthenticationListener listener) {
            this.listener = listener;
            return this;
        }

        private void configurationWebView() {
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.loadUrl(getAuthenticationApi());
            this.webView.setHorizontalScrollBarEnabled(false);
            this.webView.setVerticalScrollBarEnabled(false);
            this.webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith(redirectUri)) {
                        String authorizeCode = url.split("=")[1];
                        listener.onAuthorizationCode(authorizeCode);
                        InstagramOAuthDialog.this.dismiss();
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });

            this.webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int percent) {
                    if (percent >= 100) {
                        webView.setVisibility(View.VISIBLE);
                        txtContentDialog.setVisibility(View.GONE);
                        txtPercentDialog.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    txtPercentDialog.setText(String.format("%s%s", percent, "%"));
                    super.onProgressChanged(view, percent);
                }
            });


        }

        private View configurationDialog(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.auth_dialog, null);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setCanceledOnTouchOutside(false);
            this.setContentView(view);
            return view;
        }


        @Override
        public void show() {
            configurationWebView();
            super.show();
        }
    }

    class HttpRequest implements Response.Listener, Response.ErrorListener {

        private String              url;
        private Integer             requestMethod;
        private HttpRequestListener listener;


        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("TAG error connection", "ERROR");
            if ((error.networkResponse == null) || (error instanceof NoConnectionError)) {
                this.listener.onConnectionError();
                return;
            }
            this.listener.onFailureHttpRequest(error.networkResponse.statusCode);
        }

        @Override
        public void onResponse(Object response) {
            try {
                this.listener.onSuccessHttpRequest(new JSONObject(response.toString()));
            } catch (JSONException e) {
                listener.onFailureHttpRequest(-100);
                e.printStackTrace();
            }
        }


        public HttpRequest setUrl(String url) {
            this.url = url;
            return this;
        }

        public HttpRequest setRequestMethod(Integer requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }


        public void execute(HttpRequestListener listener) {
            this.listener = listener;
            Request request = new StringRequest(requestMethod, url, this, this) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return clientInfoParameters;
                }
            };
            Volley.newRequestQueue(context).add(request);
        }

    }
}
