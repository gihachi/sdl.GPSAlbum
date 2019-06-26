package jp.ac.titech.itpro.sdl.cameraalbum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.R;
import jp.ac.titech.itpro.sdl.cameraalbum.util.FileUtil;

public class PhotoActivity extends AppCompatActivity {

    private final static String TAG = PhotoActivity.class.getSimpleName();
    private File externalPath;
    private String date;
    private String description;
    private double latitude;
    private double longitude;
    private File photoFile;

    @Override
    protected void onCreate(Bundle saveInstanceState){

        super.onCreate(saveInstanceState);
        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        description = intent.getStringExtra(MainActivity.EXTRA_DESCRIPTION);
        date = intent.getStringExtra(MainActivity.EXTRA_DATE);
        latitude = intent.getDoubleExtra(MainActivity.EXTRA_LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(MainActivity.EXTRA_LONGITUDE,0.0);

        photoFile = new File(externalPath, FileUtil.makePhotoFileName(date));

        showDisplay();
    }

    private void showDisplay(){
        ImageView imageView = findViewById(R.id.photo_view);
        Picasso.with(getApplicationContext()).load(photoFile).into(imageView);
    }

    public void displayArialMap(View view){

        Uri uri = Uri.parse("https://www.google.com/maps/@?api=1&map_action=map&center="+latitude+","+longitude+"&zoom=50&basemap=satellite");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void displayPanorama(View view){

        Uri uri = Uri.parse("https://www.google.com/maps/@?api=1&map_action=pano&viewpoint="+latitude+","+longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
