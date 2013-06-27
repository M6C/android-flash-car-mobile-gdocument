package flash.car.mobile.gdocument.auth.util;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/**
 * Provides utility methods for communicating with the server.
 */
final public class NetworkUtilities {
    /** The tag used to log to adb console. */
    private static final String TAG = "NetworkUtilities";
    /** POST parameter name for the user's account name */
    public static final String PARAM_USERNAME = "username";
    /** POST parameter name for the user's password */
    public static final String PARAM_PASSWORD = "password";
    /** POST parameter name for the user's authentication token */
    public static final String PARAM_AUTH_TOKEN = "authtoken";
    /** POST parameter name for the client's last-known sync state */
    public static final String PARAM_SYNC_STATE = "syncstate";
    /** POST parameter name for the sending client-edited contact info */
    public static final String PARAM_CONTACTS_DATA = "contacts";
    /** Timeout (in ms) we specify for each http request */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** Base URL for the v2 Sample Sync Service */
    //public static final String BASE_URL = "https://samplesyncadapter2.appspot.com";
    public static final String BASE_URL = "https://www.google.com";
    /** URI for authentication service */
    //public static final String AUTH_URI = BASE_URL + "/auth";
    public static final String AUTH_URI = BASE_URL + "/accounts/ClientLogin";
    /** URI for sync service */
    public static final String SYNC_CONTACTS_URI = BASE_URL + "/sync";

    public static final String PARAM_ACCOUNT_TYPE = "accountType";
    public static final String PARAM_ACCOUNT_TYPE_VALUE = "GOOGLE";
    public static final String PARAM_EMAIL = "Email";
    public static final String PARAM_PASSWD = "Passwd";
    public static final String PARAM_SERVICE = "service";
    //http://code.google.com/apis/gdata/faq.html
    public static final String PARAM_SERVICE_VALUE = "wise";
    public static final String PARAM_SOURCE = "source";//test-app-log

//    private static final String PROXY_URL = "webproxy-rgs.telintrans.fr";
//    private static final int PROXY_PORT = 3128;
//    private static final String PROXY_USER = "droca";
//    private static final String PROXY_PASS = "R3214david3214";

    private NetworkUtilities() {
    }

    private static void setProxy(DefaultHttpClient httpClient) {
//    	Log.i(TAG, "setProxy DefaultHttpClient 1");
//        httpClient.getCredentialsProvider().setCredentials(
//                new AuthScope(PROXY_URL, PROXY_PORT),
//                new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
//		Log.i(TAG, "setProxy DefaultHttpClient 2");
//
//        HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT);
//        //Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(PROXY_URL, PROXY_PORT));
//        Log.i(TAG, "setProxy DefaultHttpClient 3");
//
//        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//        Log.i(TAG, "setProxy DefaultHttpClient 4");
    }

    private static void setProxy(HttpPost post) {
//        Log.i(TAG, "setProxy HttpPost 1");
//        HttpHost proxy = new HttpHost(PROXY_URL, PROXY_PORT);
//        //Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(PROXY_URL, PROXY_PORT));
//        Log.i(TAG, "setProxy HttpPost 2");
//        post.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//        Log.i(TAG, "setProxy HttpPost 3");
    }

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        // Proxy
        setProxy(httpClient);
        return httpClient;
    }

    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @return String The authentication token returned by the server (or null)
     */
    public static String authenticate(String username, String password) {
    	return authenticate(username, password, PARAM_SERVICE_VALUE);
    }

    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @param service  The google service name (http://code.google.com/apis/gdata/faq.html)
     * @return String The authentication token returned by the server (or null)
     */
    public static String authenticate(String username, String password, String service) {

    	if (service==null)
    		service = PARAM_SERVICE_VALUE;

        final HttpResponse resp;
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(PARAM_USERNAME, username));
//        params.add(new BasicNameValuePair(PARAM_PASSWORD, password));

        params.add(new BasicNameValuePair(PARAM_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE_VALUE));
        params.add(new BasicNameValuePair(PARAM_EMAIL, username));
        params.add(new BasicNameValuePair(PARAM_PASSWD, password));
        params.add(new BasicNameValuePair(PARAM_SERVICE, service));
        params.add(new BasicNameValuePair(PARAM_SOURCE, "test-app-log"));

        final HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            // this should never happen.
            throw new IllegalStateException(e);
        }
        Log.i(TAG, "Authenticating to: " + AUTH_URI);
        final HttpPost post = new HttpPost(AUTH_URI);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        // Proxy
        setProxy(post);
        try {
            resp = getHttpClient().execute(post);
            String authToken = null;
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream istream = (resp.getEntity() != null) ? resp.getEntity().getContent()
                        : null;
                if (istream != null) {
                    BufferedReader ireader = new BufferedReader(new InputStreamReader(istream));
                    for(String str = "" ; str!=null ; ) {
                    	str = ireader.readLine();
                        Log.i(TAG, "NetworkUtilities authenticate str: " + str);
                    	if (str!=null && str.trim().substring(0, 5).toUpperCase().equals("AUTH=")) {
                    		authToken = str.trim().substring(5);
                    		break;
                    	}
                    }
                    Log.i(TAG, "NetworkUtilities authenticate AuthToken: " + authToken);
                }
            }
            if ((authToken != null) && (authToken.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                return authToken;
            } else {
                Log.e(TAG, "Error authenticating" + resp.getStatusLine());
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            return null;
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }
    }
