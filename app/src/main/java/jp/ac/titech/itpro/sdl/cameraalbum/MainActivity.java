package jp.ac.titech.itpro.sdl.cameraalbum;


import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import android.location.Geocoder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import jp.ac.titech.itpro.sdl.cameraalbum.adapter.PhotoGridAdapter;
import jp.ac.titech.itpro.sdl.cameraalbum.db.AreaDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.GroupDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.PhotoDatabase;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Area;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Group;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.cameraalbum.util.FileUtil;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.R;

public class MainActivity extends AppCompatActivity {

    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final static int REQ_PERMISSIONS = 1111;

    private final static String TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter<String> adapter;
    private final static int REQ_PHOTO = 1234;
    private File externalPath;
    private List<PhotoData> photoDataList;
    private PhotoGridAdapter photoGridAdapter;

    public static final float groupRange = 15000.0f;

    public static final String EXTRA_DESCRIPTION = "PHOTO_DESCRIPTION";
    public static final String EXTRA_DATE = "PHOTO_DATE";
    public static final String EXTRA_LATITUDE = "PHOTO_LATITUDE";
    public static final String EXTRA_LONGITUDE = "PHOTO_LONGITUDE";

    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        externalPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        initialisePhotoList();

        setContentView(R.layout.activity_main);

