package com.example.findmyspot;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

class AsyncPostT extends AsyncTask<ArrayList<String>,Void,Void> {

    @Override
    protected Void doInBackground(ArrayList<String>... params) {

        try {
                /*
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("http://192.168.43.237", 3000), 10000);
                    Log.d("TEEEST","CONECTEED");
                } catch (IOException e) {
                    Log.d("TEEEST","NOT Conected");
                    Log.d("TEEEST",e.toString());
                }

                 */

          InetAddress address = InetAddress.getByName("http://192.168.43.237");
            if(address == null)
            {
                Log.d("TEEEST","NOT Conected");
            } else {
                Log.d("TEEEST","CONECTEED");
                Log.d("TEEEST",address.toString());
            }

             /**/

            URL url = new URL("http://192.168.43.237/sendPhoto"); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MACAddress", params[0].get(0));
            jsonObject.put("latitude",params[0].get(1));
            jsonObject.put("longitude",params[0].get(2));
            jsonObject.put("encodedImage",params[0].get(3));
            jsonObject.put("timestamp",params[0].get(4));
            Log.d("TEEEST",jsonObject.toString());


            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            httpURLConnection.disconnect();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}
