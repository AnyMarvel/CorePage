package cn.edu.zafu.corepage.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import cn.edu.zafu.corepage.base.BaseFragment;


public class TestFragment3 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_test3, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments!=null) {
            Toast.makeText(getActivity(),"get params:"+arguments.getString("key1")+" "+arguments.getString("key2")+" "+arguments.getString("test"),Toast.LENGTH_LONG ).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BaseApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
