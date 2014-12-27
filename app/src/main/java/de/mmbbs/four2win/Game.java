package de.mmbbs.four2win;

import de.mmbbs.R;
import de.mmbbs.four2win.CustomDialogClass;
import de.mmbbs.four2win.CustomDialogListener;
import de.mmbbs.four2win.CustomDialogType;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.PlayGameListener;
import de.mmbbs.gameserver.ui.Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

//kommentar 2
public class Game extends GameManagementActivity implements GameListener, PlayGameListener {


    private static Handler handler;
	Leinwand l;
	private de.mmbbs.four2win.CustomDialogClass cd;
	//private InputPlayerDialog ld;
	//private SharedPreferences pref;
    private String gegner;
    private  boolean firstTurn;

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.gamemenu, menu);
		 return super.onCreateOptionsMenu(menu);
	}

	 
	@Override
	protected void onStop() {
		super.onStop();
		l.onStop();
		l.exit();
        gc.quitPaaring();
        gegner=null;	}

    @Override
    public void onLogin() {

    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			Log.d(Main.TAG,"Help!!");
			return true;
		case R.id.back:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.d(Main.TAG," **** Game onCreate()");
		//setContentView(R.layout.main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_four2win);
        handler = new Handler();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            gegner= extras.getString("gegner");
            firstTurn=extras.getBoolean("start");
        } else {
            gegner= (String) savedInstanceState.getSerializable("gegner");
            firstTurn= Boolean.parseBoolean((String) savedInstanceState.getSerializable("start"));
        }
        Log.d(Main.TAG,"Game Four2Win onCreate() start="+firstTurn+" gegner="+gegner);
        l=(Leinwand) this.findViewById(R.id.gui);
        l.setGameListener(this);
        l.reset(this.getWindowManager().getDefaultDisplay().getWidth(),this.getWindowManager().getDefaultDisplay().getHeight());
        l.player1.setName(gc.getUser());
        l.player2.setName(gegner);
        if (firstTurn) {
            this.setLeftPlayer(l.player1);
            this.setRightPlayer(l.player2);
        }
        else {
            this.setLeftPlayer(l.player2);
            this.setRightPlayer(l.player1);

        }
        l.setStart(firstTurn);
	}


	@Override
	protected void onStart() {
        gc = (de.mmbbs.gameserver.GameServerApplication) getApplication();
        gc.setGameCallbacks(this, handler);
		super.onStart();
		Log.d(Main.TAG," **** Game onStart()");

//		String p1=null,p2=null;
//		pref = PreferenceManager.getDefaultSharedPreferences(this);
//		p1 = pref.getString("player1", "Player1");
//		p2 = pref.getString("player2", "Player2");

		
	}


	public void showDialog(String msg) {

		cd = new CustomDialogClass(this,CustomDialogType.INFO ,msg,
				this.getResources().getString(R.string.ok),null);
		cd.setOnCustomDialog(new CustomDialogListener() {


			@Override
			public void onNegativeButton() {

			}

			@Override
			public void onPositiveButton() {
				onBackPressed();
				
			}
			
		});
		cd.setCancelable(false);
		cd.show();
		

	}


    private void setLeftPlayer(Player p) {
        TextView tv = (TextView) this.findViewById(R.id.four2winTextViewplayerLeft);
        tv.setText(p.getName());
    }




    private void setRightPlayer(Player p) {
        TextView tv = (TextView) this.findViewById(R.id.four2winTextViewplayerRight);
        tv.setText(p.getName());
    }


    @Override
    public void setScore(int left, int right) {
        ((TextView) this.findViewById(R.id.four2winTextViewScoreLeft)).setText(Integer.toString(left));
        ((TextView) this.findViewById(R.id.four2winTextViewScoreRight)).setText(Integer.toString(right));
    }



    @Override
    public void setProgessBar(int left, int right) {
        ProgressBar pbl = (ProgressBar) this.findViewById(R.id.four2winProgressBarleft);
        ProgressBar pbr = (ProgressBar) this.findViewById(R.id.four2winProgressBarright);
        pbl.setProgress(left);
        pbr.setProgress(right);
    }


    @Override
    public void won(Player currentPlayer) {
        // TODO hier noch implementieren
    }

    @Override
    public void turn(int xi) {
        JSONObject data = new JSONObject();
        try {
            data.put("xi", xi);
            gc.play("turn",data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePlay(JSONObject obj) {
        // TODO Hier muss der neue Zug verarbeitet werden
        if (obj.optString("command").compareTo("turn")==0) {
            Log.d(Main.TAG," turn commando auf "+obj.optInt("xi"));
            l.placeStone(obj.optInt("xi"));
        }
    }

    public static Handler getHandler() {

		return handler;
	}


//	@Override
//	public void onClick(View v, LinearLayout mainlayout) {
//		EditText et = (EditText) mainlayout.findViewById(R.id.editText_player1);
//		//Log.d(Main.TAG,"l="+l+" player1="+l.player1.toString()+" et="+et);
//		l.player1.setName(et.getText().toString());
//		et = (EditText) mainlayout.findViewById(R.id.editText_player2);
//		l.player2.setName(et.getText().toString());
//		//e.putString("player2", et.getText().toString());
//		//e.commit();
//		TextView tv = (TextView) this.findViewById(R.id.four2winTextViewplayerLeft);
//		tv.setText(l.getPlayer(1).getName());
//		tv = (TextView) this.findViewById(R.id.four2winTextViewplayerRight);
//		tv.setText(l.getPlayer(2).getName());
//		l.setState(Leinwand.PLAY);
//
//
//	}


    @Override
    public void updateChat(JSONObject obj) {

    }


    @Override
    public void updateDisconnect() {
        Log.d(Main.TAG,"updateDiscopnnect() Game state="+gc.getState());
        this.showDialog(getResources().getString(de.mmbbs.R.string.player_disconnected));
        gc.stats(1, 1, 0);
        gc.addScore(l.getScore());

    }
}