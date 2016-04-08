package edu.umd.hcil.impressionistpainter434;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by jon on 3/20/2016.
 */
public class ImpressionistView extends View {

    private ImageView _imageView;

    private Canvas _offScreenCanvas = null;
    private Bitmap _offScreenBitmap = null;
    private Bitmap _loadedBitmap=null;
    private Paint _paint = new Paint();
    private Path _path= new Path();
    private ArrayList<Float> XYpoints = new ArrayList<Float>();
    private ArrayList<Point> points = new ArrayList<Point>();
    private static final int DEFAULT_BRUSH_RADIUS = 25;
    private long _elapsedTimeProcessingTouchEventsInMs = 0;

    private Rect _border;
    private int _alpha = 255;
    private int _defaultRadius = 25;
    private Point _lastPoint = null;
    private long _lastPointTime = -1;
    private boolean _useMotionSpeedForBrushStrokeSize = true;
    private Paint _paintBorder = new Paint();
    private BrushType _brushType = BrushType.Square;
    private float _minBrushRadius = 15;
    private Toast _empty;

    private float currX;
    private float currY;
    private boolean touched=false;


    private final static String CONSUMER_KEY = "pTnEIs3CxUJOvojuVrSDSAhXR";

    // Consumer Secret

    private final static String CONSUMER_SECRET = "ZIO7WJp6nCK6SMrmzBNKd7PJJjKF46vTcFYmzBqgCxPwXhoHdm";

    // Access Token
    private static String ACCESS_TOKEN = "717425759971905536-L10ddsAQ1poq3DIzE3L4O4ze6lDtYBw";

    // Access Token Secret
    private static String ACCESS_TOKEN_SECRET = "htXCEX3B9cawmZEJYOx9gQbbQaTUFuEspH6F1B8xUd2oS";

    File _imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ImpressionistPainter434");

    private static final String TAG = "PictureTweetService";


    private long startTime =0;


