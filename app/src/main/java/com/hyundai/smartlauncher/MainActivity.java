package com.hyundai.smartlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(10, 17, 24);
    private final int PANEL = Color.rgb(23, 34, 43);
    private final int GREEN = Color.rgb(133, 235, 72);
    private LinearLayout page;
    private TextView clock;
    private final String[] preferredPackages = {
        "com.google.android.youtube.tv", "com.netflix.ninja",
        "com.amazon.amazonvideo.livingroom", "com.disney.disneyplus",
        "com.google.android.videos", "tv.pluto.android",
        "com.plexapp.android", "org.videolan.vlc", "com.android.chrome"
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        buildInterface();
    }

    private void buildInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(12,25,33), Color.rgb(15,39,43), Color.rgb(8,14,22)}));
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(28), dp(18), dp(28), dp(24));
        scroll.addView(page);
        setContentView(scroll);

        LinearLayout header = row();
        TextView brand = text("HYUNDAI", 25, Color.WHITE, true);
        TextView sub = text("SMART LAUNCHER", 12, GREEN, true);
        LinearLayout brandBox = new LinearLayout(this);
        brandBox.setOrientation(LinearLayout.VERTICAL);
        brandBox.addView(brand);
        brandBox.addView(sub);
        header.addView(brandBox, new LinearLayout.LayoutParams(0, -2, 1));
        clock = text("", 15, Color.WHITE, true);
        clock.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        header.addView(clock);
        header.addView(actionButton("⚙", () -> openSettings(Settings.ACTION_SETTINGS), false),
            new LinearLayout.LayoutParams(dp(52), dp(48)));
        page.addView(header);
        updateClock();

        TextView greeting = text("Todo lo que necesitas, en un solo lugar", 15,
            Color.rgb(191,205,211), false);
        LinearLayout.LayoutParams gp = params(-2, -2);
        gp.topMargin = dp(16); gp.bottomMargin = dp(14);
        page.addView(greeting, gp);

        LinearLayout cards = row();
        cards.addView(bigCard("TV", "Televisión", "Abrir la aplicación de TV disponible", () -> openTv()),
            weightParams());
        cards.addView(bigCard("HDMI", "Entradas externas", "Abrir control de entradas si existe", () -> showInputNotice()),
            weightParams());
        cards.addView(bigCard("USB", "Archivos y medios", "Abrir un explorador disponible", () -> openFileBrowser()),
            weightParams());
        cards.addView(bigCard("Apps", "Aplicaciones", "Ver aplicaciones instaladas", () -> showAppPicker()),
            weightParams());
        page.addView(cards, params(-1, dp(154)));

        TextView toolsTitle = text("ACCESOS RÁPIDOS", 12, GREEN, true);
        LinearLayout.LayoutParams tp = params(-2,-2);
        tp.topMargin = dp(22); tp.bottomMargin = dp(10);
        page.addView(toolsTitle,tp);
        LinearLayout tools = row();
        tools.addView(actionButton("Gestionar aplicaciones", this::showAppPicker, true), weightParams());
        tools.addView(actionButton("Configuración", () -> openSettings(Settings.ACTION_SETTINGS), true), weightParams());
        tools.addView(actionButton("Red", () -> openSettings(Settings.ACTION_WIFI_SETTINGS), true), weightParams());
        page.addView(tools);

        TextView favTitle = text("TUS APLICACIONES", 12, GREEN, true);
        LinearLayout.LayoutParams fp = params(-2,-2);
        fp.topMargin=dp(22); fp.bottomMargin=dp(10);
        page.addView(favTitle,fp);
        LinearLayout favorites = row();
        int count=0;
        PackageManager pm=getPackageManager();
        for(String pkg:preferredPackages) {
            Intent launch=pm.getLaunchIntentForPackage(pkg);
            if(launch==null) continue;
            ApplicationInfo info;
            try { info=pm.getApplicationInfo(pkg,0); } catch(Exception e) { continue; }
            favorites.addView(appButton(pm.getApplicationIcon(info),
                pm.getApplicationLabel(info).toString(), launch), weightParams());
            count++;
        }
        if(count==0) {
            TextView empty=text("No se encontraron aplicaciones favoritas instaladas. Usa Aplicaciones para ver las disponibles.",14,
                Color.LTGRAY,false);
            page.addView(empty);
        } else page.addView(favorites, params(-1, dp(88)));

        TextView footer=text("Las funciones disponibles dependen del sistema y del firmware del televisor.",
            11, Color.rgb(142,158,166), false);
        LinearLayout.LayoutParams foot=params(-2,-2);
        foot.topMargin=dp(18);
        page.addView(footer,foot);
    }

    private LinearLayout row() {
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }
    private LinearLayout.LayoutParams params(int w,int h) { return new LinearLayout.LayoutParams(w,h); }
    private LinearLayout.LayoutParams weightParams() {
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);
        p.setMargins(dp(5),0,dp(5),0);
        return p;
    }
    private TextView text(String value,int size,int color,boolean bold) {
        TextView t=new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if(bold) t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }
    private GradientDrawable shape(int color,int stroke) {
        GradientDrawable d=new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(14));
        if(stroke!=0) d.setStroke(dp(2),stroke);
        return d;
    }
    private Button styledButton(String label, boolean primary) {
        Button b=new Button(this);
        b.setText(label); b.setTextColor(primary?Color.rgb(15,25,18):Color.WHITE);
        b.setTextSize(13); b.setAllCaps(false);
        b.setBackground(shape(primary?GREEN:Color.rgb(36,51,61),primary?GREEN:Color.rgb(59,78,87)));
        b.setFocusable(true);
        b.setPadding(dp(8),dp(4),dp(8),dp(4));
        b.setOnFocusChangeListener((v,focus)->v.setBackground(shape(
            focus?Color.rgb(48,76,48):(primary?GREEN:Color.rgb(36,51,61)),
            focus?Color.WHITE:(primary?GREEN:Color.rgb(59,78,87))));
        return b;
    }
    private View bigCard(String tag,String title,String detail,Runnable action) {
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(12),dp(12),dp(12),dp(12));
        box.setBackground(shape(PANEL,Color.rgb(46,69,78)));
        box.setFocusable(true); box.setClickable(true);
        TextView badge=text(tag,22,GREEN,true);
        TextView heading=text(title,15,Color.WHITE,true);
        TextView description=text(detail,11,Color.rgb(185,198,202),false);
        description.setMaxLines(3);
        box.addView(badge);
        LinearLayout.LayoutParams hp=params(-1,-2); hp.topMargin=dp(8);
        box.addView(heading,hp);
        LinearLayout.LayoutParams dpms=params(-1,-2); dpms.topMargin=dp(5);
        box.addView(description,dpms);
        box.setOnClickListener(v->action.run());
        box.setOnFocusChangeListener((v,focus)->v.setBackground(shape(
            focus?Color.rgb(34,58,43):PANEL,focus?GREEN:Color.rgb(46,69,78))));
        return box;
    }
    private Button actionButton(String label,Runnable action,boolean primary) {
        Button b=styledButton(label,primary);
        b.setOnClickListener(v->action.run());
        return b;
    }
    private View appButton(android.graphics.drawable.Drawable icon,String label,Intent intent) {
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER);
        box.setPadding(dp(5),dp(7),dp(5),dp(7));
        box.setBackground(shape(PANEL,Color.rgb(46,69,78)));
        box.setFocusable(true); box.setClickable(true);
        android.widget.ImageView image=new android.widget.ImageView(this);
        image.setImageDrawable(icon);
        LinearLayout.LayoutParams ip=params(dp(38),dp(38));
        box.addView(image,ip);
        TextView name=text(label,10,Color.WHITE,false);
        name.setGravity(Gravity.CENTER); name.setMaxLines(2);
        LinearLayout.LayoutParams np=params(-1,-2); np.topMargin=dp(5);
        box.addView(name,np);
        box.setOnClickListener(v->{try{startActivity(intent);}catch(Exception e){toast("No se pudo abrir la aplicación.");}});
        box.setOnFocusChangeListener((v,focus)->v.setBackground(shape(focus?Color.rgb(34,58,43):PANEL,focus?GREEN:Color.rgb(46,69,78))));
        return box;
    }
    private void updateClock() {
        if(clock!=null) clock.setText(new SimpleDateFormat("EEE d MMM  ·  HH:mm",new Locale("es","CO")).format(new Date()));
        clock.postDelayed(this::updateClock,30000);
    }
    private void showAppPicker() {
        PackageManager pm=getPackageManager();
        List<ApplicationInfo> apps=new ArrayList<>();
        for(ApplicationInfo ai:pm.getInstalledApplications(0))
            if(!ai.packageName.equals(getPackageName()) && pm.getLaunchIntentForPackage(ai.packageName)!=null) apps.add(ai);
        Collections.sort(apps,(a,b)->pm.getApplicationLabel(a).toString().compareToIgnoreCase(pm.getApplicationLabel(b).toString()));
        String[] names=new String[apps.size()];
        for(int i=0;i<apps.size();i++) names[i]=pm.getApplicationLabel(apps.get(i)).toString();
        new AlertDialog.Builder(this).setTitle("Aplicaciones instaladas").setItems(names,(d,which)->{
            Intent launch=pm.getLaunchIntentForPackage(apps.get(which).packageName);
            if(launch!=null) try{startActivity(launch);}catch(Exception e){toast("No se pudo abrir la aplicación.");}
        }).setNegativeButton("Cerrar",null).show();
    }
    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch(Exception e) { try{startActivity(new Intent(Settings.ACTION_SETTINGS));}catch(Exception ignored){toast("Ajustes no disponibles.");} }
    }
    private void openTv() {
        if(launchFirst(new String[]{"com.realtek.dtv","com.android.tv","com.toptech.launcher"}))
            return;
        toast("No se encontró una aplicación de TV accesible. La entrada puede requerir una función del fabricante.");
    }
    private void openFileBrowser() {
        if(launchFirst(new String[]{"com.rtk.mediabrowser","com.realtek.media.player","com.android.documentsui","com.google.android.tv"}))
            return;
        try { Intent i=new Intent(Intent.ACTION_GET_CONTENT); i.setType("*/*"); startActivity(Intent.createChooser(i,"Abrir archivo")); }
        catch(Exception e){toast("No hay un explorador de archivos disponible.");}
    }
    private void showInputNotice() {
        new AlertDialog.Builder(this).setTitle("Entradas HDMI")
            .setMessage("El cambio de entrada depende del firmware del televisor. Esta aplicación no puede garantizar el control de HDMI en este modelo; no se mostrará una entrada como seleccionada si no podemos cambiarla realmente.")
            .setPositiveButton("Entendido",null).show();
    }
    private boolean launchFirst(String[] packages) {
        for(String pkg:packages) {
            Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
            if(i!=null) try{startActivity(i);return true;}catch(Exception ignored){}
        }
        return false;
    }
    private int dp(float value){return (int)(value*getResources().getDisplayMetrics().density+0.5f);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
    @Override public boolean onKeyDown(int keyCode,KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_MENU){showAppPicker();return true;}
        return super.onKeyDown(keyCode,event);
    }
}