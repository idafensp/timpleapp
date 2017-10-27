package com.tamarawa.timpleplayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tamarawa.timpleplayer.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
@SuppressLint("NewApi")
public class Player extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
 
    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
 
    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    
    private static final int SWIPE_MIN_DISTANCE = 20;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    
    
    

    private static boolean CALCULATE_COORDINATES = false;
    

    private static boolean PLAY_S1 = true;
    private static boolean PLAY_S2 = true;
    private static boolean PLAY_S3 = true;
    private static boolean PLAY_S4 = true;
    private static boolean PLAY_S5 = true;

    
    private LinearLayout s1;
	private LinearLayout s2;
	private LinearLayout s3;
	private LinearLayout s4;
	private LinearLayout s5;
    
    private static int s1l = -1;
    private static int s1r = -1;
    private static int s2l = -1;
    private static int s2r = -1;
    private static int s3l = -1;
    private static int s3r = -1;
    private static int s4l = -1;
    private static int s4r = -1;
    private static int s5l = -1;
    private static int s5r = -1;
    
    private static SoundPool soundPool;

    private static final int NUM_FRETS = 5;

	private static final int NUM_OF_BUTTONS = 7;

	private static final int BASE_COLOR = 0xFFC0C0C0;

	private static final int SELECTED_COLOR = 0xFF00FF00;

	private static final String SOUND_DS = "AZOFRA";
	private static final String SOUND_FARINA_A = "FARINA_A";

	private static final String TAG = "TP-PL";

	
	private int[] s1ids;
	private int[] s2ids;
	private int[] s3ids;
	private int[] s4ids;
	private int[] s5ids;
	
	private static int[] currentChord;
	
	private static Button bchord1;
	private static Button bchord2;
	private static Button bchord3;
	private static Button bchord4;
	private static Button bchord5;
	private static Button bchord6;
	private static Button bchord7;

	private float volume;
	
	private final String CHORD_ERR_MASSAGE = "Attempted to load an invalid chord:";


	Map<String,int[]> chordLibrary;
	Map<String,String> chordLabelTranslator;
	Map<String,String> chordLabelTranslatorBack;

	

	private Animation shake;

	private AudioManager audioManager;

	private String NOT_LATINA_STRING ="1";

	private SharedPreferences sharedPref;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
        setContentView(R.layout.activity_player);
		//avoid orientation change
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		int screenWidth = myGetWidth(this);
		int buttonSize = (int) screenWidth / NUM_OF_BUTTONS;
		
		Log.d(TAG,"ScreenW="+screenWidth+"->"+buttonSize);

		bchord1 = (Button) findViewById(R.id.bchord1);
		bchord2 = (Button) findViewById(R.id.bchord2);
		bchord3 = (Button) findViewById(R.id.bchord3);
		bchord4 = (Button) findViewById(R.id.bchord4);
		bchord5 = (Button) findViewById(R.id.bchord5);
		bchord6 = (Button) findViewById(R.id.bchord6);
		bchord7 = (Button) findViewById(R.id.bchord7);
		

		Log.d(TAG,"ScreenW2="+screenWidth+"->"+buttonSize);
		
		TableRow.LayoutParams params = (TableRow.LayoutParams) bchord1.getLayoutParams();
		params.width = buttonSize;
		params.height = (int)((1.3)*buttonSize);
		bchord1.setLayoutParams(params);
		bchord2.setLayoutParams(params);
		bchord3.setLayoutParams(params);
		bchord4.setLayoutParams(params);
		bchord5.setLayoutParams(params);
		bchord6.setLayoutParams(params);
		bchord7.setLayoutParams(params);
		
		
		
		currentChord = new int[5];

		clearChordButtons(null);
		loadFretSounds();
		
		
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		loadChordLibrary();
		
		//get max volume
