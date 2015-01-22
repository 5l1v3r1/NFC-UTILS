package com.nfcutil.app.activity;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.example.nfcutils.R;
import com.nfcutil.app.adapters.MifareUltraLightCAdapter;
import com.nfcutil.app.base.NFCUtilsBase;
import com.nfcutil.app.entity.MifareUltraLightC;
import com.nfcutil.app.util.CommonTask;
import com.nfcutil.app.util.CommonValues;
import com.nfcutil.app.util.NFCHammer;

public class MifareUltralightCActivity extends NFCUtilsBase implements OnItemClickListener{
	TextView tvUID, tvType, tvMemory, tvPage, tvBlock, value1, value2, value3, value4;
	PinnedHeaderListView lvMifareUltralLightC;
	MifareUltraLightCAdapter adapter;
	MifareUltraLightC mifareUltraLightC;
	LinearLayout llViewOfValue, llEditOfValue;
	EditText etNFCValue;
	RelativeLayout rlCancel, rlOK, rlshowvalue;
	Spinner spBlockNumber, spShowBlockValue;
	String selectedBlockNumber;
	NfcAdapter mAdapter;
	PendingIntent mPendingIntent;
	Tag tag;
	AlertDialog writeAlertDialog ;
	
	@Override
	protected void onCreate(Bundle saveInstance) {
		super.onCreate(saveInstance);
		setContentView(R.layout.activity_mifare_ultralight_c);
		Initalization();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setListValue();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

	private void Initalization() {
		tvUID = (TextView) findViewById(R.id.tvUID);
		tvType = (TextView) findViewById(R.id.tvType);
		tvMemory = (TextView) findViewById(R.id.tvMemory);
		tvPage = (TextView) findViewById(R.id.tvPage);
		tvBlock = (TextView) findViewById(R.id.tvBlock);
		lvMifareUltralLightC = (PinnedHeaderListView) findViewById(R.id.lvMifareUltralLightC);
		
		lvMifareUltralLightC.setOnItemClickListener(this);
		setValue();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		try{
			if((position%2) != 0){
				mifareUltraLightC = (MifareUltraLightC) view.getTag();
				showValue(mifareUltraLightC);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void setValue(){
		tvUID.setText(CommonValues.getInstance().UID);
		tvType.setText(CommonValues.getInstance().Type);
		tvMemory.setText(CommonValues.getInstance().Memory);
		tvPage.setText(CommonValues.getInstance().ultraLightCPageCount);
		tvBlock.setText(CommonValues.getInstance().ultraLightCPageSize);
	}
	
	private void setListValue(){
		adapter = new MifareUltraLightCAdapter(this, R.layout.classic_1k_individual_item, CommonValues.getInstance().mifareUltraLightCList);
		lvMifareUltralLightC.setAdapter(adapter);
	}
	
	private void showValue(final MifareUltraLightC _mifareUltraLightC){
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("NFC Write");
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.nfc_write_dialog, null);
		builder.setView(dialogView);
		final AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		
		spBlockNumber = (Spinner) dialogView.findViewById(R.id.spBlockNumber);
		spShowBlockValue = (Spinner) dialogView.findViewById(R.id.spShowBlockValue);
		value1 = (TextView) dialogView.findViewById(R.id.value1);
		value2 = (TextView) dialogView.findViewById(R.id.value2);
		value3 = (TextView) dialogView.findViewById(R.id.value3);
		value4 = (TextView) dialogView.findViewById(R.id.value4);
		llViewOfValue = (LinearLayout) dialogView.findViewById(R.id.llViewOfValue);
		llEditOfValue = (LinearLayout) dialogView.findViewById(R.id.llEditOfValue);
		etNFCValue = (EditText) dialogView.findViewById(R.id.etNFCValue);
		rlCancel = (RelativeLayout) dialogView.findViewById(R.id.rlCancel);
		rlOK = (RelativeLayout) dialogView.findViewById(R.id.rlOK);
		rlshowvalue = (RelativeLayout) dialogView.findViewById(R.id.rlshowvalue);
		String[] valueOfULC = {"Select One",String.valueOf(_mifareUltraLightC.block1),String.valueOf(_mifareUltraLightC.block2),String.valueOf(_mifareUltraLightC.block3),String.valueOf(_mifareUltraLightC.block4)};
		String[] convertOfULCValue = {"Hex Value","Asci Value"};
		spBlockNumber.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, valueOfULC));
		spShowBlockValue.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, convertOfULCValue));
		
		llViewOfValue.setVisibility(View.VISIBLE);
		llEditOfValue.setVisibility(View.GONE);
		
