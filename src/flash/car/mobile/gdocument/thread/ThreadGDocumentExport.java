package flash.car.mobile.gdocument.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.WorkSheetRow;
import com.pras.auth.Authenticator;

public class ThreadGDocumentExport implements Runnable {

	private Bundle authBundle;

	public ThreadGDocumentExport(String authToken) {
		authBundle = new Bundle();
		if (authToken!=null) {
			String[] l = authToken.split(";");
			for(int i=0 ; i<l.length ; i++) {
				String[] j = l[i].split(":");
				if (j.length==2) {
					Log.i("ThreadGDocumentExport", "ThreadGDocumentExport constructor authBundle add key:"+j[0]+" value:"+j[1]);
					authBundle.putString(j[0], j[1]);
				}
			}
		}
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport constructor authBundle.size:"+authBundle.size());
	}

	public void run(){
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport run START");
		Authenticator authenticator = new Authenticator() {
			public String getAuthToken(String service) {
				String token = authBundle.getString(service);
				Log.i("ThreadGDocumentExport", "ThreadGDocumentExport getAuthToken service:["+service+"] token:"+token);
				return token;
			}	        				
		};
		SpreadSheetFactory factory = SpreadSheetFactory.getInstance(authenticator);

        String title = "FlashCarMobileReport";
		String titleWorkSheet = "report";

		// Get selected SpreadSheet 	
		ArrayList<SpreadSheet> spreadSheets = factory.getSpreadSheet(title, false);
		
		if(spreadSheets == null || spreadSheets.size() == 0){
			Log.i("ThreadGDocumentExport", "No SpreadSheet Exists!");
	        factory.createSpreadSheet(title);
	        Log.i("ThreadGDocumentExport", "SpreadSheet '"+title+"' created");
	        spreadSheets = factory.getSpreadSheet(title, false);
		}
		
		Log.i("ThreadGDocumentExport", "Number of SpreadSheets: "+ spreadSheets.size());
		
		SpreadSheet sp = spreadSheets.get(0);
		WorkSheet workSheet = null;
		List<WorkSheet> workSheets = sp.getWorkSheet(titleWorkSheet, false);
		if(workSheets == null || workSheets.size() == 0){
			Log.i("ThreadGDocumentExport", "### Creating WorkSheet for ListFeed ###");
			String[] columns = {"date", "item", "price"};
			workSheet = sp.addListWorkSheet(titleWorkSheet, 1, columns);
			Log.i("ThreadGDocumentExport", "WorkSheet '"+workSheet+"' created");
		}
		else {
			workSheet = workSheets.get(0);
		}
		
		HashMap<String, String> row_data = new HashMap<String, String>();
		row_data.put("date", new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
		row_data.put("item", "Data"+new Random().nextLong());
		row_data.put("price", Float.toString(new Random().nextFloat()));
		
		// Add entries
		WorkSheetRow list_row1 = workSheet.addListRow(row_data);
		Log.i("ThreadGDocumentExport", "Data '"+row_data+"' added");

        Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 14 flushMe DEFORE");
		factory.flushMe();
		Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 15 flushMe AFTER");

        Log.i("ThreadGDocumentExport", "ThreadGDocumentExport 16 run END");
	}
}