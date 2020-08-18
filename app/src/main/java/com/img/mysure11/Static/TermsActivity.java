package com.img.mysure11.Static;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.img.mysure11.R;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("Terms & Conditions");


        String content = "<p>Maternity is the blessing for a mother and she wants to preserve this period in her life with love and happiness. Whether you&rsquo;re a professional maternity photographer or an expecting mom that wants a few happy moments for taking your portrait, the articles in our maternity photography section are for you.&nbsp;</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/4.png  \\\" style=\\\"height:484px; width:860px\\\" /></p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/6.png   \\\" style=\\\"height:287px; width:500px\\\" /></p>\\r\\n\\r\\n<p>Here we are discussing expert advice on maternity &amp; newborn photoshoot, In this collection of photos, there are numbers of happy moments of the couple.</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/8.png   \\\" style=\\\"height:597px; width:860px\\\" /></p>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/5.png   \\\" style=\\\"height:272px; width:500px\\\" /></p>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<p>One of the most important parts of <a href=\\\"https://www.flixaura.com/categorydetails/maternity-photoshoot/consumer\\\">maternity photography</a> is simply coming up with ideas for posing and composition that are flattering for the couple. The various posing guides you&rsquo;ll find in this section dive deep into how to pose expecting mothers for individual portraits, portraits with their significant other, and even portraits with children and other family members.</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/7.png  \\\" style=\\\"height:1000px; width:595px\\\" /></p>\\r\\n\\r\\n<p>As a photographer to capture all these moments is a task to provide the best services and a lifetime memory for new parenthood you have to think out of the box.</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/2.png   \\\" style=\\\"height:888px; width:500px\\\" /></p>\\r\\n\\r\\n<p>This magical journey might end in 9 Months, but Good Photographs are going to last for a lifetime.</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" src=\\\"https://www.flixaura.com/uploads/blog_image/meternity/3.png  \\\" style=\\\"height:571px; width:500px\\\" /></p>\\r\\n\\r\\n<p>&nbsp;</p>\\r\\n\\r\\n<p>We at <a href=\\\"https://www.flixaura.com/\\\">Flixaura </a>have specialized photographers for every event during your most special day. We ensure that no moment is missed. And you rest assured that you have the memories for the rest of your life.</p>";

//        WebView webView = findViewById(R.id.webView);
//        webView.loadData("<style>\n" +
//                "p {\n" +
//                "color: #ffffff;\n" +
//                "padding: 20px 15px 20px 15px;\n" +
//                "background:#0F1629;\n" +
//                "}\n" +
//                "</style>"+content,"text/html; charset=UTF-8", null);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setScrollContainer(false);
//        webView.loadUrl("file:///android_asset/terms.html");
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return (event.getAction() == MotionEvent.ACTION_MOVE);
//            }
//        });
    }
}
