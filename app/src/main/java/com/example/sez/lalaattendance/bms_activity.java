package com.example.sez.lalaattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class bms_activity extends AppCompatActivity {
    private WebView webView;
    private static String URL = "http://llc-attendance.000webhostapp.com/Attendance_Data/getSubject.php";
    private static String google_form = "";
    private static String google_sheet = "";
    String div,stream,year;
    Intent intent;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bms_activity);

        intent = getIntent();
        b = intent.getExtras();

        div = b.getString("div");
        year = b.getString("year");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(year + "BMS " + div);

        getURLs(div,year);

        displayWebView(R.id.present);
    }

    private void getURLs(final String div,final String year) {
        final AlertDialog dialog = new AlertDialog.Builder(bms_activity.this)
                .setTitle(year + "BMS " + div)
                .setMessage("Loading URL`s ...")
                .show();
        Log.d("URL","ENTERED getURLs METHOD");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("subject");
                            Log.d("URL","jsonARRAY: " + jsonArray);
                            if(jsonObject.getString("success").equals("1"))
                            {
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.d("URL","Google Form: " + object.getString("google_form"));
                                    Log.d("URL","Google Sheet: " + object.getString("google_sheet"));

                                    google_form = object.getString("google_form");
                                    google_sheet = object.getString("google_sheet");

                                    if(google_form.equalsIgnoreCase("NULL") && google_sheet.equalsIgnoreCase("NULL") )
                                    {
                                        dialog.dismiss();
                                        //Toast.makeText(bscit_fy.this, "URL`s Loaded Successfully", Toast.LENGTH_SHORT).show();
                                        //showMessage("Success","URL`s Loaded Successfully");
                                        showMessage("Alert","Failed to load URL`s \nKindly go back and try again");

                                    }
                                    else if(TextUtils.isEmpty(google_form) && TextUtils.isEmpty(google_sheet) )
                                    {
                                        dialog.dismiss();
                                        //Toast.makeText(bscit_fy.this, "URL`s Loaded Successfully", Toast.LENGTH_SHORT).show();
                                        //showMessage("Success","URL`s Loaded Successfully");
                                        showMessage("Alert","Failed to load URL`s \nKindly go back and try again");

                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                        displayWebView(R.id.present);
                                        //Toast.makeText(bscit_fy.this, "Failed to Load URL`s \nKindly Refresh", Toast.LENGTH_SHORT).show();
                                        // showMessage("Alert","Failed to load URL`s \nKindly go back and try again");
                                        showMessage("Success","URL`s Loaded Successfully");
                                    }

                                }

                            }
                            else
                            {
                                Toast.makeText(bms_activity.this, "Could not fetch URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(JSONException e)
                        {
                            Toast.makeText(bms_activity.this, "JSON Exception " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception ex)
                        {
                            Toast.makeText(bms_activity.this, "Exception: " + ex.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(bms_activity.this, "Volley Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("year",year);
                params.put("stream","BMS");
                params.put("div",div);
                params.put("s_type","THEORY");
                return params;
            }
        } ;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void displayWebView(int itemID)
    {
        if(itemID == R.id.present)
        {
            webView = (WebView)findViewById(R.id.webView);
            //string url ="https://docs.google.com/forms/d/e/1FAIpQLSdBMr9TTBY4G-5vEq5h2J8L7gldnJAHMVKR49Quwm0DH_yAAA/viewform";
            WebSettings webSettings = webView.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setScrollbarFadingEnabled(false);
            webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            //  webView.getSettings().setBuiltInZoomControls(true);
            webView.setWebViewClient(new WebViewClient());

            webView.loadUrl(google_form);
            Toast.makeText(bms_activity.this, year + "BMS "+ div +"PRESENT SELECTED", Toast.LENGTH_SHORT).show();

        }
        if(itemID == R.id.sheet)
        {

            String url = "https://docs.google.com/spreadsheets/d/1XY-3b9oTE1cRdRwkEwpHQyMZ8nmZ3wU_7Nh4MrQuw1Y/edit#gid=2012468510";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.FullScreen);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.sheet,null);
            WebView wv = (WebView) view.findViewById(R.id.view);
            TextView tv = (TextView) view.findViewById(R.id.layer);
            // alertBuilder.setTitle(div + " BBI");
            alertBuilder.setView(view);
            final AlertDialog dialog = alertBuilder.create();
            dialog.show();
            WebSettings webSettings = wv.getSettings();

            webSettings.setJavaScriptEnabled(true);
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.setScrollbarFadingEnabled(false);
            wv.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            //  webView.getSettings().setBuiltInZoomControls(true);
            wv.setWebViewClient(new WebViewClient());

            wv.loadUrl(google_sheet);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            Toast.makeText(bms_activity.this, year + "BMS "+ div +" PRESENT SELECTED", Toast.LENGTH_SHORT).show();
        }
        if(itemID == R.id.navigation_home)
        {
            if(year.equals("FY"))
            {
                Intent intent = new Intent(bms_activity.this,bms.class);
                startActivity(intent);
                finish();
            }
            else if(year.equals("SY"))
            {
                Intent intent = new Intent(bms_activity.this,sybms.class);
                startActivity(intent);
                finish();
            }
            else if(year.equals("TY"))
            {
                Intent intent = new Intent(bms_activity.this,tybms.class);
                startActivity(intent);
                finish();
            }
        }
    }
    public void showMessage(String title,String message)
    {
        if(title.equals("Success"))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(bms_activity.this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }
        if(title.equals("Alert"))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(bms_activity.this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("Refresh",div);
                    getURLs(div,year);
                }
            });
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(year.equals("FY"))
                    {
                        Intent intent = new Intent(bms_activity.this,bms.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(year.equals("SY"))
                    {
                        Intent intent = new Intent(bms_activity.this,sybms.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(year.equals("TY"))
                    {
                        Intent intent = new Intent(bms_activity.this,tybms.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });
            builder.show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bscit_status,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        displayWebView(id);

        return super.onOptionsItemSelected(item);
    }


}