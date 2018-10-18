package com.example.mukul.bismillah;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    OcrManager manager;
    TextView textView;
    Button btnSelectImage1;
    Bitmap dstBmp;
    private int GALLERY = 1, CAMERA = 2;
    ImageView img;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       textView=(TextView)findViewById(R.id.textView);
        img=(ImageView)findViewById(R.id.img);

        btnSelectImage1 = (Button) findViewById(R.id.btnSelectImage);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         manager = new OcrManager();
        manager.initAPI();

        // no error, init api ok.
        // how to using it roconignize text
        //baseAPI.setImage(bitmap);
        //return baseAPI.getUTF8Text();

        //String s=manager.startRecognize(m);
        //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
        //doOCR(convertColorIntoBlackAndWhiteImage(dstBmp) );
        btnSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();

            }
        });




    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    /* Choose an image from Gallery */
  /*  void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                 /*   if (bitmap.getWidth() >= bitmap.getHeight()){

                        dstBmp = Bitmap.createBitmap(
                                bitmap,
                                bitmap.getWidth()/2 - bitmap.getHeight()/2,
                                0,
                                bitmap.getHeight(),
                                bitmap.getHeight()
                        );

                    }else{

                        dstBmp = Bitmap.createBitmap(
                                bitmap,
                                0,
                                bitmap.getHeight()/2 - bitmap.getWidth()/2,
                                bitmap.getWidth(),
                                bitmap.getWidth()
                        );
                    }
                    */



                    //String path = saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    img.setImageBitmap(convertColorIntoBlackAndWhiteImage(bitmap));
                    //doBinary();
                    doOCR(convertColorIntoBlackAndWhiteImage(bitmap) );

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");


          /*  if (thumbnail.getWidth() >= thumbnail.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        thumbnail,
                        thumbnail.getWidth()/2 - thumbnail.getHeight()/2,
                        0,
                        thumbnail.getHeight(),
                        thumbnail.getHeight()
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        thumbnail,
                        0,
                        thumbnail.getHeight()/2 - thumbnail.getWidth()/2,
                        thumbnail.getWidth(),
                        thumbnail.getWidth()
                );
            }
            */

            img.setImageBitmap(convertColorIntoBlackAndWhiteImage(thumbnail));
           // doBinary();
           doOCR(convertColorIntoBlackAndWhiteImage(thumbnail) );
            //saveImage(thumbnail);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }









    public void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Please wait...", true);
            // mResult.setVisibility(V.ViewISIBLE);


        }
        else {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            public void run() {

                final String result = manager.startRecognize(bitmap);


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result != null && !result.equals("")) {
                            String s = result.trim();
                            textView.setText(s);






                        }

                        mProgressDialog.dismiss();
                    }

                });

            };
        }).start();


    }
    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */

}