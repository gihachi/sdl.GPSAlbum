package jp.ac.titech.itpro.sdl.cameraalbum.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.ac.titech.itpro.sdl.cameraalbum.R;

public class MapDisplaytDialog extends DialogFragment {

    public interface MapDialogListener{
        public void onArialMapSelected(DialogFragment dialog);
        public void onPanoramaSelected(DialogFragment dialog);
    }

    MapDialogListener mListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        // @see https://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
        Activity activity = getActivity();
        try{
            mListener = (MapDialogListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(" cannot cast ");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] mapTypeArr = {"Panorama", "Arial Map"};

        builder.setTitle("choose display map")
                .setItems(R.array.map_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            mListener.onPanoramaSelected(MapDisplaytDialog.this);
                        }else if(which == 1){
                            mListener.onArialMapSelected(MapDisplaytDialog.this);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

}
