package com.example.koreamap;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.GeolocationPermissions;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
public class MainActivity extends Activity {
    WebView w;
    GeolocationPermissions.Callback gcb; String gorigin;
    boolean hasLoc() { return Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED; }
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        w = new WebView(this);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setGeolocationEnabled(true);
        w.setWebChromeClient(new WebChromeClient() {
            @Override public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback cb) {
                if (hasLoc()) { cb.invoke(origin, true, false); return; }
                gcb = cb; gorigin = origin;
                if (Build.VERSION.SDK_INT >= 23) requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 77);
                else cb.invoke(origin, true, false);
            }
        });
        w.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, android.webkit.WebResourceRequest r) {
                String u = r.getUrl().toString();
                if (u.startsWith("http")) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u))); return true; }
                return false;
            }
        });
        w.loadUrl("file:///android_asset/index.html");
        setContentView(w);
    }
    @Override public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == 77 && gcb != null) {
            boolean ok = false; for (int r : res) if (r == PackageManager.PERMISSION_GRANTED) ok = true;
            gcb.invoke(gorigin, ok, false); gcb = null;
        }
    }
    @Override public void onBackPressed() { if (w.canGoBack()) w.goBack(); else super.onBackPressed(); }
}
