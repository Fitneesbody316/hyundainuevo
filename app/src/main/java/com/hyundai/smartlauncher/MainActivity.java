package com.hyundai.smartlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {
    private static final int REF_W = 1671;
    private static final int REF_H = 941;
    private FrameLayout root;
    private final String[] favoritePackages = {
        "com.google.android.youtube.tv",
        "com.netflix.ninja",
        "com.amazon.amazonvideo.livingroom",
        "com.disney.disneyplus",
        "com.google.android.videos",
        "tv.pluto.android",
        "com.plexapp.android",
        "org.videolan.vlc",
        "com.android.chrome"
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        ImageView screen = new ImageView(this);
        screen.setImageResource(com.hyundai.smartlauncher.R.drawable.launcher_screen);
        screen.setScaleType(ImageView.ScaleType.FIT_XY);
        root.addView(screen, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
        root.post(this::installHotspots);
    }

    private void installHotspots() {
        // Main cards: coordinates correspond to the 1671x941 approved reference image.
        addHotspot("TV", 42, 178, 454, 551, () -> runInput("TV"), true);
        addHotspot("HDMI", 470, 181, 847, 552, this::showInputs, false);
        addHotspot("USB", 852, 181, 1232, 552, () -> runInput("USB"), false);
        addHotspot("Aplicaciones", 1240, 181, 1611, 552, this::showAppPicker, false);

        // Utility buttons.
        addHotspot("Limpiar caché de todas las apps", 45, 572, 502, 675, this::showCacheInfo, false);
        addHotspot("Gestionar apps", 515, 572, 874, 675,
            () -> openSettings(Settings.ACTION_APPLICATION_SETTINGS), false);
        addHotspot("Configuración", 883, 572, 1267, 675,
            () -> openSettings(Settings.ACTION_SETTINGS), false);
        addHotspot("Red", 1278, 572, 1609, 675,
            () -> openSettings(Settings.ACTION_WIFI_SETTINGS), false);

        // Favorite app cards, plus Add App.
        int[][] fav = {
            {48,738,176,884},{204,738,333,884},{361,738,490,884},
            {516,738,647,884},{671,738,802,884},{827,738,963,884},
            {984,738,1123,884},{1142,738,1281,884},{1300,738,1440,884}
        };
        for (int i=0; i<fav.length; i++) {
            final String pkg = favoritePackages[i];
            int[] b = fav[i];
            addHotspot(labelFor(pkg), b[0],b[1],b[2],b[3], () -> launchOrExplain(pkg), false);
        }
        addHotspot("Añadir app", 1457,738,1608,884, this::showAppPicker, false);
        // Settings gear in header.
        addHotspot("Ajustes", 1504, 18, 1640, 132,
            () -> openSettings(Settings.ACTION_SETTINGS), false);
    }

    private void addHotspot(String label, int x1, int y1, int x2, int y2, Runnable action, boolean initial) {
        FocusOverlay overlay = new FocusOverlay();
        overlay.setFocusable(true);
        overlay.setFocusableInTouchMode(false);
        overlay.setContentDescription(label);
        overlay.setOnClickListener(v -> action.run());
        overlay.setOnFocusChangeListener((v, focused) -> {
            v.invalidate();
            if (focused) {
                v.setScaleX(1.006f);
                v.setScaleY(1.006f);
            } else {
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
            Math.max(1, (int)((x2-x1) * root.getWidth() / (float)REF_W)),
            Math.max(1, (int)((y2-y1) * root.getHeight() / (float)REF_H)));
        lp.leftMargin = (int)(x1 * root.getWidth() / (float)REF_W);
        lp.topMargin = (int)(y1 * root.getHeight() / (float)REF_H);
        root.addView(overlay, lp);
        if (initial) overlay.requestFocus();
    }

    private class FocusOverlay extends View {
        private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
        FocusOverlay() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setBackgroundColor(Color.TRANSPARENT);
            glow.setColor(Color.rgb(82,255,65));
            glow.setStyle(Paint.Style.STROKE);
            glow.setStrokeWidth(dp(8));
            glow.setMaskFilter(new BlurMaskFilter(dp(10), BlurMaskFilter.Blur.NORMAL));
            line.setColor(Color.rgb(115,255,70));
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeWidth(dp(3));
        }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            if (hasFocus()) {
                RectF r = new RectF(dp(5),dp(5),getWidth()-dp(5),getHeight()-dp(5));
                c.drawRoundRect(r,dp(16),dp(16),glow);
                c.drawRoundRect(r,dp(16),dp(16),line);
            }
        }
    }

    private void showInputs() {
        String[] inputs = {"TV", "HDMI 1", "HDMI 2", "HDMI 3", "AV", "Reproductor", "USB"};
        new AlertDialog.Builder(this).setTitle("Entradas del televisor")
            .setItems(inputs, (d, which) -> runInput(inputs[which]))
            .setNegativeButton("Cancelar", null).show();
    }

    private void runInput(String input) {
        if ("USB".equals(input) || "Reproductor".equals(input)) {
            if (launchFirst(new String[]{"com.rtk.mediabrowser","com.realtek.media.player",
                "com.android.documentsui","com.google.android.tv"})) return;
            toast("No se encontró un reproductor compatible en el televisor.");
            return;
        }
        if ("TV".equals(input)) {
            if (launchFirst(new String[]{"com.android.tv","com.realtek.dtv","com.toptech.launcher"})) return;
        } else {
            if (launchFirst(new String[]{"com.realtek.tv.hdmitvinput","com.android.tv"})) return;
        }
        toast("El cambio directo de entrada depende del firmware Hyundai/Realtek.");
    }

    private boolean launchFirst(String[] packages) {
        for (String p : packages) {
            Intent i = getPackageManager().getLaunchIntentForPackage(p);
            if (i != null) {
                try { startActivity(i); return true; } catch (Exception ignored) {}
            }
        }
        return false;
    }

    private void launchOrExplain(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i == null) {
            toast("Esta aplicación no está instalada. Puedes agregar otra desde Añadir app.");
            return;
        }
        try { startActivity(i); } catch (Exception e) { toast("No se pudo abrir la aplicación."); }
    }

    private void showAppPicker() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = new ArrayList<>();
        for (ApplicationInfo ai : pm.getInstalledApplications(0)) {
            if (!ai.packageName.equals(getPackageName()) && pm.getLaunchIntentForPackage(ai.packageName) != null)
                apps.add(ai);
        }
        Collections.sort(apps, (a,b) -> pm.getApplicationLabel(a).toString()
            .compareToIgnoreCase(pm.getApplicationLabel(b).toString()));
        String[] labels = new String[apps.size()];
        for (int i=0; i<apps.size(); i++) labels[i] = pm.getApplicationLabel(apps.get(i)).toString();
        new AlertDialog.Builder(this).setTitle("Abrir aplicación")
            .setItems(labels, (d, which) -> {
                String pkg = apps.get(which).packageName;
                Intent launch = pm.getLaunchIntentForPackage(pkg);
                if (launch != null) startActivity(launch);
            }).setNegativeButton("Cerrar", null).show();
    }

    private void showCacheInfo() {
        new AlertDialog.Builder(this).setTitle("Limpiar caché de todas las apps")
            .setMessage("Android 7.1.1 no permite a un launcher común borrar la caché privada de todas las aplicaciones sin permisos de sistema. Puedes abrir Ajustes y limpiar el almacenamiento/caché desde las opciones que permita tu televisor.")
            .setPositiveButton("Abrir ajustes", (d,w) -> openSettings(Settings.ACTION_SETTINGS))
            .setNegativeButton("Cancelar", null).show();
    }

    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception e) {
            try { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
            catch (Exception ignored) { toast("Ajustes no disponibles."); }
        }
    }

    private String labelFor(String pkg) {
        try { return getPackageManager().getApplicationLabel(
            getPackageManager().getApplicationInfo(pkg,0)).toString(); }
        catch (Exception e) { return pkg; }
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + .5f);
    }

    private void toast(String text) { Toast.makeText(this,text,Toast.LENGTH_LONG).show(); }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) { showAppPicker(); return true; }
        return super.onKeyDown(keyCode,event);
    }
}