        // gpsの許可
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQ_PERMISSIONS);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        FusedLocationProviderClient client = new FusedLocationProviderClient(this);

        photoGridAdapter = new PhotoGridAdapter(getApplicationContext(), photoDataList, externalPath);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(photoGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoData clickedPhoto = photoDataList.get(position);
                Intent intent = new Intent(getApplication(), PhotoActivity.class);
                intent.putExtra(EXTRA_DATE, clickedPhoto.date);
                intent.putExtra(EXTRA_LATITUDE, clickedPhoto.latitude);
                intent.putExtra(EXTRA_LONGITUDE, clickedPhoto.longitude);

                Log.d(TAG, "clicked photo data"+clickedPhoto.date+","+clickedPhoto.latitude+","+clickedPhoto.longitude+","+clickedPhoto.groupID+","+clickedPhoto.isOutSide);
                startActivity(intent);
            }
        });
    }

    public void initialisePhotoList(){
        // TODO 別スレッドで動かす
        PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").allowMainThreadQueries().build();
        photoDataList = photoDB.photoDao().loadAllPhotoData();
    }

    public void takePhoto(View view){
        File photoFilePath = getTempFilePath();

        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                "jp.ac.titech.itpro.sdl.sdlcameraalbum.fileprovider",
                photoFilePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        PackageManager manager = getPackageManager();
        List activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!activities.isEmpty()) {
            startActivityForResult(intent, REQ_PHOTO);
        } else {
            Toast.makeText(MainActivity.this, R.string.toast_no_camera, Toast.LENGTH_LONG).show();
        }
    }

    private File getTempFilePath(){
        File storageDir = externalPath;
        File photoFilePath = new File(storageDir, getString(R.string.temp_photo_file_name));
        return photoFilePath;
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode){
            case REQ_PHOTO:
                if(resCode == RESULT_OK){
                    getLocationAndAddPhotoDataToDataBase();
                }
                break;
        }
    }

    private void getLocationAndAddPhotoDataToDataBase(){

        if(checkPermission()){
            locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null){
                        Toast.makeText(getApplicationContext() , "cannot get location", Toast.LENGTH_LONG).show();
                    }else{
                        try{
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.d(TAG, "location:"+latitude+","+longitude);
                            Geocoder geocoder =  new Geocoder(getApplicationContext(), Locale.ENGLISH);
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            Address address = addresses.get(0);
                            Log.d(TAG, "address:"+address.getCountryName());
                            Log.d(TAG, "address:"+address.getAdminArea());
                            Log.d(TAG, "address:"+address.getLocality());
                            Log.d(TAG, "address:"+address.getPostalCode());

                            String countryName = address.getCountryName();
                            String adminAreaName = address.getAdminArea();
                            String localityName = address.getLocality();

                            if(countryName != null && adminAreaName != null && localityName != null){
                                String areaName = countryName+"+"+adminAreaName+"+"+localityName;
                                addPhoto(latitude, longitude, areaName);
                            }
                        }catch (IOException e){
                            Toast.makeText(getApplicationContext() , "cannot reverse geo coding", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }


    }

    // Todo dbを閉じる
    private void addPhoto(double latitude, double longitude, String areaName){

        Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSZ");
                String takenDate = sdf.format(date);
                String fileName = FileUtil.makePhotoFileName(takenDate);

                FileUtil.renameFile(getTempFilePath(), new File(externalPath, fileName));

                PhotoData photoData = makePhotoData(areaName, latitude, longitude, takenDate);

                PhotoDatabase photoDB = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").build();
                photoDB.photoDao().insertPhoto(photoData);

                Log.d(TAG, "taken photo : "+fileName);
                photoDataList.add(photoData);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        photoGridAdapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

    private PhotoData makePhotoData(String areaName, double latitude, double longitude, String date){

        GroupDatabase groupDB = Room.databaseBuilder(getApplicationContext(), GroupDatabase.class, "groups").build();
        PhotoDatabase photoDatabase = Room.databaseBuilder(getApplicationContext(), PhotoDatabase.class, "photos").build();

        AreaDatabase areaDB = Room.databaseBuilder(getApplicationContext(), AreaDatabase.class, "areas").build();
        List<Area> areaList = areaDB.areaDao().loadAreaFromName(areaName);

        if(areaList.size() == 1){
            Area area = areaList.get(0);
            long areaID = area._id;
            List<Group> groupList = groupDB.groupDao().loadGroupsInSpecificArea(areaID);
            float closestGroupDist = -1.0f;

            // 更新されてなかったらgroupは存在しない
            long closestGroupID = -1;

            // 半径の3倍以内に中心があるグループ
            List<Group> within3TimesRangeGroup = new ArrayList<Group>();

            float[] locationResult = new float[3];

            for(Group group : groupList){
                double centeredLatitude = group.centerLatitude;
                double centeredLongitude = group.centerLongitude;

                // groupの中心と写真を撮った位置の距離を取得
                Location.distanceBetween(centeredLatitude, centeredLongitude, latitude, longitude, locationResult);
                float distanceFromCenter = locationResult[0];

                // 範囲内に入っていた場合
                if(Float.compare(distanceFromCenter, groupRange) <= 0){
                    closestGroupID = group._id;
                    return new PhotoData(date, latitude, longitude, closestGroupID, true);

                }else{
                    if(Float.compare(closestGroupDist, 0.0f) < 0 || Float.compare(distanceFromCenter, closestGroupDist) < 0){
                        closestGroupDist = distanceFromCenter;
                        closestGroupID = group._id;
                    }
                    if (Float.compare(distanceFromCenter, groupRange * 3.0f) <= 0){
                        within3TimesRangeGroup.add(group);
                    }
                }
            }

            // 範囲内に入っていない場合は以下

            // groupがないか, 1番近いgroupが半径の2倍の外の時 -> 新しいgroupを作る
            if(closestGroupID < 0 || Float.compare(closestGroupDist, groupRange * 2.0f) > 0){

                Group newGroup = new Group(areaID, latitude, longitude, FileUtil.makePhotoFileName(date));
                long newGroupID = groupDB.groupDao().insertGroup(newGroup);

                // group範囲外の要素の調査
                if(within3TimesRangeGroup.size() > 0){

                    long[] within3TimesRangeGroupIDArr = new long[within3TimesRangeGroup.size()];

                    List<PhotoData> outSidePhotoList = photoDatabase.photoDao().loadOutSidePhotoDataByGroupID(within3TimesRangeGroupIDArr);
                    List<PhotoData> upDataPhotoList = new ArrayList<>();

                    for (PhotoData photoData : outSidePhotoList){
                        double photoLatitude = photoData.latitude;
                        double photoLongitude = photoData.longitude;

                        Location.distanceBetween(photoLatitude, photoLongitude, latitude, longitude, locationResult);
                        float distance = locationResult[0];

                        // 範囲内に入っていたら所属を変える
                        if(Float.compare(distance, groupRange) <= 0){
                            photoData.groupID = newGroupID;
                            photoData.isOutSide = true;
                            upDataPhotoList.add(photoData);
                        }
                    }

                    if(upDataPhotoList.size() > 0){
                        photoDatabase.photoDao().updateUsers(upDataPhotoList);
                    }
                }
                return new PhotoData(date, latitude, longitude, newGroupID, true);
            }else{ // 一番近いグループが半径の2倍以内の時

                return new PhotoData(date, latitude, longitude, closestGroupID, false);
            }
        }else{ // areaが存在しない場合

            Area newArea = new Area(areaName);
            long newAreaID = areaDB.areaDao().insertArea(newArea);

            Group newGroup = new Group(newAreaID, latitude, longitude, FileUtil.makePhotoFileName(date));
            long newGroupID = groupDB.groupDao().insertGroup(newGroup);

            return new PhotoData(date, latitude, longitude, newGroupID, true);
        }
    }

    private boolean checkPermission(){
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}
