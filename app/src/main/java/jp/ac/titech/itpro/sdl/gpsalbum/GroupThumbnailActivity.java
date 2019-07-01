package jp.ac.titech.itpro.sdl.gpsalbum;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.itpro.sdl.gpsalbum.adapter.GroupThumbnailAdapter;
import jp.ac.titech.itpro.sdl.gpsalbum.constantval.ExtraString;
import jp.ac.titech.itpro.sdl.gpsalbum.db.GroupDatabase;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Group;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.PhotoData;

public class GroupThumbnailActivity extends MainGridActivity {

    private final static String TAG = GroupThumbnailActivity.class.getSimpleName();

    private static final int REQ_GROUP_ALBUM = 3333;


    private List<Group> groupList;
    private GroupThumbnailAdapter groupThumbnailAdapter;

    @Override
    protected void doAdditionalInitialization(){

        groupList = new ArrayList<>();
        groupThumbnailAdapter = new GroupThumbnailAdapter(getApplicationContext(), groupList, externalPath);

        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(groupThumbnailAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Group clickedGroup = groupList.get(position);
                Log.d(TAG, "group id "+clickedGroup._id);
                Intent intent = new Intent(getApplication(), GroupAlbumActivity.class);
                intent.putExtra(ExtraString.EXTRA_GROUP_ID, clickedGroup._id);
                intent.putExtra(ExtraString.EXTRA_GROUP_LIST_ID, position);

                startActivityForResult(intent, REQ_GROUP_ALBUM);
            }
        });

        initializeGroupList();
    }

    @Override
    protected void unvisibleSpecificMenuItem(Menu menu){
        MenuItem unvisibleItem = menu.findItem(R.id.go_group_activity);
        unvisibleItem.setVisible(false);
    }

    @Override
    protected void goAllPhotoActivity(){
        jampAnotherActivity();
    }

    @Override
    protected  void goGropThumbnailActivity(){

    }

    private void initializeGroupList(){
        Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                GroupDatabase groupDB = Room.databaseBuilder(getApplicationContext(), GroupDatabase.class, "groups").build();
                groupList.addAll(groupDB.groupDao().loadAllGroup());
                groupDB.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        groupThumbnailAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void goToAnotherActivity(){
        Intent intent = new Intent(getApplication(), AllPhotoAlbumActivity.class);
        startActivity(intent);
    }

    @Override
    protected void notifyStorePhotoDataToUIThread(PhotoData photoData, Group group){

        Toast.makeText(getApplicationContext(), "new photo is registered in group"+photoData.groupID, Toast.LENGTH_LONG).show();

        if(group == null){
            return;
        }

        groupList.add(group);
        groupThumbnailAdapter.notifyDataSetChanged();
    }

    @Override
    protected void otherActionForActivityResult(int reqCode, int resCode, Intent data){

        switch (reqCode){
            case REQ_GROUP_ALBUM:
                if(resCode == RESULT_OK){
                    int listIndex = data.getIntExtra(ExtraString.EXTRA_DELETE_GROUP_LIST_ID, -1);
                    if(listIndex < 0){
                        return;
                    }
                    groupList.remove(listIndex);
                    groupThumbnailAdapter.notifyDataSetChanged();
                }else if(resCode == RESULT_CANCELED){
                    if(data != null){
                        boolean isThumbnailChanged = data.getBooleanExtra(ExtraString.EXTRA_IS_THUMBNAIL_CHANGED, false);
                        if(isThumbnailChanged){
                            int groupListIndex = data.getIntExtra(ExtraString.EXTRA_GROUP_LIST_ID, -1);
                            String newThumbnailFileName = data.getStringExtra(ExtraString.EXTRA_NEW_THUMBNAIL_NAME);
                            Log.d(TAG, "new thumbnail " + isThumbnailChanged+","+groupListIndex+","+newThumbnailFileName);

                            groupList.get(groupListIndex).thumbnailName = newThumbnailFileName;
                            groupThumbnailAdapter.notifyDataSetChanged();
                        }
                    }
                }
        }
    }

}
