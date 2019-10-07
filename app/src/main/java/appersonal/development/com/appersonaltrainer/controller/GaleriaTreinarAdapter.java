package appersonal.development.com.appersonaltrainer.controller;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import appersonal.development.com.appersonaltrainer.R;

public class GaleriaTreinarAdapter extends PagerAdapter {

    private final Context context;
    private final int[] treinar = new int[]{R.drawable.t_treino_01, R.drawable.t_treino_02,
            R.drawable.t_treino_03, R.drawable.t_treino_04, R.drawable.t_treino_05, R.drawable.t_treino_06};

    public GaleriaTreinarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return treinar.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup pager, int position, @NonNull Object object) {
        (pager).removeView((ImageView) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup pager, int position) {
        ImageView imagem = new ImageView(context);
        imagem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imagem.setImageResource(treinar[position]);
        (pager).addView(imagem, 0);
        return imagem;
    }
}
