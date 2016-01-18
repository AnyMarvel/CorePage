package cn.edu.zafu.corepage.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class LoadResActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super .onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
        overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
        setContentView(R.layout.layout_load);
        new LoadDexTask().execute();
    }
    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
                Log.d("loadDex" , "install finish" );
                ((BaseApplication) getApplication()).installFinish(getApplication());
            } catch (Exception e) {
                Log.e("loadDex" , e.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            Log.d( "loadDex", "get install finish");
            finish();
            System.exit( 0);
        }
    }
    @Override
    public void onBackPressed() {
        //cannot backpress
    }}