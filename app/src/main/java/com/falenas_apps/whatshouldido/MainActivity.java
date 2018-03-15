package com.falenas_apps.whatshouldido;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.falenas_apps.whatshouldido.Data.ActionContract;
import com.falenas_apps.whatshouldido.Data.ActionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.id.input;
import static android.R.id.switch_widget;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String[] projection = {ActionContract.ActionEntry._ID,ActionContract.ActionEntry.ACTION, ActionContract.ActionEntry.WEIGHT};
    //List of all actions the user has entered
    ArrayList<String> actionList = new ArrayList<>();
    //The weight each action should be given, default is all weighted equally
    ArrayList<Integer> weightList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();
    Random rand = new Random();
    TextView actionView;
    ConstraintLayout mDrawerPane;
    ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    LoaderManager lm = getSupportLoaderManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title);
        lm.initLoader(0, null, this);
        actionView = (TextView) findViewById(R.id.mainAction);

        Button newAction = (Button) findViewById(R.id.different_action);
        //Lets the user refesh the page to pick a different aciton
        newAction.setOnClickListener(new View.OnClickListener() {

            //When user clicks this button, page refreshes
            @Override
            public void onClick(View view) {
                choseNewAction();
            }
        });

        Button addAction = (Button) findViewById(R.id.create_new_action);
        //Lets the user add a new action
        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
                builder.setTitle(R.string.new_action);
                builder.setCancelable(true);
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                builder.setView(input);
                builder.setPositiveButton(R.string.add, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String action = input.getText().toString();

                        insertAction(action);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }

                });
                builder.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog actionEnter = builder.create();
                actionEnter.show();
            }

        });


        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (ConstraintLayout) findViewById(R.id.drawer_pane);
        mDrawerList = (ListView) findViewById(R.id.activity_list);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        if (mDrawerToggle.onOptionsItemSelected(menuItem)){
            return true;
        }
        //handles an option being selected
        int id = menuItem.getItemId();
        switch (id){
            case R.id.reviewActions:
                //Start Fragment
                break;
        }
        return true;
    }

    public void deleteAction(int id,Context context){

        //deletes action with passed position.  Position is id for this data
        try {
            Uri uri = ContentUris.withAppendedId(ActionContract.CONTENT_URI, id);
            context.getContentResolver().delete(uri, null, null);


        }catch (IllegalArgumentException e){
            Toast.makeText(context,getString(R.string.failure),Toast.LENGTH_LONG).show();
        }
    }

    private void insertAction(String action){

        //lets the user enter a new action, then displays a toast saying successful add
        ContentValues values = new ContentValues();
        values.put(ActionContract.ActionEntry.ACTION,action);
        try {
            getContentResolver().insert(ActionContract.CONTENT_URI,values);
            Toast.makeText(this,getString(R.string.action_added)+action,Toast.LENGTH_LONG).show();
         //   lm.initLoader(0,null,null);

        }
        catch(IllegalArgumentException e){
            Toast.makeText(this,R.string.failure,Toast.LENGTH_LONG).show();
        }
    }


    private void choseNewAction() {

        //Sets the view to have a new random action from the list
        if (actionList.size() > 0) {
            String newAction = actionList.get(rand.nextInt(actionList.size()));
            //makes sure new action is not the same as old action. Chose new action until valid
            if (actionList.size() > 1) {
                String oldAction = getCurrentAction(actionView);
                while (oldAction.equals(newAction)) {
                    newAction = actionList.get(rand.nextInt(actionList.size()));
                }
            }
            actionView.setText(newAction);
        }
    }

    private String getCurrentAction(TextView v){
        return v.getText().toString();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //creates query for loader
        return new CursorLoader(this, ActionContract.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        actionList.clear();
        weightList.clear();
        idList.clear();

        if (cursor!= null && cursor.getCount() >0 ){

            //creates a list of all possible actions
            while(cursor.moveToNext()){
                actionList.add(cursor.getString(cursor.getColumnIndex(ActionContract.ActionEntry.ACTION)));
                weightList.add(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ActionContract.ActionEntry.WEIGHT))));
                idList.add(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ActionContract.ActionEntry._ID))));
            }
            //randomly selections an action
            choseNewAction();
        }
        else{

            //sets the text to tell user to create an action
            actionView.setText(R.string.create_action);

        }
        DrawerListAdapter drawerListAdapter = new DrawerListAdapter(this, actionList,idList);
        mDrawerList.setAdapter(drawerListAdapter);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}

class DrawerListAdapter extends BaseAdapter{
    Context mContext;
    ArrayList <String> mActionList;
    ArrayList <Integer> mIdList;

    public DrawerListAdapter(Context context,ArrayList<String> actionList,ArrayList<Integer> idList){
        mContext = context;
        mActionList = actionList;
        mIdList = idList;

    }

    @Override
    public int getCount() {
        return mActionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mActionList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;



        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item,null);
        }
        else{
            view = convertView;
        }
        ImageView deleteActionButton = (ImageView) view.findViewById(R.id.cancelAction);
        deleteActionButton.setOnClickListener(new View.OnClickListener() {

            //gets the position of the item in the listview, then sends that off to be deleted
            @Override
            public void onClick(View view) {
                String action = mActionList.get(position);
                ListView parentView = (ListView) view.getParent().getParent();
                int position = parentView.getPositionForView((ConstraintLayout) view.getParent());
                MainActivity mainActivity = new MainActivity();
                mainActivity.deleteAction(mIdList.get(position),mContext);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.delete)+action,Toast.LENGTH_LONG).show();
            }
        });

        TextView textView = (TextView) view.findViewById(R.id.actionTitle);
        textView.setText(mActionList.get(position));
        return view;
    }


}
