package de.andreaskrienke.android.outlinecalculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CropImageView mCropImageView;
    private Toolbar toolbar;

    private Button btnLegend;
    private Button btnArea;
    private Button btnRotate;
    private Button btnClip;

    private EditText editLegend;
    private EditText editArea;

    private TextView txtLegendResult;
    private TextView txtAreaResult;

    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 1;

    final int RequestPermissionCode = 1;

    private float factor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestRuntimePermission();
        }
    }

    private void initUI() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        mCropImageView = (CropImageView)findViewById(R.id.cropImageView);

        editLegend = (EditText)findViewById(R.id.edit_legend);
        editArea = (EditText)findViewById(R.id.edit_area);

        txtLegendResult = (TextView)findViewById(R.id.txt_legend_result);
        txtAreaResult = (TextView)findViewById(R.id.txt_area_result);

        btnLegend = (Button)findViewById(R.id.btn_legend);
        btnLegend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editLegend.getText().toString().trim().length() <= 0) {
                    Toast.makeText(view.getContext(), "Please enter legend distance", Toast.LENGTH_SHORT).show();
                    return;
                }
                int legend = Integer.valueOf(editLegend.getText().toString());

                RectF rectLegend = mCropImageView.getActualCropRect();
                factor = legend / rectLegend.width();

                // Set Calculated Legend Result
                txtLegendResult.setText("=> Pixel Factor: " +  factor);
            }
        });

        btnArea = (Button)findViewById(R.id.btn_area);
        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RectF rectArea = mCropImageView.getActualCropRect();
                // Calculate Area
                float area = (rectArea.width() * factor) *
                        (rectArea.height() * factor);

                // Set Calculated Area Result
                editArea.setText(String.valueOf(area));

                /*
                // Set Area Calculations
                txtAreaResult.setText("=> (Width: " + (rectArea.width() * factor) + " (" + rectArea.width() + " dp)" +
                        " x " +
                        "Height: " + (rectArea.height() * factor) + " (" + rectArea.height() + " dp) )");
                */
            }
        });

    }

    private void requestRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA))
            Toast.makeText(this,"CAMERA permission allows us to access CAMERA app",Toast.LENGTH_SHORT).show();
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case RequestPermissionCode:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Canceled",Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.btn_camera:
                takeImage();
                break;

            case R.id.btn_gallery:
                chooseImageFromGallery();
                break;

            case R.id.btn_rotate:
                mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;

            case R.id.btn_clip:
                mCropImageView.setImageBitmap(mCropImageView.getCroppedBitmap());
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap imageBitmap;

        if ((requestCode == REQUEST_IMAGE_CAPTURE) &&
                (resultCode == RESULT_OK)) {

            //galleryAddPic();

            if (data == null) {
                imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, null);
                mCropImageView.setImageBitmap(imageBitmap);
            }
            else {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            }
            mCropImageView.setImageBitmap(imageBitmap);
        }
        else if ((requestCode == REQUEST_IMAGE_FROM_GALLERY) &&
                (resultCode == RESULT_OK)) {
            if (data != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    imageBitmap = BitmapFactory.decodeStream(is);
                    mCropImageView.setImageBitmap(imageBitmap);
                    mCurrentPhotoPath = data.getData().toString();

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void takeImage() {
        // TODO Use the devices camera app to take a photo
        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takeImageIntent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            }
            catch (IOException ioe) {

            }

            if (photo != null) {
                takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            Log.i(MainActivity.class.getName(),
                    "Directory creation was needed and was successfull: " + storageDir.mkdirs());
        }
        File imageFile = new File(storageDir, imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void chooseImageFromGallery() {
        // TODO let the user choose an image from the gallery
        Intent chooseImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseImageIntent.setType("image/*");

        if (chooseImageIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(chooseImageIntent, REQUEST_IMAGE_FROM_GALLERY);

        }
    }
}
