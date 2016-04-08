package edu.umd.hcil.impressionistpainter434;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TWEET_STRING="picture";

    private static final String TAG = "PictureTweetService";
    private  ImpressionistView _impressionistView;

    private final static String CONSUMER_KEY = "pTnEIs3CxUJOvojuVrSDSAhXR";

    // Consumer Secret

    private final static String CONSUMER_SECRET = "ZIO7WJp6nCK6SMrmzBNKd7PJJjKF46vTcFYmzBqgCxPwXhoHdm";

    // Access Token
    private static String ACCESS_TOKEN = "717425759971905536-L10ddsAQ1poq3DIzE3L4O4ze6lDtYBw";

    // Access Token Secret
    private static String ACCESS_TOKEN_SECRET = "htXCEX3B9cawmZEJYOx9gQbbQaTUFuEspH6F1B8xUd2oS";

    // networking variables
    private static SecureRandom mRandom;

    AlarmManager _alarmManager;
    File _imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ImpressionistPainter434");

    // These images are downloaded and added to the Android Gallery when the 'Download Images' button is clicked.
    // This was super useful on the emulator where there are no images by default
    private static String[] IMAGE_URLS ={
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/BoliviaBird_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/BolivianDoor_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/MinnesotaFlower_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/PeruHike_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/ReginaSquirrel_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/SucreDog_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/SucreStreet_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/SucreStreet_PhotoByJonFroehlich2(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/SucreWine_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/WashingtonStateFlower_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/JonILikeThisShirt_Medium.JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/JonUW_(853x1280).jpg",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/MattMThermography_Medium.jpg",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/PinkFlower_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/PinkFlower2_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/PurpleFlowerPlusButterfly_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/WhiteFlower_PhotoByJonFroehlich(Medium).JPG",
            "http://www.cs.umd.edu/class/spring2016/cmsc434/assignments/IA08-AndroidII/Images/YellowFlower_PhotoByJonFroehlich(Medium).JPG",
            "https://pbs.twimg.com/media/CfUXAlkUMAA7lTg.jpg",
            "https://pbs.twimg.com/media/CfUXA-sUEAEmBo5.jpg",
            "https://pbs.twimg.com/media/CfUW-xqVIAADFeg.jpg",
            "https://pbs.twimg.com/media/CfUXAx0UsAAAezI.jpg",
            "http://uploads3.wikiart.org/images/vincent-van-gogh/the-starry-night-1889(1).jpg!Large.jpg",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _impressionistView = (ImpressionistView)findViewById(R.id.viewImpressionist);
        ImageView imageView = (ImageView)findViewById(R.id.viewImage);
        _impressionistView.setImageView(imageView);
        _imageRoot.mkdirs();
        //_alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


    }


    public void onButtonClickClear(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Clear Painting?")
                .setMessage("Do you really want to clear your painting?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MainActivity.this, "Painting cleared", Toast.LENGTH_SHORT).show();
                        _impressionistView.clearPainting();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onButtonClickSetBrush(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }
    public void onButtonClickSave(View v){
        _impressionistView.save();
       /* if(_impressionistView.getMyImageBitmap()!=null){
            String fileName= "img"+(new SimpleDateFormat("yyMMddhhmm").format(new Date()))+ ".png";
            File file = new File(_imageRoot,fileName);
            try{

                FileOutputStream outputStream = new FileOutputStream(file);
                _impressionistView.getMyImageBitmap().compress(Bitmap.CompressFormat.PNG, 95, outputStream);
                outputStream.flush();
                outputStream.close();

                MediaScannerConnection.scanFile(this, new String[]{_imageRoot.toString()}, null, null);
                MediaStore.Images.Media.insertImage(getContentResolver(), _impressionistView.getMyImageBitmap(), "fileName", "picture");

                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }


        }else{
            Toast toast=Toast.makeText(this, "No Picture To Save", Toast.LENGTH_SHORT);
            toast.show();
        }*/
    }
    public void onButtonClickSaveAndPost(View v){
        _impressionistView.saveAndPush();
    }




    public void onButtonClickSaveAndPostOld(View v){
        if(_impressionistView.getMyImageBitmap()!=null){
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


                Bitmap tempImpression= _impressionistView.getMyImageBitmap();

                tempImpression.setHasAlpha(true);



                _impressionistView.getOrigImageBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStreamOrigImage);
                tempImpression.compress(Bitmap.CompressFormat.PNG, 50, outputStreamImpressionImage);
                outputStreamImpressionImage.flush();
                outputStreamImpressionImage.close();

                //outputStreamOrigImage.flush();
                //outputStreamOrigImage.close();

                //MediaScannerConnection.scanFile(this, new String[]{_imageRoot.toString()}, null, null);

                Toast.makeText(this, impressionFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          onFinsihed(finalSuccess);
                      }
                  });

                }
            }).start();

    }else{
            Toast toast=Toast.makeText(this, "No Picture To Save", Toast.LENGTH_SHORT);
            toast.show();
        }



    }

    private void onFinsihed(boolean finalSuccess) {
        Log.d(TAG, "----------------response----------------" + finalSuccess);
        Toast.makeText(this, "image post status:"+finalSuccess, Toast.LENGTH_SHORT).show();
    }





    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCircle:
                Toast.makeText(this, "Circle Brush", Toast.LENGTH_SHORT).show();
                _impressionistView.setBrushType(BrushType.Circle);
                return true;
            case R.id.menuSquare:
                Toast.makeText(this, "Square Brush", Toast.LENGTH_SHORT).show();
                _impressionistView.setBrushType(BrushType.Square);
                return true;
            case R.id.menuDynamicSquare:
                Toast.makeText(this, "DyanmicSquare Brush", Toast.LENGTH_SHORT).show();
                _impressionistView.setBrushType(BrushType.DynamicSquare);
                return true;

        }
        return false;
    }


    /**
     * Downloads test images to use in the assignment. Feel free to use any images you want. I only made this
     * as an easy way to get images onto the emulator.
     *
     * @param v
     */
    public void onButtonClickDownloadImages(View v){

        // Without this call, the app was crashing in the onActivityResult method when trying to read from file system
        FileUtils.verifyStoragePermissions(this);

        // Amazing Stackoverflow post on downloading images: http://stackoverflow.com/questions/15549421/how-to-download-and-save-an-image-in-android
        final BasicImageDownloader imageDownloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {

            @Override
            public void onError(String imageUrl, BasicImageDownloader.ImageError error) {
                Log.v("BasicImageDownloader", "onError: " + error);
            }

            @Override
            public void onProgressChange(String imageUrl, int percent) {
                Log.v("BasicImageDownloader", "onProgressChange: " + percent);
            }

            @Override
            public void onComplete(String imageUrl, Bitmap downloadedBitmap) {
                File externalStorageDirFile = Environment.getExternalStorageDirectory();
                String externalStorageDirStr = Environment.getExternalStorageDirectory().getAbsolutePath();
                boolean checkStorage = FileUtils.checkPermissionToWriteToExternalStorage(MainActivity.this);
                String guessedFilename = URLUtil.guessFileName(imageUrl, null, null);

                // See: http://developer.android.com/training/basics/data-storage/files.html
                // Get the directory for the user's public pictures directory.
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), guessedFilename);
                try {
                    boolean compressSucceeded = downloadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                    FileUtils.addImageToGallery(file.getAbsolutePath(), getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        for(String url: IMAGE_URLS){
            imageDownloader.download(url, true);
        }
    }

    /**
     * Loads an image from the Gallery into the ImageView
     *
     * @param v
     */
    public void onButtonClickLoadImage(View v){

        // Without this call, the app was crashing in the onActivityResult method when trying to read from file system
        FileUtils.verifyStoragePermissions(this);

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    /**
     * Called automatically when an image has been selected in the Gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.viewImage);

                // destroy the drawing cache to ensure that when a new image is loaded, its cached
                imageView.destroyDrawingCache();
                imageView.setImageBitmap(bitmap);
                imageView.setDrawingCacheEnabled(true);
                _impressionistView.setMyImageBitmap(bitmap);
                //_impressionistView.setLayoutParams(new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static abstract class TwitterCallback{
        public abstract void onFinsihed(Boolean success);
    }

}

