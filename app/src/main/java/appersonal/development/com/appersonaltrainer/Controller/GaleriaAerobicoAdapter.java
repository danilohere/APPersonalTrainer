package appersonal.development.com.appersonaltrainer.Controller;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import appersonal.development.com.appersonaltrainer.R;

public class GaleriaAerobicoAdapter extends PagerAdapter {

    private Context context;
    private int[] aerobico = new int[]{R.drawable.t_exer_01, R.drawable.t_exer_02,
            R.drawable.t_aero_03, R.drawable.t_aero_04, R.drawable.t_aero_05, R.drawable.t_aero_06};

    public GaleriaAerobicoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return aerobico.length;
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
        imagem.setImageResource(aerobico[position]);
        (pager).addView(imagem, 0);
        return imagem;
    }
}