		value1.setText(_mifareUltraLightC.pagevalue1);
		value2.setText(_mifareUltraLightC.pagevalue2);
		value3.setText(_mifareUltraLightC.pagevalue3);
		value4.setText(_mifareUltraLightC.pagevalue4);
		
		spShowBlockValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String value = (String) spShowBlockValue.getItemAtPosition(position);
				if(value.equals("Hex Value")){
					value1.setText(_mifareUltraLightC.pagevalue1);
					value2.setText(_mifareUltraLightC.pagevalue2);
					value3.setText(_mifareUltraLightC.pagevalue3);
					value4.setText(_mifareUltraLightC.pagevalue4);
				}else{
					value1.setText(CommonTask.hexToAscii((_mifareUltraLightC.pagevalue1)));
					value2.setText(CommonTask.hexToAscii((_mifareUltraLightC.pagevalue2)));
					value3.setText(CommonTask.hexToAscii((_mifareUltraLightC.pagevalue3)));
					value4.setText(CommonTask.hexToAscii((_mifareUltraLightC.pagevalue4)));
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		spBlockNumber.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectedBlockNumber = (String) spBlockNumber.getItemAtPosition(position);
				if(!selectedBlockNumber.equals("Select One") && position != 0){
					llViewOfValue.setVisibility(View.GONE);
					llEditOfValue.setVisibility(View.VISIBLE);
					rlshowvalue.setVisibility(View.GONE);
					if(position == 1){
						etNFCValue.setText(_mifareUltraLightC.pagevalue1.equals("00000000")?"":CommonTask.hexToAscii(_mifareUltraLightC.pagevalue1));
					}else if(position == 2){
						etNFCValue.setText(_mifareUltraLightC.pagevalue2.equals("00000000")?"":CommonTask.hexToAscii(_mifareUltraLightC.pagevalue2));
					}else if(position == 3){
						etNFCValue.setText(_mifareUltraLightC.pagevalue3.equals("00000000")?"":CommonTask.hexToAscii(_mifareUltraLightC.pagevalue3));
					}else if(position == 4){
						etNFCValue.setText(_mifareUltraLightC.pagevalue4.equals("00000000")?"":CommonTask.hexToAscii(_mifareUltraLightC.pagevalue4));
					}
				}else{
					llViewOfValue.setVisibility(View.VISIBLE);
					llEditOfValue.setVisibility(View.GONE);
					rlshowvalue.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		rlCancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		
		rlOK.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!selectedBlockNumber.equals("Select One")){
					alertDialog.dismiss();
					writeULCValue(etNFCValue.getText().toString(), Integer.parseInt(selectedBlockNumber));
				}
			}
		});
		
		
		alertDialog.show();
		
	}

	private void writeULCValue(String value, int blocknumber){
		try{
			AlertDialog.Builder writeBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			writeBuilder.setTitle("NFC Write");
			writeBuilder.setMessage("Please Tap the card!!!");
			writeBuilder.setCancelable(false);
			writeBuilder.setNegativeButton("Dismis", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mAdapter != null) {
						mAdapter.disableForegroundDispatch(MifareUltralightCActivity.this);
					}
					dialog.dismiss();					
				}
			});
			writeAlertDialog = writeBuilder.create();
			writeAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;			
			
			mAdapter = NfcAdapter.getDefaultAdapter(this);
			if (mAdapter == null) {
				CommonTask.showMessage(this, R.string.error, R.string.no_nfc);
				// finish();
				return;
			}
			mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			if (mAdapter != null) {
				if (!mAdapter.isEnabled()) {
					CommonTask.showWirelessSettingsDialog(this);
				}
				mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
			}
			writeAlertDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		resolveIntent(intent);
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			getTagInfo(intent);
		}
	}

	private void getTagInfo(Intent intent) {
		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String[] techList = tag.getTechList();
		for (int i = 0; i < techList.length; i++) {
			if (techList[i].equals(MifareUltralight.class.getName())) {
				MifareUltralight mifareUlTag = MifareUltralight.get(tag);
				switch (mifareUlTag.getType()) {
				case MifareUltralight.TYPE_ULTRALIGHT:
					break;
				case MifareUltralight.TYPE_ULTRALIGHT_C:
					boolean result = NFCHammer.writeOnMifareUltralightC(this, tag, etNFCValue.getText().toString(),Integer.parseInt(selectedBlockNumber));
					if(result){
						result = NFCHammer.ReadULCValue(this, tag);
						if(result){
							adapter = new MifareUltraLightCAdapter(this, R.layout.classic_1k_individual_item, CommonValues.getInstance().mifareUltraLightCList);
							writeAlertDialog.dismiss();
							
							adapter.notifyDataSetChanged();
						}
					}
					break;
				}
				break;
			}else{
				//Toast.makeText(this, "Please Tap the UltraLight tag.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
