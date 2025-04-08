package itstep.learning.androidpv211;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AnimActivity extends AppCompatActivity {

    Animation alfaAnimation;
    Animation rotateAnimation;
    Animation rotate2Animation;
    Animation scaleAnimation;
    Animation bellAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anim);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        alfaAnimation= AnimationUtils.loadAnimation(this, R.anim.anim_alfa);
        rotateAnimation=AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        rotate2Animation=AnimationUtils.loadAnimation(this, R.anim.anim_rotate2);
        scaleAnimation=AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        bellAnimation=AnimationUtils.loadAnimation(this, R.anim.bell);
        findViewById(R.id.anim_v_alfa).setOnClickListener(
                v->v.startAnimation(alfaAnimation));
        findViewById(R.id.anim_v_rotate).setOnClickListener(
                v->v.startAnimation(rotateAnimation));
        findViewById(R.id.anim_v_rotate2).setOnClickListener(
                v->v.startAnimation(rotate2Animation));
        findViewById(R.id.anim_v_scale).setOnClickListener(
                v->v.startAnimation(scaleAnimation));
        findViewById(R.id.anim_v_bell).setOnClickListener(
                v->v.startAnimation(bellAnimation));
    }
}

//Анимации в андроид это аналог css-transition - правила постепенной смены числовых характеристик
//от начального до конечного значення
//создаем руесурсную директорию ANIM,  в ней аним.ресурсы