//		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		
		

		int s = chordLibrary.size();
		final CharSequence[] items = new CharSequence[s];
		final CharSequence[] itemsAnglo = new CharSequence[s];
		final CharSequence[] itemsLat = new CharSequence[s];
		Iterator<String> it = chordLibrary.keySet().iterator();
		int i = 0;
		while(it.hasNext())
		{
			String c = it.next();
			itemsAnglo[i]=c;
			itemsLat[i]=chordLabelTranslator.get(c);
			i++;
		}
		
		if(getNotacion())
		{
			copyArray(items,itemsLat,s);
		}
		else
		{
			copyArray(items,itemsAnglo,s);
		}
		
		loadDefaultChords(items);
		
		
		OnLongClickListener ltl = new OnLongClickListener()
		{
			Button  b;
			@Override
			public boolean onLongClick(View v) {
				b = (Button) v;
				Log.d(TAG,"long click on " + b.getText() + " with id " + b.getId());
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Player.this);
		        builder.setTitle("Escoge acorde:");
		        builder.setItems(items, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		                // Do something with the selection
		            	String pre = (String) b.getText();
		                
		            	b.setText(items[item]);
		            	
		            	//TODO [STORE_CHORD_PREF] add here code for storing the new value of the button on a preference
		            	
		            	//Log.d(TAG,"Changed from " + pre + " to " + b.getText() +"("+ chordLibrary.get(b.getText())[0]+chordLibrary.get(b.getText())[1]+chordLibrary.get(b.getText())[2]+chordLibrary.get(b.getText())[3]+chordLibrary.get(b.getText())[4]+")");
		                clearChordButtons(null);

		                
		        		//no chord
		        		currentChord[0] = 0;
		        		currentChord[1] = 0;
		        		currentChord[2] = 0;
		        		currentChord[3] = 0;
		        		currentChord[4] = 0;
		        		
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();
		        
				return false;
			}
			
		};
		bchord1.setOnLongClickListener(ltl);
		bchord2.setOnLongClickListener(ltl);
		bchord3.setOnLongClickListener(ltl);
		bchord4.setOnLongClickListener(ltl);
		bchord5.setOnLongClickListener(ltl);
		bchord6.setOnLongClickListener(ltl);
		bchord7.setOnLongClickListener(ltl);
		
		
		
	    shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		
	    
		
		LinearLayout screen = (LinearLayout) findViewById(R.id.stringsLayout);
		screen.setOnTouchListener(new OnTouchListener() {
            

			@Override
            public boolean onTouch(final View view, final MotionEvent event) 
            {
            	if(!CALCULATE_COORDINATES)
            	{
            		
            		CALCULATE_COORDINATES = true;
            		

             		s1l = s1.getLeft();
             		s1r = s1.getRight();
             		s2l = s2.getLeft();
             		s2r = s2.getRight();
             		s3l = s3.getLeft();
             		s3r = s3.getRight();
             		s4l = s4.getLeft();
             		s4r = s4.getRight();
             		s5l = s5.getLeft();
             		s5r = s5.getRight();
             		

             		Log.d(TAG,"s1="+s1l+"-"+s1r);
             		Log.d(TAG,"s2="+s2l+"-"+s2r);
             		Log.d(TAG,"s3="+s3l+"-"+s3r);
             		Log.d(TAG,"s4="+s4l+"-"+s4r);
             		Log.d(TAG,"s5="+s5l+"-"+s5r);
            	}
               

                //Log.d(TAG,"onTouch" + event.getAction());
                if(event.getAction()==2)
                {
                	float x = event.getX();
                	if((s1l<x)&&(s1r>x))
                	{
                		playString1();
                		
                	} else if ((s2l<x)&&(s2r>x)){

                		playString2();
                		
                	} else if ((s3l<x)&&(s3r>x)){

                		playString3();
                		
                	} else if ((s4l<x)&&(s4r>x)) {

                		playString4();
                		
                	} else if ((s5l<x)&&(s5r>x)) {

                		playString5();
                		
                	}                	
                }
                if((event.getAction()==0)||(event.getAction()==1))
                {
                	PLAY_S1 = true;
                	PLAY_S2 = true;
					PLAY_S3 = true;
					PLAY_S4 = true;
					PLAY_S5 = true;
                }
                return true;
            }

			private void playString5() {
				if(PLAY_S5)
				{
					PLAY_S5=false;
					Log.d(TAG,"------------>Playing String 5|"+currentChord[4]);
					//mpString5.start();
					

					soundPool.play(s5ids[currentChord[4]], volume, volume, 1, 0, 1f);
					s5.startAnimation(shake);
					
					PLAY_S2 = true;
					PLAY_S3 = true;
					PLAY_S4 = true;
					PLAY_S1 = true;
				}
				
			}

			private void playString4() {
				if(PLAY_S4)
				{
					PLAY_S4=false;
					Log.d(TAG,"------------>Playing String 4|"+currentChord[3]);
					//mpString4.start();
					

					soundPool.play(s4ids[currentChord[3]], volume, volume, 1, 0, 1f);
					s4.startAnimation(shake);
					
					PLAY_S2 = true;
					PLAY_S3 = true;
					PLAY_S1 = true;
					PLAY_S5 = true;
				}
				
			}

			private void playString3() {
				if(PLAY_S3)
				{
					PLAY_S3=false;
					Log.d(TAG,"------------>Playing String 3|"+currentChord[2]);
					//mpString3.start();
					
					soundPool.play(s3ids[currentChord[2]], volume, volume, 1, 0, 1f);
					s3.startAnimation(shake);
					
					
					PLAY_S2 = true;
					PLAY_S1 = true;
					PLAY_S4 = true;
					PLAY_S5 = true;
				}
				
			}

			private void playString2() {
				if(PLAY_S2)
				{
					PLAY_S2=false;
					Log.d(TAG,"------------>Playing String 2|"+currentChord[1]);
					//mpString2.start();
					

					soundPool.play(s2ids[currentChord[1]], volume, volume, 1, 0, 1f);
					s2.startAnimation(shake);
					
					PLAY_S1 = true;
					PLAY_S3 = true;
					PLAY_S4 = true;
					PLAY_S5 = true;
				}
				
			}

			private void playString1() {
				if(PLAY_S1)
				{
					PLAY_S1=false;
					Log.d(TAG,"------------>Playing String 1|"+currentChord[0]);
					//mpString1.start();
					

					soundPool.play(s1ids[currentChord[0]], volume, volume, 1, 0, 1f);
					s1.startAnimation(shake);
					
					PLAY_S2 = true;
					PLAY_S3 = true;
					PLAY_S4 = true;
					PLAY_S5 = true;
				}
				
			}
        });
		
		s1 = (LinearLayout) findViewById(R.id.string1);
		s2 = (LinearLayout) findViewById(R.id.string2);
		s3 = (LinearLayout) findViewById(R.id.string3);
		s4 = (LinearLayout) findViewById(R.id.string4);
		s5 = (LinearLayout) findViewById(R.id.string5);

		
		String timpleSound = sharedPref.getString(Settings.TIMPLE_SOUND_KEY, "FALLO_AL_LEER_PREFS");
		

		setBackground(R.drawable.backtry3);
		
		
		
		
		

        
    }

    private void loadDefaultChords(CharSequence[] items) 
    {

    	
    	
    	//TODO [STORE_CHORD_PREF] modify so the default chords are retrieved from the preference list
		if(getNotacion())
		{
	    	bchord1.setText("Do");
	    	bchord2.setText("Re");
	    	bchord3.setText("Mi");
	    	bchord4.setText("Fa");
	    	bchord5.setText("Sol");
	    	bchord6.setText("La");
	    	bchord7.setText("Si");
		}
		else
		{
	    	bchord1.setText("C");
	    	bchord2.setText("D");
	    	bchord3.setText("E");
	    	bchord4.setText("F");
	    	bchord5.setText("G");
	    	bchord6.setText("A");
	    	bchord7.setText("B");
		}
		
		
	}

	private void copyArray(CharSequence[] items, CharSequence[] itemsSource, int size) {
		for(int i=0; i<size;i++)
		{
			items[i]=itemsSource[i];
		}
		
	}

	private boolean getNotacion() {

		String not = sharedPref.getString(Settings.NOTACION_KEY, "FALLO_AL_LEER_PREFS");

		boolean res = false;
		Log.d(TAG,"PREFERENCES Notaci—n: " + not);
		if(not.equals(NOT_LATINA_STRING ))
		{
			res = true;	
		}
		return res;
	}

	private void loadFretSounds() 
    {
    	soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);

		s1ids = new int[NUM_FRETS];
		s2ids = new int[NUM_FRETS];
		s3ids = new int[NUM_FRETS];
		s4ids = new int[NUM_FRETS];
		s5ids = new int[NUM_FRETS];
		
		//no chord
		currentChord[0] = 0;
		currentChord[1] = 0;
		currentChord[2] = 0;
		currentChord[3] = 0;
		currentChord[4] = 0;
		

		String timpleSound = sharedPref.getString(Settings.TIMPLE_SOUND_KEY, "FALLO_AL_LEER_PREFS");

		Log.d(TAG,"timpleSound="+timpleSound);
		
		
		if(timpleSound.equals(SOUND_DS))
		{
			loadDSSounds();
			return;
		}
		
		if(timpleSound.equals(SOUND_FARINA_A))
		{
			loadFarinaASounds();
			return;
		}
		
		
		//default values
		loadDSSounds();

		
	}
	

	
	private void loadFarinaASounds() 
	{
		s1ids[0] = soundPool.load(this, R.raw.a10, 1);
		s2ids[0] = soundPool.load(this, R.raw.a20, 1);
		s3ids[0] = soundPool.load(this, R.raw.a30, 1);
		s4ids[0] = soundPool.load(this, R.raw.a40, 1);
		s5ids[0] = soundPool.load(this, R.raw.a50, 1);
		

		//fret 1
		s1ids[1] = soundPool.load(this, R.raw.a11, 1);
		s2ids[1] = soundPool.load(this, R.raw.a21, 1);
		s3ids[1] = soundPool.load(this, R.raw.a31, 1);
		s4ids[1] = soundPool.load(this, R.raw.a41, 1);
		s5ids[1] = soundPool.load(this, R.raw.a51, 1);
		

		//fret 2
		s1ids[2] = soundPool.load(this, R.raw.a12, 1);
		s2ids[2] = soundPool.load(this, R.raw.a22, 1);
		s3ids[2] = soundPool.load(this, R.raw.a32, 1);
		s4ids[2] = soundPool.load(this, R.raw.a42, 1);
		s5ids[2] = soundPool.load(this, R.raw.a52, 1);
		

		//fret 3
		s1ids[3] = soundPool.load(this, R.raw.a13, 1);
		s2ids[3] = soundPool.load(this, R.raw.a23, 1);
		s3ids[3] = soundPool.load(this, R.raw.a33, 1);
		s4ids[3] = soundPool.load(this, R.raw.a43, 1);
		s5ids[3] = soundPool.load(this, R.raw.a53, 1);
		

		//fret 4
		s1ids[4] = soundPool.load(this, R.raw.a14, 1);
		s2ids[4] = soundPool.load(this, R.raw.a24, 1);
		s3ids[4] = soundPool.load(this, R.raw.a34, 1);
		s4ids[4] = soundPool.load(this, R.raw.a44, 1);
		s5ids[4] = soundPool.load(this, R.raw.a54, 1);
		
	}
	
	private void loadDSSounds() 
	{
		s1ids[0] = soundPool.load(this, R.raw.azo10, 1);
		s2ids[0] = soundPool.load(this, R.raw.azo20, 1);
		s3ids[0] = soundPool.load(this, R.raw.azo30, 1);
		s4ids[0] = soundPool.load(this, R.raw.azo40, 1);
		s5ids[0] = soundPool.load(this, R.raw.azo50, 1);
		

		//fret 1
		s1ids[1] = soundPool.load(this, R.raw.azo11, 1);
		s2ids[1] = soundPool.load(this, R.raw.azo21, 1);
		s3ids[1] = soundPool.load(this, R.raw.azo31, 1);
		s4ids[1] = soundPool.load(this, R.raw.azo41, 1);
		s5ids[1] = soundPool.load(this, R.raw.azo51, 1);
		

		//fret 2
		s1ids[2] = soundPool.load(this, R.raw.azo12, 1);
		s2ids[2] = soundPool.load(this, R.raw.azo22, 1);
		s3ids[2] = soundPool.load(this, R.raw.azo32, 1);
		s4ids[2] = soundPool.load(this, R.raw.azo42, 1);
		s5ids[2] = soundPool.load(this, R.raw.azo52, 1);
		

		//fret 3
		s1ids[3] = soundPool.load(this, R.raw.azo13, 1);
		s2ids[3] = soundPool.load(this, R.raw.azo23, 1);
		s3ids[3] = soundPool.load(this, R.raw.azo33, 1);
		s4ids[3] = soundPool.load(this, R.raw.azo43, 1);
		s5ids[3] = soundPool.load(this, R.raw.azo53, 1);
		

		//fret 4
		s1ids[4] = soundPool.load(this, R.raw.azo14, 1);
		s2ids[4] = soundPool.load(this, R.raw.azo24, 1);
		s3ids[4] = soundPool.load(this, R.raw.azo34, 1);
		s4ids[4] = soundPool.load(this, R.raw.azo44, 1);
		s5ids[4] = soundPool.load(this, R.raw.azo54, 1);
		
	}


