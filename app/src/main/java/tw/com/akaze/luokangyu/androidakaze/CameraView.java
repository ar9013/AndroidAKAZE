package tw.com.akaze.luokangyu.androidakaze;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;



/**
 * Created by luokangyu on 2017/1/10.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {


    private static String TAG = "CameraView";
    SurfaceView surfacePreview;
    private SurfaceHolder holder;
    Camera.Size  previewSize;
    List<Camera.Size> previewSizeList;
    private Camera cameraObj;



    public CameraView(Context context, SurfaceView surfaceView) {
        super(context);

        surfacePreview = surfaceView;

        holder = surfacePreview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    public void setCamera(Camera camera) {
        cameraObj = camera;
        if (cameraObj != null) {
            previewSizeList = cameraObj.getParameters().getSupportedPreviewSizes();
            requestLayout();

            // get Camera parameters

            Camera.Parameters params = cameraObj.getParameters();



            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                // This is an undocumented although widely known feature
                   params.set("orientation", "portrait");
                    camera.setDisplayOrientation(90);
            }else{
                params.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
            }



            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters
                cameraObj.setParameters(params);
            }
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try{
            if(cameraObj != null){
                cameraObj.setPreviewDisplay(holder);
            }

        }catch (Exception ex){
            Log.e(TAG, "IOException caused by setPreviewDisplay()", ex);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {

        if(cameraObj != null){
            Camera.Parameters parameters = cameraObj.getParameters();
            parameters.setPreviewSize(previewSize.width,previewSize.height);
            requestLayout();

            cameraObj.setParameters(parameters);
            cameraObj.startPreview();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(cameraObj != null){
            cameraObj.stopPreview();
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (previewSizeList != null) {
            previewSize = getOptimalPreviewSize(previewSizeList, width, height);
        }
    }

}
