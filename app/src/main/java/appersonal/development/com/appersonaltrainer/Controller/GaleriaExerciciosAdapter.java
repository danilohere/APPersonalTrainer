package appersonal.development.com.appersonaltrainer.Controller;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import appersonal.development.com.appersonaltrainer.R;

public class GaleriaExerciciosAdapter extends PagerAdapter {

    private Context context;
    private int[] exercicio = new int[]{R.drawable.t_exer_01, R.drawable.t_exer_02,
            R.drawable.t_exer_03, R.drawable.t_exer_04, R.drawable.t_exer_05};

    public GaleriaExerciciosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return exercicio.length;
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
        imagem.setImageResource(exercicio[position]);
        (pager).addView(imagem, 0);
        return imagem;
    }
}
