package cn.edu.szu.szuschedule.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.edu.szu.szuschedule.R;

/**
 * Created by chenlin on 24/05/2017.
 */
public class TestFragment extends Fragment {
    public static TestFragment newInstance(String info) {
        Bundle args = new Bundle();
        TestFragment fragment = new TestFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, null);
        TextView textInfo = (TextView) view.findViewById(R.id.textView);
        textInfo.setText(getArguments().getString("info"));
        return view;
    }
}