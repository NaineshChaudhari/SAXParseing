package com.example.n9xch.saxparseing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaXActivity extends AppCompatActivity {

   String streamTitle ="";
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sa_x);

        final TextView result=(TextView)findViewById(R.id.tvresult);

        (new AsyncTask<String, Void, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd= ProgressDialog.show(SaXActivity.this,"","");
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    URL rssUrl= new URL("http://timesofindia.indiatimes.com/rssfeeds/21483354.cms");

                    SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                    SAXParser mySAXParser= mySAXParserFactory.newSAXParser();
                    XMLReader myXMLReader = mySAXParser.getXMLReader();
                    RSSHandler myRSSHadler = new RSSHandler();
                    myXMLReader.setContentHandler(myRSSHadler);
                    InputSource myInPutSource = new InputSource(rssUrl.openStream());
                    myXMLReader.parse(myInPutSource);


                } catch (MalformedURLException e) {
                    e.printStackTrace();

                 //   result.setText("Can't Connect RSS!! 1");
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                   // result.setText("Can't Connect RSS!! 2");
                } catch (SAXException e) {
                    e.printStackTrace();
                    //result.setText("Can't Connect RSS!! 3");
                } catch (IOException e) {
                    e.printStackTrace();
                    //result.setText("Can't Connect RSS!! 4");
                }
                return streamTitle;

            }

            @Override
            protected void onPostExecute(String streamTitle) {
                super.onPostExecute(streamTitle);
                pd.dismiss();

                    result.setText(streamTitle);

            }
        }).execute();

    }

    private class RSSHandler extends DefaultHandler
    {

        final int stateUnknown = 0;
        final int stateTitle = 1;
        int state = stateUnknown;

        boolean bdes,bpubdate;


        int numberOfTitel=0;
        String strTitle="";
        String strElement="";
        String strdes="";
        String strpubdate="";

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            strTitle = "\n************ Start Document ************\n";
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            strTitle += "************ End Document ************\n";
            streamTitle = "Number Of Title: "+ String.valueOf(numberOfTitel)+"\n"
                    + strTitle + strpubdate + strdes  ;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if(localName.equalsIgnoreCase("title"))
            {
                state = stateTitle;
                strElement = "\n\nTitle: ";
                numberOfTitel++;
            }
            else
            {
                state = stateUnknown;
            }

          if(localName.equalsIgnoreCase("pubDate"))
            {
                bpubdate=true;
                strpubdate = "PubDate: ";
                // numberOfTitel++;
            }
            else
            {
                bpubdate=false;
            }

            if(localName.equalsIgnoreCase("description"))
            {
                bdes=true;
                strdes = "Description: ";
               // numberOfTitel++;
            }
            else
            {
                bdes=false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            String strCharacters =new String(ch,start,length);
            if(state == stateTitle)
            {
                strElement += strCharacters;
            }
            if(bpubdate==true)
            {
                strpubdate = strCharacters;
            }
            if(bdes==true)
            {
                strdes += strCharacters;
            }


        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (localName.equalsIgnoreCase("title"))
            {
                strTitle+=strElement + "\n";
            }
            if (localName.equalsIgnoreCase("pubDate"))
            {
                strTitle+=strpubdate + "\n";
            }
            if (localName.equalsIgnoreCase("description"))
            {
                strTitle+=strdes + "\n";
            }
            state = stateUnknown;

        }

       }

}