//
//	private void loadDefaultSounds()
//	{
//		//fret 0
//		s1ids[0] = soundPool.load(this, R.raw.cuerda10, 1);
//		s2ids[0] = soundPool.load(this, R.raw.cuerda20, 1);
//		s3ids[0] = soundPool.load(this, R.raw.cuerda30, 1);
//		s4ids[0] = soundPool.load(this, R.raw.cuerda40, 1);
//		s5ids[0] = soundPool.load(this, R.raw.cuerda50, 1);
//		
//
//		//fret 1
//		s1ids[1] = soundPool.load(this, R.raw.cuerda11, 1);
//		s2ids[1] = soundPool.load(this, R.raw.cuerda21, 1);
//		s3ids[1] = soundPool.load(this, R.raw.cuerda31, 1);
//		s4ids[1] = soundPool.load(this, R.raw.cuerda41, 1);
//		s5ids[1] = soundPool.load(this, R.raw.cuerda51, 1);
//		
//
//		//fret 2
//		s1ids[2] = soundPool.load(this, R.raw.cuerda12, 1);
//		s2ids[2] = soundPool.load(this, R.raw.cuerda22, 1);
//		s3ids[2] = soundPool.load(this, R.raw.cuerda32, 1);
//		s4ids[2] = soundPool.load(this, R.raw.cuerda42, 1);
//		s5ids[2] = soundPool.load(this, R.raw.cuerda52, 1);
//		
//
//		//fret 3
//		s1ids[3] = soundPool.load(this, R.raw.cuerda13, 1);
//		s2ids[3] = soundPool.load(this, R.raw.cuerda23, 1);
//		s3ids[3] = soundPool.load(this, R.raw.cuerda33, 1);
//		s4ids[3] = soundPool.load(this, R.raw.cuerda43, 1);
//		s5ids[3] = soundPool.load(this, R.raw.cuerda53, 1);
//		
//
//		//fret 4
//		s1ids[4] = soundPool.load(this, R.raw.cuerda14, 1);
//		s2ids[4] = soundPool.load(this, R.raw.cuerda24, 1);
//		s3ids[4] = soundPool.load(this, R.raw.cuerda34, 1);
//		s4ids[4] = soundPool.load(this, R.raw.cuerda44, 1);
//		s5ids[4] = soundPool.load(this, R.raw.cuerda54, 1);
//	}


	private void loadChordLibrary() 
    {
    	 chordLibrary = new LinkedHashMap<String,int[]>();
    	 chordLabelTranslator = new HashMap<String,String>();
    	 chordLabelTranslatorBack = new HashMap<String,String>();
    	 
    	 
//    	 chordLibrary.put("K", new int[]{1,2,3,4,5});

    	 
    	//C-DO
    	 addChord("C",  "Do",new int[]{2,3,0,0,0});
    	 //C7-Do7
    	 addChord("C7",  "Do7",new int[]{2,1,0,0,0});
    	 //Cm-Dom
    	 addChord("Cm",  "Dom",new int[]{1,3,3,0,0});
    	 //Cd-DoD
    	 addChord("Cd",  "DoD",new int[]{1,0,2,0,2});

    	 //D-RE
    	 addChord("D",  "Re",new int[]{0,0,2,2,2});
    	 //D7-RE7
    	 addChord("D7",  "Re7",new int[]{0,0,2,0,2});
    	 //Dm-REm
    	 addChord("Dm",  "Rem",new int[]{0,0,1,2,2});
    	 //Dd-REd
    	 addChord("Dd",  "ReD",new int[]{0,2,1,2,1});
    	 
    	 
    	 
    	 //E-MI
    	 addChord("E",  "Mi",new int[]{2,2,0,4,1});
    	 //E7-MI7
    	 addChord("E7",  "Mi7",new int[]{2,2,0,2,1});
    	 //Em-MIm
    	 addChord("Em",  "Mim",new int[]{2,2,0,4,0});
    	 //Ed-MId
    	 addChord("Ed",  "MiD",new int[]{2,1,3,1,0});
    	 
    	 
    	 
    	//F-FA
    	 addChord("F",  "Fa",new int[]{3,0,1,0,2});
    	//F7-FA7
    	 addChord("F7",  "Fa7",new int[]{1,0,1,0,2});
    	//Fm-FAm
    	 addChord("Fm",  "Fam",new int[]{3,3,1,0,1});
    	//Fd-FAd
    	 addChord("Fd",  "FaD",new int[]{0,2,1,2,1});
    	//F#m-FA#m
    	 addChord("F#m",  "Fa#m",new int[]{4,0,2,1,2});
    	//F#7-FA#7 TODO
    	 addChord("F#7",  "Fa#7",new int[]{4,4,2,4,3});

    	 
    	 
    	 //G-SOL
   	 	 addChord("G", "Sol", new int[]{0,2,3,2,0});
    	 //G7-SOL7
    	 addChord("G7",  "Sol7",new int[]{0,2,1,2,0});
    	 //Gm-SOLm
   	 	 addChord("Gm", "Solm", new int[]{0,1,3,2,0});
    	 //Gd-SOLd
    	 addChord("Gd",  "SolD",new int[]{2,1,0,1,0});

    	 //A-LA
    	 addChord("A",  "La",new int[]{2,0,0,1,2});
    	 //A7-LA
    	 addChord("A7",  "La7",new int[]{2,0,0,1,0});
    	 //Am-LAm
    	 addChord("Am",  "Lam",new int[]{2,0,0,0,2});
    	 //Ad-LAd
    	 addChord("Ad",  "LaD",new int[]{1,0,2,0,2});
    	 
    	 

    	 //B-SI
    	 addChord("B",  "Si",new int[]{4,2,2,3,4});
    	 //B7-SI7
    	 addChord("B7",  "Si7",new int[]{4,0,2,3,4});
    	 //Bm-SIm
    	 addChord("Bm",  "Sim",new int[]{4,2,2,2,4});
    	 //Bd-SId
    	 addChord("Bd",  "SiD",new int[]{0,2,1,2,1});
    	 //Bb-SIb
    	 addChord("Bb",  "Si",new int[]{3,1,1,2,3});
    	 
    	 
    	 Iterator it = chordLabelTranslator.entrySet().iterator();
    	 while (it.hasNext()) 
 	     {
    		 Map.Entry pairs = (Map.Entry)it.next();
    		 String v = (String) pairs.getValue();
    		 String k = (String) pairs.getKey();
    		 
    		 chordLabelTranslatorBack.put(v, k);
    		 
 	     }
    	 
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.d(TAG,"postcreate");
		
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

	public void addChord(String angChor, String latChord, int[] keys)
	{
	   	chordLibrary.put(angChor, keys);
	   	chordLabelTranslator.put(angChor, latChord);
	}
	

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //mSystemUiHider.hide();
        	Log.d(TAG,"RUN HIDER");
        }
    };



    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    
    

    
    public void loadChord(View view) {
    	
    	Button b = (Button) view;
    	String c = (String) b.getText();
    	
    	
    	if(getNotacion())
    	{
    		Log.d(TAG,"Translating " + c + " into " + chordLabelTranslatorBack.get(c));
    		c=chordLabelTranslatorBack.get(c);
    	}
    	
    	if(chordLibrary.containsKey(c))
    	{
    		currentChord[0]=chordLibrary.get(c)[0];
    		currentChord[1]=chordLibrary.get(c)[1];
    		currentChord[2]=chordLibrary.get(c)[2];
    		currentChord[3]=chordLibrary.get(c)[3];
    		currentChord[4]=chordLibrary.get(c)[4];
    		//= chordLibrary.get(c);
    		clearChordButtons(b);
        	Log.d(TAG,"Loading "+c+" Chord("+currentChord[0]+currentChord[1]+currentChord[2]+currentChord[3]+currentChord[4]+")");
    	}
    	else
    	{
    		System.err.println(CHORD_ERR_MASSAGE + c);
    	}
    	

    }
    
    public void clearChordButtons(Button b)
    {
    	bchord1.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord2.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord3.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord4.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord5.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord6.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);
    	bchord7.getBackground().setColorFilter(BASE_COLOR, PorterDuff.Mode.MULTIPLY);   
		if(b!=null)
		{
			b.getBackground().setColorFilter(SELECTED_COLOR, PorterDuff.Mode.MULTIPLY);
		}
    }
		
    

    @SuppressLint("NewApi")
	public static int myGetWidth(Context mContext){
        int width=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.HONEYCOMB){                   
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }
        else{
            width = display.getWidth();  // deprecated
        }
        return width;
    }
    
    @Override
	protected void onResume()
	{
		super.onResume();
		//get max volume
		//int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);	
		loadFretSounds();
	}
	
	
	@Override
	protected void onRestart()
	{
		super.onResume();
		//get max volume
		//int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		loadFretSounds();
	}

	
	public void setBackground(int id)
	{
		LinearLayout layout =(LinearLayout)findViewById(R.id.screen);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    layout.setBackgroundDrawable( getResources().getDrawable(id) );
		} else {
		    layout.setBackground( getResources().getDrawable(id));
		}
	}
	
}
