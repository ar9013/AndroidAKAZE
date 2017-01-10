package tw.com.akaze.luokangyu.androidakaze;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;

import android.view.*;
import android.widget.FrameLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    CameraView cameraView;
    Camera camera;
    Activity activity;
    Context context;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = MainActivity.this;
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        cameraView = new CameraView(this,(SurfaceView)findViewById(R.id.surfaceView));
        cameraView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout =((FrameLayout)findViewById(R.id.layout));
        frameLayout .addView(cameraView);

        cameraView.setKeepScreenOn(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        int numCamera = Camera.getNumberOfCameras();
        if(numCamera >0){
            try{
                camera = Camera.open(0);

                camera.startPreview();
                cameraView.setCamera(camera);

            }catch (Exception ex){
                Toast.makeText(context,"No camera haradware found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(camera != null){
            camera.stopPreview();
            cameraView.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

}