    public ImpressionistView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Because we have more than one constructor (i.e., overloaded constructors), we use
     * a separate initialization method
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle){

        // Set setDrawingCacheEnabled to true to support generating a bitmap copy of the view (for saving)
        // See: http://developer.android.com/reference/android/view/View.html#setDrawingCacheEnabled(boolean)
        //      http://developer.android.com/reference/android/view/View.html#getDrawingCache()
        this.setDrawingCacheEnabled(true);

        _paint.setColor(Color.RED);
        _paint.setAlpha(_alpha);
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.FILL);
        _paint.setStrokeWidth(40);

        _paintBorder.setColor(Color.BLACK);
        _paintBorder.setStrokeWidth(3);
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setAlpha(50);

        //_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){

        Bitmap bitmap = getDrawingCache();
        Log.v("onSizeChanged", MessageFormat.format("bitmap={0}, w={1}, h={2}, oldw={3}, oldh={4}", bitmap, w, h, oldw, oldh));
        if(bitmap != null) {
            _offScreenBitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
            _offScreenBitmap.setHasAlpha(true);
            _offScreenCanvas = new Canvas(_offScreenBitmap);
        }
    }

    /**
     * Sets the ImageView, which hosts the image that we will paint in this view
     * @param imageView
     */
    public void setImageView(ImageView imageView){
        _imageView = imageView;

       // _loadedBitmap= ((BitmapDrawable)_imageView.getDrawable()).getBitmap();
    }

    public void setMyImageBitmap(Bitmap bitmap){
        //_loadedBitmap=bitmap;
        //_loadedBitmap=Bitmap.createScaledBitmap(_loadedBitmap, _imageView.getWidth(), _imageView.getHeight(), true);
        _border=getBitmapPositionInsideImageView(_imageView);
        _loadedBitmap=Bitmap.createScaledBitmap(bitmap, _border.width(), _border.height(), true);
        _loadedBitmap.setHasAlpha(true);
    }
    public Bitmap getMyImageBitmap(){


        _offScreenBitmap.setHasAlpha(true);
        return _offScreenBitmap;
    }
    public Bitmap getOrigImageBitmap(){return _loadedBitmap;}

    /**
     * Sets the brush type. Feel free to make your own and completely change my BrushType enum
     * @param brushType
     */
    public void setBrushType(BrushType brushType){
        _brushType = brushType;
    }

    /**
     * Clears the painting
     */
    public void clearPainting(){
       _offScreenCanvas.drawColor(Color.WHITE);
        invalidate();
    }
    public void save(){
        if(_loadedBitmap!=null){
            String fileName= "img"+(new SimpleDateFormat("yyMMddhhmm").format(new Date()))+ ".png";
            File file = new File(_imageRoot,fileName);
            try{

                FileOutputStream outputStream = new FileOutputStream(file);
                _offScreenBitmap.compress(Bitmap.CompressFormat.PNG, 95, outputStream);
                outputStream.flush();
                outputStream.close();

                //MediaScannerConnection.scanFile(this, new String[]{_imageRoot.toString()}, null, null);
                //MediaStore.Images.Media.insertImage(getContentResolver(), _impressionistView.getMyImageBitmap(), "fileName", "picture");

                FileUtils.addImageToGallery(file.getAbsolutePath(), this.getContext().getApplicationContext());


                Toast.makeText(super.getContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(super.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }


        }else{
            Toast toast=Toast.makeText(super.getContext(), "No Picture To Save", Toast.LENGTH_SHORT);
            toast.show();
        }


    }


    public void saveAndPush(){
        if(_loadedBitmap!=null){

            final String impressionImageFileName= "impressionImg"+(new SimpleDateFormat("yyMMddhhmm").format(new Date()))+ ".png";
            final String origImageFileName= "origImg"+(new SimpleDateFormat("yyMMddhhmm").format(new Date()))+ ".png";
            final String statusFileName="img"+(new SimpleDateFormat("yyMMddhhmm").format(new Date()))+ ".png";
            Long delay = 1000L;
            final File impressionFile = new File(_imageRoot,impressionImageFileName);
            final File origImageFile= new File(_imageRoot, origImageFileName);


            //code based on: https://github.com/learnNcode/DemoTwitterImagePost/blob/master/src/com/learnncode/demotwitterimagepost/HelperMethods.java
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
            configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
            configurationBuilder.setOAuthAccessToken(ACCESS_TOKEN);
            configurationBuilder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
            Configuration configuration = configurationBuilder.build();

            final Twitter twitter= new TwitterFactory(configuration).getInstance();

            try{
                FileOutputStream outputStreamImpressionImage = new FileOutputStream(impressionFile);
                FileOutputStream outputStreamOrigImage = new FileOutputStream(origImageFile);




                _offScreenBitmap.setHasAlpha(true);



                _loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStreamOrigImage);
                _offScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStreamImpressionImage);
                outputStreamImpressionImage.flush();
                outputStreamImpressionImage.close();

                FileUtils.addImageToGallery(impressionFile.getAbsolutePath(), this.getContext().getApplicationContext());

                //outputStreamOrigImage.flush();
                //outputStreamOrigImage.close();

                //MediaScannerConnection.scanFile(super.getContext(), new String[]{_imageRoot.toString()}, null, null);

                //MediaStore.Images.Media.insertImage(getContext().getContentResolver(), b,
                //"Painting" + savedNum, "This is painting number " + savedNum++);



                Toast.makeText(super.getContext(), impressionFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(super.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }


            new Thread(new Runnable() {

                private double x;

                @Override
                public void run() {
                    boolean success = true;
                    try {
                        x = Math.random();
                        if (impressionFile.exists()) {
                            long[] mediaIds= new long[2];


                            UploadedMedia mediaOrig=twitter.uploadMedia(origImageFile);
                            mediaIds[0]= mediaOrig.getMediaId();


                            UploadedMedia mediaImpression = twitter.uploadMedia(impressionFile);
                            mediaIds[1]= mediaImpression.getMediaId();




                            StatusUpdate status = new StatusUpdate(statusFileName);

                            status.setMediaIds(mediaIds);


                            twitter.updateStatus(status);
                        }else{
                            Log.d(TAG, "----- Invalid File ----------");
                            success = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        success = false;
                    }



                    final boolean finalSuccess = success;

                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFinsihed(finalSuccess);
                        }
                    });

                }
            }).start();

        }else{
            Toast toast=Toast.makeText(super.getContext(), "No Picture To Save", Toast.LENGTH_SHORT);
            toast.show();
        }


    }
    private void onFinsihed(boolean finalSuccess) {
        Log.d(TAG, "----------------response----------------" + finalSuccess);
        Toast.makeText(super.getContext(), "image post status:"+finalSuccess, Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(_offScreenBitmap != null) {
            canvas.drawBitmap(_offScreenBitmap, 0, 0, _paint);
        }

        // Draw the border. Helpful to see the size of the bitmap in the ImageView
        _border=getBitmapPositionInsideImageView(_imageView);
        canvas.drawRect(_border, _paintBorder);

    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        //TODO
        //Basically, the way this works is to liste for Touch Down and Touch Move events and determine where those
        //touch locations correspond to the bitmap in the ImageView. You can then grab info about the bitmap--like the pixel color--
        //at that location
        float curTouchX = motionEvent.getX();
        float curTouchY = motionEvent.getY();
        int curTouchXRounded = (int) curTouchX;
        int curTouchYRounded = (int) curTouchY;
        float brushRadius = DEFAULT_BRUSH_RADIUS;
        touched=true;




        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                _paint.setAlpha(_alpha);
                if (_loadedBitmap == null) {
                    startTime = SystemClock.elapsedRealtime();
                    if (_empty != null) {
                        _empty.cancel();
                    }

                    _empty = Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                    _empty.show();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if(_brushType.equals(BrushType.Square)) {
                    _paint.setStrokeCap(Paint.Cap.SQUARE);
                    _paint.setAlpha(_alpha);
                    int historySize = motionEvent.getHistorySize();
                    for (int i = 0; i < historySize; i++) {

                        float touchX = motionEvent.getHistoricalX(i);
                        float touchY = motionEvent.getHistoricalY(i);



                        // TODO: draw to the offscreen bitmap for historical x,y points
                        if (_loadedBitmap != null) {
                            if (_border.contains((int) touchX, (int) touchY)) {

                                int pixel = _loadedBitmap.getPixel((int) touchX - _border.left, (int) touchY - _border.top);

                                _paint.setAlpha(_alpha);
                                _paint.setColor(pixel);
                                _offScreenCanvas.drawPoint(touchX, touchY, _paint);
                            }
                        }else{
                                //_offScreenCanvas.drawPoint(touchX, touchY, _paint);

                            if(_empty !=null){
                                _empty.cancel();
                            }

                          _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                          _empty.show();
                        }
                    }



                    // TODO: draw to the offscreen bitmap for current x,y point.


                    if (_loadedBitmap != null) {
                        if (_border.contains(curTouchXRounded, curTouchYRounded)) {
                            int pixel = _loadedBitmap.getPixel((int) curTouchX - _border.left, (int) curTouchY - _border.top);
                            _paint.setColor(pixel);
                            _paint.setAlpha(_alpha);

                            _offScreenCanvas.drawPoint(curTouchXRounded, curTouchYRounded, _paint);
                        }
                    }else{
                        if(_empty !=null){
                            _empty.cancel();
                        }
                      _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                      _empty.show();
                    }



                }else if(_brushType.equals(BrushType.Circle)){
                    _paint.setStrokeCap(Paint.Cap.ROUND);
                    _paint.setAlpha(_alpha);
                    int historySize = motionEvent.getHistorySize();
                    for (int i = 0; i < historySize; i++) {

                        float touchX = motionEvent.getHistoricalX(i);
                        float touchY = motionEvent.getHistoricalY(i);



                        // TODO: draw to the offscreen bitmap for historical x,y points
                        if (_loadedBitmap != null) {
                            if (_border.contains((int) touchX, (int) touchY)) {

                                int pixel = _loadedBitmap.getPixel((int) touchX - _border.left, (int) touchY - _border.top);
                                _paint.setColor(pixel);
                                _paint.setAlpha(_alpha);
                                _offScreenCanvas.drawPoint(touchX, touchY, _paint);

                            }
                        }else{
                            if(_empty !=null){
                                _empty.cancel();
                            }
                            _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                            _empty.show();
                        }





                    }

                    // TODO: draw to the offscreen bitmap for current x,y point.




                    if (_loadedBitmap != null) {
                        if (_border.contains(curTouchXRounded, curTouchYRounded)) {

                            int pixel = _loadedBitmap.getPixel((int) curTouchX - _border.left, (int) curTouchY - _border.top);
                            _paint.setColor(pixel);
                            _paint.setAlpha(_alpha);

                            _offScreenCanvas.drawPoint(curTouchXRounded, curTouchYRounded, _paint);
                        }
                    }else{
                        if(_empty !=null){
                            _empty.cancel();
                        }
                      _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                      _empty.show();
                    }




                }else if(_brushType.equals(BrushType.DynamicSquare)){
                    _paint.setStrokeCap(Paint.Cap.SQUARE);
                    _paint.setAlpha(_alpha);
                    double velocity=0;

                    int historySize = motionEvent.getHistorySize();
                    float lastX=0;
                    float lastY=0;
                    for (int i = 0; i < historySize; i++) {

                        if(i==0){
                            lastX=motionEvent.getHistoricalX(i);
                            lastY=motionEvent.getHistoricalY(i);
                        }


                        float touchX = motionEvent.getHistoricalX(i);
                        float touchY = motionEvent.getHistoricalY(i);

                        float diffX= motionEvent.getX()-touchX;
                        float diffY= motionEvent.getY()-touchY;

                        double distance= Math.sqrt((diffX * diffX) + (diffY * diffY));



                        long endTime = SystemClock.elapsedRealtime();
                        _elapsedTimeProcessingTouchEventsInMs = endTime - startTime;


                        velocity= distance/_elapsedTimeProcessingTouchEventsInMs;
                        Log.i("VELOCITY", "currVelocityis:"+velocity);


                        if (_loadedBitmap != null) {
                            if(velocity<40000) {
                                _paint.setStrokeWidth((float) velocity * _minBrushRadius);
                            } else{
                                _paint.setStrokeWidth(40);
                            }
                            if (_border.contains((int) touchX, (int) touchY)) {

                                int pixel = _loadedBitmap.getPixel((int) touchX - _border.left, (int) touchY - _border.top);

                                _paint.setAlpha(_alpha);
                                _paint.setColor(pixel);
                                _offScreenCanvas.drawPoint(touchX, touchY, _paint);
                            }
                        }else{
                            //_offScreenCanvas.drawPoint(touchX, touchY, _paint);

                            if(_empty !=null){
                                _empty.cancel();
                            }

                            _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                            _empty.show();
                        }




                    }

                    //float touchX = lastX;
                    //float touchY = lastY;

                    //float diffX= motionEvent.getX()-touchX;
                    //float diffY= motionEvent.getY()-touchY;

                    //double distance= Math.sqrt((diffX * diffX) + (diffY * diffY));



                    //long endTime = SystemClock.elapsedRealtime();
                    //_elapsedTimeProcessingTouchEventsInMs += endTime - startTime;


                     //velocity= distance/_elapsedTimeProcessingTouchEventsInMs;
                    Log.i("VELOCITY", "currVelocityis:"+velocity);

                    if (_loadedBitmap != null) {
                        if(velocity<40000) {
                            _paint.setStrokeWidth((float) velocity * _minBrushRadius);
                        } else{
                            _paint.setStrokeWidth(40);
                        }
                        if (_border.contains(curTouchXRounded, curTouchYRounded)) {
                            int pixel = _loadedBitmap.getPixel((int) curTouchX - _border.left, (int) curTouchY - _border.top);
                            _paint.setColor(pixel);
                            _paint.setAlpha(_alpha);

                            _offScreenCanvas.drawPoint(curTouchXRounded, curTouchYRounded, _paint);
                        }
                    }else{
                        if(_empty !=null){
                            _empty.cancel();
                        }
                        _empty= Toast.makeText(super.getContext(), "LOAD A PICTURE", Toast.LENGTH_SHORT);
                        _empty.show();
                    }

                }
                _paint.setAlpha(_alpha);

                startTime = SystemClock.elapsedRealtime();

                break;
            case MotionEvent.ACTION_UP:
                _elapsedTimeProcessingTouchEventsInMs=0;
                _paint.setStrokeWidth(40);
                _paint.setAlpha(_alpha);
                break;
        }
        invalidate();
        return true;


    }





    /**
     * This method is useful to determine the bitmap position within the Image View. It's not needed for anything else
     * Modified from:
     *  - http://stackoverflow.com/a/15538856
     *  - http://stackoverflow.com/a/26930938
     * @param imageView
     * @return
     */
    private static Rect getBitmapPositionInsideImageView(ImageView imageView){
        Rect rect = new Rect();

        if (imageView == null || imageView.getDrawable() == null) {
            return rect;
        }

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int widthActual = Math.round(origW * scaleX);
        final int heightActual = Math.round(origH * scaleY);

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - heightActual)/2;
        int left = (int) (imgViewW - widthActual)/2;

        rect.set(left, top, left + widthActual, top + heightActual);

        return rect;
    }
}
