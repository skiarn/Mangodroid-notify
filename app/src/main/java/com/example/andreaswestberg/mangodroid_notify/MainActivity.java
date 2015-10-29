package com.example.andreaswestberg.mangodroid_notify;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.andreaswestberg.mangodroid_notify.activity.EventDetailActivity;
import com.example.andreaswestberg.mangodroid_notify.fragment.EventDetailFragment;
import com.example.andreaswestberg.mangodroid_notify.fragment.EventsFragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends ActionBarActivity implements EventsFragment.OnFragmentInteractionListener {
    private static String TAG = "MainActivity";

    private static String keyFileName = "mangodroid.key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviewreportsactivity);

        String content = getData();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EventsFragment fragment = EventsFragment.newInstance(content);
        fragmentTransaction.add(R.id.container_body, fragment);
        fragmentTransaction.commit();

        //getSupportFragmentManager().beginTransaction()
          //      .add(R.id.container, new EventDetailFragment().newInstance(content)).commit();
    }

    private String getData() {
        String content = "";
        Intent i = getIntent();
        Uri u = i.getData();
        if(i == null) return "";
        byte[] data = ReadUri(u);
        byte[] dad = getAttachmentAsBase64();
        if (data != null) {

            try {
                String strdata = new String(data);
                strdata=strdata.replace("\r\n","");
                byte[] dataBase64= Base64.decode(strdata.getBytes(), Base64.URL_SAFE);
                byte[] decodedBytes = null;
                Cipher c = Cipher.getInstance("AES/CFB/NoPadding");
                byte[] iv = Arrays.copyOfRange(dataBase64, 0, 16);
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                byte[] d = Arrays.copyOfRange(dataBase64, 16, dataBase64.length);
                c.init(Cipher.DECRYPT_MODE, getKey(keyFileName), ivspec);
                decodedBytes = c.doFinal(d);
                if(decodedBytes != null && decodedBytes.length != 0){
                    byte[] dcplainbytes= Base64.decode(decodedBytes, Base64.URL_SAFE);
                    content = new String(dcplainbytes, "UTF-8");
                }
            } catch (Exception e) {
                Log.i(TAG, "AES decryption error: " + e.toString());
                Toast.makeText(getApplicationContext(), "AES decryption error: " + e.toString(), Toast.LENGTH_LONG).show();
                String dataRawString = "";
                content = encodeStringToUTF8(data);
            }
        }
        return content;
    }

    private String encodeStringToUTF8(byte[] data) {
        String s = "";
        try {
            s = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException encodingEx) {
            encodingEx.printStackTrace();
            Log.e(TAG, encodingEx.toString());
        }
        return s;
    }

    private byte[] ReadUri(Uri u) {
        byte[] filedata = null;
        if(u == null) return null;
        String scheme = u.getScheme();

        if(ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try
            {
                ContentResolver cr = getContentResolver();
                AssetFileDescriptor afd = cr.openAssetFileDescriptor(u, "r");
                long length = afd.getLength();
                filedata = new byte[(int) length];
                InputStream is = cr.openInputStream(u);
                if(is == null) return null;
                try
                {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    while ((nRead = is.read(filedata, 0, filedata.length)) != -1) {
                        buffer.write(filedata, 0, nRead);
                    }
                    buffer.flush();
                    filedata = buffer.toByteArray();
                }
                catch(IOException e) {
                    return null;
                }
            }
            catch(FileNotFoundException e) {
                return null;
            }
        }
        return filedata;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "No current settings implemented.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.generateNewKey:
                SecretKey key = null;
                try {
                    key = getKey(keyFileName);
                    Toast.makeText(this, "Key available.", Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(this, "Error while generating key:" + e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Error while generating key:" + e.toString(), Toast.LENGTH_LONG).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private SecretKey getKey(String keyFileName) throws NoSuchAlgorithmException, IOException {
        try {
            byte[] b64key = getEncryptionKeyFromDiskAsBase64();
            return new SecretKeySpec(b64key, 0, b64key.length, "AES");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            //key file do not exist. lets create it.
            createEncryptionKeyOnDisk();
            return getKey(keyFileName);
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system   entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    private byte[] getEncryptionKeyFromDiskAsBase64() throws IOException {
        ContextWrapper contextWrapper = new ContextWrapper(this);
        final String filepath = "mangodriodFileStorage";
        byte[] keybase64 = null;
        File internalFile = new File(contextWrapper.getDir(filepath, Context.MODE_PRIVATE), keyFileName);
        int size = (int) internalFile.length();
        keybase64 = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(internalFile));
            buf.read(keybase64, 0, keybase64.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return Base64.decode(keybase64, Base64.URL_SAFE);

    }
    private void createEncryptionKeyOnDisk() throws NoSuchAlgorithmException, IOException {
        SecretKey key = generateKey();
        String keyBase64enc = Base64.encodeToString(key.getEncoded(), Base64.URL_SAFE);
        //save to external directory..

        FileWriter fwe = new FileWriter(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + keyFileName));
        fwe.write(keyBase64enc);
        fwe.close();

        //save to internal storage.
        ContextWrapper contextWrapper = new ContextWrapper(this);
        final String filepath = "mangodriodFileStorage";
        FileWriter fwi = new FileWriter(new File(contextWrapper.getDir(filepath, Context.MODE_PRIVATE), keyFileName));
        fwi.write(keyBase64enc);
        fwi.close();

        //FileOutputStream fos = new FileOutputStream(new File(internalDirectory, filename));
        //fos.write(data);
        //fos.close();
    }

    public byte[] getAttachmentAsBase64() {
        Intent intent = getIntent();
        InputStream is = null;
        FileOutputStream os = null;
        String fullPath = null;

        try {
            String action = intent.getAction();
            if (!Intent.ACTION_VIEW.equals(action)) {
                return null;
            }

            Uri uri = intent.getData();
            String scheme = uri.getScheme();
            String name = null;

            if (scheme.equals("file")) {
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments.size() > 0) {
                    name = pathSegments.get(pathSegments.size() - 1);
                }
            } else if (scheme.equals("content")) {
                Cursor cursor = getContentResolver().query(uri, new String[] {
                        MediaStore.MediaColumns.DISPLAY_NAME
                }, null, null, null);
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex);
                }
            } else {
                return null;
            }

            if (name == null) {
                return null;
            }

            int n = name.lastIndexOf(".");
            String fileName, fileExt;

            if (n == -1) {
                return null;
            } else {
                fileName = name.substring(0, n);
                fileExt = name.substring(n);
                if (!fileExt.equals(".mgofy")) {
                    return null;
                }
            }

            fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "attachment-mgofy-report"/* create full path to where the file is to go, including name/ext */;

            is = getContentResolver().openInputStream(uri);
            os = new FileOutputStream(fullPath);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();
            File file = new File(fullPath);
            int size = (int) file.length();
            byte[] attachment = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(attachment, 0, attachment.length);
            buf.close();
            return attachment;
        } catch (Exception e) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e1) {
                }
            }
            if (fullPath != null) {
                File f = new File(fullPath);
                f.delete();
            }
        }
        return null;
    }

    @Override
    public void onEventSelected(String source) {
        Toast.makeText(getApplicationContext(), "event selected: " + source, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("source", source);
        startActivity(intent);

        //getSupportFragmentManager().beginTransaction().replace(R.id.container_body, EventDetailFragment.newInstance(source), "Report..").commit();
    }

}