//
//    /**
//     * Perform 2-way sync with the server-side contacts. We send a request that
//     * includes all the locally-dirty contacts so that the server can process
//     * those changes, and we receive (and return) a list of contacts that were
//     * updated on the server-side that need to be updated locally.
//     *
//     * @param account The account being synced
//     * @param authtoken The authtoken stored in the AccountManager for this
//     *            account
//     * @param serverSyncState A token returned from the server on the last sync
//     * @param dirtyContacts A list of the contacts to send to the server
//     * @return A list of contacts that we need to update locally
//     */
//    public static List<RawContact> syncContacts(
//            Account account, String authtoken, long serverSyncState, List<RawContact> dirtyContacts)
//            throws JSONException, ParseException, IOException, AuthenticationException {
//        // Convert our list of User objects into a list of JSONObject
//        List<JSONObject> jsonContacts = new ArrayList<JSONObject>();
//        for (RawContact rawContact : dirtyContacts) {
//            jsonContacts.add(rawContact.toJSONObject());
//        }
//
//        // Create a special JSONArray of our JSON contacts
//        JSONArray buffer = new JSONArray(jsonContacts);
//
//        // Create an array that will hold the server-side contacts
//        // that have been changed (returned by the server).
//        final ArrayList<RawContact> serverDirtyList = new ArrayList<RawContact>();
//
//        // Prepare our POST data
//        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(PARAM_USERNAME, account.name));
//        params.add(new BasicNameValuePair(PARAM_AUTH_TOKEN, authtoken));
//        params.add(new BasicNameValuePair(PARAM_CONTACTS_DATA, buffer.toString()));
//        if (serverSyncState > 0) {
//            params.add(new BasicNameValuePair(PARAM_SYNC_STATE, Long.toString(serverSyncState)));
//        }
//        Log.i(TAG, params.toString());
//        HttpEntity entity = new UrlEncodedFormEntity(params);
//
//        // Send the updated friends data to the server
//        Log.i(TAG, "Syncing to: " + SYNC_CONTACTS_URI);
//        final HttpPost post = new HttpPost(SYNC_CONTACTS_URI);
//        post.addHeader(entity.getContentType());
//        post.setEntity(entity);
//        final HttpResponse resp = getHttpClient().execute(post);
//        final String response = EntityUtils.toString(resp.getEntity());
//        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            // Our request to the server was successful - so we assume
//            // that they accepted all the changes we sent up, and
//            // that the response includes the contacts that we need
//            // to update on our side...
//            final JSONArray serverContacts = new JSONArray(response);
//            Log.d(TAG, response);
//            for (int i = 0; i < serverContacts.length(); i++) {
//                RawContact rawContact = RawContact.valueOf(serverContacts.getJSONObject(i));
//                if (rawContact != null) {
//                    serverDirtyList.add(rawContact);
//                }
//            }
//        } else {
//            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
//                Log.e(TAG, "Authentication exception in sending dirty contacts");
//                throw new AuthenticationException();
//            } else {
//                Log.e(TAG, "Server error in sending dirty contacts: " + resp.getStatusLine());
//                throw new IOException();
//            }
//        }
//
//        return serverDirtyList;
//    }

//    /**
//     * Download the avatar image from the server.
//     *
//     * @param avatarUrl the URL pointing to the avatar image
//     * @return a byte array with the raw JPEG avatar image
//     */
//    public static byte[] downloadAvatar(final String avatarUrl) {
//        // If there is no avatar, we're done
//        if (TextUtils.isEmpty(avatarUrl)) {
//            return null;
//        }
//
//        try {
//            Log.i(TAG, "Downloading avatar: " + avatarUrl);
//            // Request the avatar image from the server, and create a bitmap
//            // object from the stream we get back.
//            URL url = new URL(avatarUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//            try {
//                final BitmapFactory.Options options = new BitmapFactory.Options();
//                final Bitmap avatar = BitmapFactory.decodeStream(connection.getInputStream(),
//                        null, options);
//
//                // Take the image we received from the server, whatever format it
//                // happens to be in, and convert it to a JPEG image. Note: we're
//                // not resizing the avatar - we assume that the image we get from
//                // the server is a reasonable size...
//                Log.i(TAG, "Converting avatar to JPEG");
//                ByteArrayOutputStream convertStream = new ByteArrayOutputStream(
//                        avatar.getWidth() * avatar.getHeight() * 4);
//                avatar.compress(Bitmap.CompressFormat.JPEG, 95, convertStream);
//                convertStream.flush();
//                convertStream.close();
//                // On pre-Honeycomb systems, it's important to call recycle on bitmaps
//                avatar.recycle();
//                return convertStream.toByteArray();
//            } finally {
//                connection.disconnect();
//            }
//        } catch (MalformedURLException muex) {
//            // A bad URL - nothing we can really do about it here...
//            Log.e(TAG, "Malformed avatar URL: " + avatarUrl);
//        } catch (IOException ioex) {
//            // If we're unable to download the avatar, it's a bummer but not the
//            // end of the world. We'll try to get it next time we sync.
//            Log.e(TAG, "Failed to download user avatar: " + avatarUrl);
//        }
//        return null;
//    }

}