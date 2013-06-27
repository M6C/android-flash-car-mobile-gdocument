package flash.car.mobile.gdocument;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import flash.car.mobile.gdocument.auth.AuthenticatorActivity;
import flash.car.mobile.gdocument.thread.ThreadGDocumentExport;

public class FlashCarMobileGDocumentActivity extends Activity {
	private static final int AUTH_TOKEN_REQUEST = 1337;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i("FlashCarMobile", "onCreate 1");
        super.onCreate(savedInstanceState);

		Log.i("FlashCarMobile", "onCreate 5");
        Intent intent = new Intent(this, AuthenticatorActivity.class);
//        intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, "roca.david@gmail.com");
//        intent.putExtra(AuthenticatorActivity.PARAM_PASSWORD, "32143214");
		Log.i("FlashCarMobile", "onCreate 6");
		startActivityForResult(intent, AUTH_TOKEN_REQUEST);

		Log.i("FlashCarMobile", "onCreate 7");
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		try {
	        Log.i("FlashCarMobile", "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" intent:"+intent+" - START");
	        super.onActivityResult(requestCode, resultCode, intent);
	        switch (requestCode) {
	          case AUTH_TOKEN_REQUEST:
	        	  if (intent!=null && intent.hasExtra(AccountManager.KEY_AUTHTOKEN)) {
		        	String authToken = intent.getExtras().getString(AccountManager.KEY_AUTHTOKEN);
			        Log.i("FlashCarMobile", "onActivityResult AUTH_TOKEN_REQUEST authToken:"+authToken);
		            // Init and Read SpreadSheet list from Google Server
		            runOnUiThread(new ThreadGDocumentExport(authToken));
	        	  }
			      Log.i("FlashCarMobile", "onActivityResult AUTH_TOKEN_REQUEST no authToken found");
	          break;
	        }
	        Log.i("FlashCarMobile", "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode+" intent:"+intent+" - END");
		}
		catch (RuntimeException ex) {
			Log.e("FlashCarMobile", "RuntimeException", ex);
			throw ex;
		}
	}
}