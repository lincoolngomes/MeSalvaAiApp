package com.mesalvaai.aplicativo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.mesalvaai.aplicativo.R;
import com.mesalvaai.aplicativo.fragment.ContatosFragment;
import com.mesalvaai.aplicativo.fragment.ConversasFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class SocialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        //Configurar abas
        FragmentPagerItemAdapter adapterSocial = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add(R.string.social_menu1, ConversasFragment.class)
                .add(R.string.social_menu2, ContatosFragment.class)
                .create()
        );
        ViewPager viewPagerSocial = findViewById(R.id.viewPager);
        viewPagerSocial.setAdapter( adapterSocial );

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPagerSocial );

    }
}