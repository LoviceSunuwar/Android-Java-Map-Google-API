package com.example.FA_LoviceSunuwar_c0835390_Android;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class GetURL {
    //htttp url connection data get
    public String readURL(String Url) throws IOException {

        String mydata = "";

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;


        try {
            URL url = new URL(Url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ((line = bufferedReader.readLine())!= null){
                stringBuffer.append(line);

            }
            mydata = stringBuffer.toString();
            bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return mydata;
    }
}
