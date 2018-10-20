package com.ic.stephen.internationaldialer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ic.stephen.internationaldialer.R;

/**
 * Created by Jarvis on 6/24/2016.
 */
public class HelpFragment extends Fragment {
    TextView faqTextView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        faqTextView = (TextView) view.findViewById(R.id.textView_faq);
        faqTextView.setText(Html.fromHtml(getString(R.string.faq_paragraph)));
        return view;
    }
}
