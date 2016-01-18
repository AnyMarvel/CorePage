package cn.edu.zafu.corepage.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import cn.edu.zafu.corepage.base.BaseFragment;



public class TestFragment4 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_test4, container, false);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BaseApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
