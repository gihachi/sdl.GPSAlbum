package jp.ac.titech.itpro.sdl.gpsalbum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import jp.ac.titech.itpro.sdl.gpsalbum.constantval.ExtraString;
import jp.ac.titech.itpro.sdl.gpsalbum.dialog.DeletePhotoDialog;
import jp.ac.titech.itpro.sdl.gpsalbum.dialog.MapDisplaytDialog;
import jp.ac.titech.itpro.sdl.gpsalbum.util.FileUtil;

public class PhotoActivity extends AppCompatActivity implements MapDisplaytDialog.MapDialogListener, DeletePhotoDialog.DeletePhotoListener {

    private final static String TAG = PhotoActivity.class.getSimpleName();
    private File externalPath;
    private String date;
    private double latitude;
    private double longitude;
    private File photoFile;
    private int listIndex;

    @Override
    protected void onCreate(Bundle saveInstanceState){

        super.onCreate(saveInstanceState);
        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        date = intent.getStringExtra(ExtraString.EXTRA_DATE);
        latitude = intent.getDoubleExtra(ExtraString.EXTRA_LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(ExtraString.EXTRA_LONGITUDE,0.0);

        listIndex = intent.getIntExtra(ExtraString.EXTRA_LIST_INDEX, -1);
        if(listIndex < 0){
            throw new Error("you have to set list index");
        }

        photoFile = new File(externalPath, FileUtil.makePhotoFileName(date));

        showDisplay();
    }

    private void showDisplay(){
        ImageView imageView = findViewById(R.id.photo_view);
        Picasso.with(getApplicationContext()).load(photoFile).into(imageView);
    }

    @Override
    public void onArialMapSelected(DialogFragment dialog){
        displayArialMap();
    }

    @Override
    public void onPanoramaSelected(DialogFragment dialog){
        displayPanorama();
    }

    @Override
    public void onDeletePositiveClick(DialogFragment dialog){
        deletePhoto();
    }

    public void displaylMap(View view){
        DialogFragment mapDialog = new MapDisplaytDialog();
        mapDialog.show(getSupportFragmentManager(), "display map");
    }

    public void displayArialMap(){

        Uri uri = Uri.parse("https://www.google.com/maps/@?api=1&map_action=map&center="+latitude+","+longitude+"&zoom=50&basemap=satellite");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void displayPanorama(){

        Uri uri = Uri.parse("https://www.google.com/maps/@?api=1&map_action=pano&viewpoint="+latitude+","+longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onClickDeletePhotoButton(View view){
        DialogFragment deletePhotoDialog = new DeletePhotoDialog();
        deletePhotoDialog.show(getSupportFragmentManager(), "delete photo");
    }

    public void deletePhoto(){

        Intent intent = new Intent();
        intent.putExtra(ExtraString.EXTRA_DELETE_INDEX, listIndex);
        setResult(RESULT_OK, intent);
        finish();
    }
}
