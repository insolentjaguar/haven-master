package org.havenapp.main;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.orm.SugarContext;

import java.io.IOException;

import org.havenapp.main.service.SignalSender;
import org.havenapp.main.service.WebServer;

public class HavenApp extends MultiDexApplication {


    /*
    ** Onion-available Web Server for optional remote access
     */
    private WebServer mOnionServer = null;

    private PreferenceManager mPrefs = null;

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);

        mPrefs = new PreferenceManager(this);

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(this,config);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (mPrefs.getRemoteAccessActive())
            startServer();
    }


    public void startServer ()
    {
        if (mOnionServer == null || (!mOnionServer.isAlive()))
        {
            if ( mPrefs.getRemoteAccessCredential() != null) {
                try {
                    mOnionServer = new WebServer(this, mPrefs.getRemoteAccessCredential());
                } catch (IOException ioe) {
                    Log.e("OnioNServer", "unable to start onion server", ioe);
                }
            }
        }
    }

    public void stopServer ()
    {
        if (mOnionServer != null && mOnionServer.isAlive())
        {
            mOnionServer.stop();
        }
    }
}
