package com.nfcutil.app.util;

import java.io.IOException;

import com.nfcutil.app.activity.MifareClassic1kActivity;
import com.nfcutil.app.activity.MifareUltralightCActivity;
import com.nfcutil.app.entity.MifareClassic1k;
import com.nfcutil.app.entity.MifareUltraLightC;
import com.skarim.app.utils.CommonTasks;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.util.Log;
import android.widget.Toast;

public class NFCHammer {
	
	public static boolean ReadULCValue(Context context, Tag t){
		CommonValues.getInstance().mifareUltraLightCList.clear();
		Log.d("skm",
				"===========Mifare Ultralight C Read Start==================");
		Tag tag = t;
		String tagId;
		MifareUltraLightC mifareUltraLightC;
		MifareUltralight mifare = MifareUltralight.get(tag);
		Log.d("skm", "Tag Type :" + mifare.getType());
		CommonValues.getInstance().Type=""+mifare.getType();
		try {
			tagId = CommonTasks.getHexString(tag.getId());
			Log.d("skm", "UID :" + tagId.trim());
			CommonValues.getInstance().Name="Mifare UltraLight C";
			CommonValues.getInstance().UID=tagId;
			CommonValues.getInstance().Memory="192 bytes";
			CommonValues.getInstance().ultraLightCPageSize=""+mifare.PAGE_SIZE;
			CommonValues.getInstance().ultraLightCPageCount="44";
			mifare.connect();
			for (int i = 0; i < 11; i++) {
				mifareUltraLightC = new MifareUltraLightC();
				mifareUltraLightC.Header = "Page " + (i * 4) + " to "
						+ (((i + 1) * 4) - 1);
				mifareUltraLightC.pagevalue1 = CommonTasks.getHexString(mifare
						.readPages(i * 4));
				mifareUltraLightC.pagevalue2 = CommonTasks.getHexString(mifare
						.readPages(((i * 4) + 1)));
				mifareUltraLightC.pagevalue3 = CommonTasks.getHexString(mifare
						.readPages(((i * 4) + 2)));
				mifareUltraLightC.pagevalue4 = CommonTasks.getHexString(mifare
						.readPages(((i * 4) + 3)));
				mifareUltraLightC.block1 = (i * 4);
				mifareUltraLightC.block2 = ((i * 4)+1);
				mifareUltraLightC.block3 = ((i * 4)+2);
				mifareUltraLightC.block4 = ((i * 4)+3);
				CommonValues.getInstance().mifareUltraLightCList
						.add(mifareUltraLightC);
			
			}

		} catch (IOException e) {
			Log.d("skm", e.getMessage());
		} finally {
			if (mifare != null) {
				try {
					mifare.close();
				} catch (IOException e) {
					Log.d("skm", e.getMessage());
				}
			}
		}
		if (CommonValues.getInstance().mifareUltraLightCList.size() == 11) {
			return true;
		}else{
			Toast.makeText(context, "Please Hold your card again!", Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	public static boolean ReadClassic1kValue(Context context, MifareClassic _mfc){
		CommonValues.getInstance().mifareClassic1kList.clear();
		MifareClassic mfc = _mfc;
		MifareClassic1k classic1k;
		try {
			mfc.connect();

			Log.d("skm", "== MifareClassic Info == ");
			CommonValues.getInstance().Name="Mifare Classic 1K";
			Log.d("skm", "Size: " + mfc.getSize());
			CommonValues.getInstance().Memory=""+mfc.getSize();
			Log.d("skm", "Type: " + mfc.getType());
			CommonValues.getInstance().Type=""+mfc.getType();
			Log.d("skm", "BlockCount: " + mfc.getBlockCount());
			CommonValues.getInstance().Block=""+mfc.getBlockCount();

			Log.d("skm", "MaxTransceiveLength: " + mfc.getMaxTransceiveLength());
			Log.d("skm", "SectorCount: " + mfc.getSectorCount());
			CommonValues.getInstance().Sector=""+mfc.getSectorCount();
			CommonValues.getInstance().UID=CommonTasks.getHexString(mfc.getTag().getId());

			Log.d("skm", "Reading sectors...");

			for (int i = 0; i < mfc.getSectorCount(); ++i) {
				classic1k = new MifareClassic1k();
				if (mfc.authenticateSectorWithKeyA(i,
						MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)) {
					Log.d("skm", "Authorized sector " + i + " with MAD key");
					classic1k.Header = i;

				} else if (mfc.authenticateSectorWithKeyA(i,
						MifareClassic.KEY_DEFAULT)) {
					classic1k.Header = i;
					Log.d("skm", "Authorization granted to sector " + i
							+ " with DEFAULT key");

				} else if (mfc.authenticateSectorWithKeyA(i,
						MifareClassic.KEY_NFC_FORUM)) {
					classic1k.Header = i;
					Log.d("skm", "Authorization granted to sector " + i
							+ " with NFC_FORUM key");

				} else {
					Log.d("skm", "Authorization denied to sector " + i);

					continue;
				}


				for (int k = 0; k < mfc.getBlockCountInSector(i); ++k) {
					int block = mfc.sectorToBlock(i) + k;
					byte[] data = null;

					try {

						data = mfc.readBlock(block);
					} catch (IOException e) {
						Log.d("skm",
								"Block " + block + " data: " + e.getMessage());
						continue;
					}
					String blockData = CommonTasks.getHexString(data);

					switch (k) {
						case 0:
							classic1k.Block1Value = blockData;
							classic1k.block1 = (i*4)+k;
							break;
						case 1:
							classic1k.Block2Value = blockData;
							classic1k.block2 = (i*4)+k;
							break;
						case 2:
							classic1k.Block3Value = blockData;
							classic1k.block3 = (i*4)+k;
							break;
						case 3:
							classic1k.Block4Value = blockData;
							classic1k.block4 = (i*4)+k;
							break;

					}
					Log.d("skm", "Block " + block + " data: " + blockData);
				}
				CommonValues.getInstance().mifareClassic1kList.add(classic1k);
			}

		} catch (Exception exception) {
			exception.printStackTrace();

		}
		
		if(CommonValues.getInstance().mifareClassic1kList.size()==16){
			return true;
		}else{
			Toast.makeText(context, "Please Hold your card again!", Toast.LENGTH_LONG).show();
			return false;
		}
	}
}
