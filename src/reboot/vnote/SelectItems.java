package reboot.vnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import ddbb.Conexion;
import ddbb.Note;

public class SelectItems extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;
	Conexion con;
	private String lastQuery = "";
	private SparseBooleanArray SelectedItems;
	private String[] ArraySelectedItems;
	private AudioManager audio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectitems);
		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent thisIntent = getIntent();
		setLastQuery(thisIntent.getExtras().getString("lastQuery"));
		FillListView();
		saveSelectedItems();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select, menu);
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		SearchView search = (SearchView) menu.findItem(R.id.search)
				.getActionView();

		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				saveSelectedItems();
				if (newText.trim().equals("")) {
					setLastQuery("SELECT title FROM notes ORDER BY date DESC"); // default
				} else {
					setLastQuery("SELECT title FROM notes WHERE title LIKE '%"
							+ newText + "%'");
				}
				FillListView();
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.search:
			saveSelectedItems();
			return true;
		case R.id.delete:
			saveSelectedItems();
			EraseNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getSelectedItemsString() {
		String auxstr = "";
		int len = lvNotes.getCount();
		SparseBooleanArray check = getSelectedItems();
		for (int i = 0; i < len; i++) {
			if (check.get(i)) {
				Note item = list.get(i);
				auxstr += item.getTitle() + "\n";
			}
		}
		return auxstr;
	}

	private void RecheckItems() {
		String[] titleItems = getArraySelectedItems();
		int len = lvNotes.getCount();
		for (int i = 0; i < len; i++) {
			Note item = list.get(i);
			if (item.getTitle().equals(titleItems[i])) {
				lvNotes.setItemChecked(i, true);
			}
		}
	}

	private String[] setSelectedItemsArray() {
		String[] auxstr = {};
		int len = lvNotes.getCount();
		SparseBooleanArray check = getSelectedItems();
		for (int i = 0; i < len; i++) {
			if (check.get(i)) {
				Note item = list.get(i);
				auxstr[i] = item.getTitle();
			}
		}
		return auxstr;

	}

	private void FillListView() {
		con = new Conexion(getApplicationContext(), "DBNotes.db", null, 1);
		db = con.getWritableDatabase();
		TEST_INSERT();
		Cursor a = db.rawQuery(getLastQuery(), null);
		list.clear();
		if (a.moveToFirst()) {
			do {
				nota = new Note(a.getString(0));
				list.add(nota);
			} while (a.moveToNext());
		}

		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(
				getApplicationContext(),
				android.R.layout.simple_list_item_checked, list);

		lvNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lvNotes.setAdapter(adapt);

		if (getSelectedItems() == null || getSelectedItems().size() == 0) {
			Toast.makeText(this, "ninguno seleccionado", Toast.LENGTH_SHORT)
					.show();
		} else {
			RecheckItems();
		}
	}

	private void saveSelectedItems() {
		setSelectedItems();
		if (!(getSelectedItems() == null || getSelectedItems().size() == 0)) {
			setArraySelectedItems();
		}
	}

	private void EraseNote() {
		String listItemsSelected = getSelectedItemsString();
		if (!listItemsSelected.equals("")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Do you want to DELETE the following notes?");
			final TextView tv_alert = new TextView(this);
			tv_alert.setText(listItemsSelected);
			alert.setView(tv_alert);

			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int len = lvNotes.getCount();
							final SparseBooleanArray checked = lvNotes
									.getCheckedItemPositions();
							for (int i = 0; i < len; i++) {
								if (checked.get(i)) {
									Note item = list.get(i);
									con.deleteNote(item.getTitle());
								}
							}
							FillListView();
						}
					});
			alert.setNegativeButton("No", null);
			alert.show();
		}

	}

	private void TEST_INSERT() {
		con.InsertNote(db, con.getToday(), con.getToday(), con.getToday());
	}

	public SparseBooleanArray getSelectedItems() {
		return SelectedItems;
	}

	public void setSelectedItems() {
		SelectedItems = lvNotes.getCheckedItemPositions();
	}

	public String[] getArraySelectedItems() {
		return ArraySelectedItems;
	}

	public void setArraySelectedItems() {
		ArraySelectedItems = setSelectedItemsArray();
	}

	public void setLastQuery(String lastQuery) {
		this.lastQuery = lastQuery;
	}

	public String getLastQuery() {
		return lastQuery;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		// Para controlar el volumen
		case KeyEvent.KEYCODE_VOLUME_UP:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			return true;
		default:
			return true;
		}

	}
}