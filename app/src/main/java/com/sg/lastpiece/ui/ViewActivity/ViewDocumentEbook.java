package com.sg.lastpiece.ui.ViewActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.model.layer.NullLayer;
import com.sg.lastpiece.R;
import com.sg.lastpiece.utils.BoldTextView;
import com.sg.lastpiece.utils.Constants;
import com.sg.lastpiece.utils.NormalTextView;

import java.net.URLEncoder;

public class ViewDocumentEbook extends AppCompatActivity {


    private WebView pdfview;
    private BoldTextView tv_title;
    private ImageButton btn_back;
    private ImageView img_network_error;
    private Button btn_try_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document_ebook);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Opening....!!!");
        pd.show();

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.isClickable();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdfview=(WebView)findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);


        String filename=getIntent().getStringExtra(Constants.EBOOK_TITLE);
        String fileurl=getIntent().getStringExtra(Constants.EXTRA_VIEW_PDF);

        tv_title = (BoldTextView) findViewById(R.id.tv_title);
        tv_title.setText(filename);

        img_network_error = (ImageView) findViewById(R.id.img_network_error);
        btn_try_again = (Button) findViewById(R.id.btn_try_again);

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        pdfview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                img_network_error.setVisibility(View.INVISIBLE);
                btn_try_again.setVisibility(View.INVISIBLE);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(pdfview == null){
                    img_network_error.setVisibility(View.VISIBLE);
                    btn_try_again.setVisibility(View.VISIBLE);

                }

                pd.dismiss();
            }


        });

        String url="";
        try {
            url= URLEncoder.encode(fileurl,"UTF-8");
        }catch (Exception ex)
        {

        }

        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);

    }

}
