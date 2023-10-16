package com.jica.newpts.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jica.newpts.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BridgeInterface(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Android -> Javascript 함수 호출!(웹뷰페이지가 모두 로딩이 끝날때)
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        // 최초 웹뷰로드
        webView.loadUrl("https://newpts-26161.web.app");
    }

    private class BridgeInterface {
        // 클래스 이름은 아무거나 지어도 되지만 어노테이션이 중요함
        @JavascriptInterface
        public void processDATA(String data) {
            // 카카오 주소 검색 결과 API의 결과 값이 브릿지 통로를 통해 전달받는다(from Javascript)
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}