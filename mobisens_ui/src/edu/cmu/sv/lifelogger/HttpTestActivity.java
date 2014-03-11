package edu.cmu.sv.lifelogger;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.app.ProgressDialog;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class HttpTestActivity {
    private String latitude;
    private String longitude;
    private final String APIKEY = "AIzaSyBOxUejYHOuGv9CvEVIeDcc-fA3hiwW0hQ"; //REPLACE WITH YOUR OWN GOOGLE PLACES API KEY
    private final int radius = 100;
    private String type = "food";
    private StringBuilder query = new StringBuilder();
    private ArrayList<Place> places = new ArrayList<Place>();
    private ListView listView;
 

    
    public  void  getAddress(){
    	latitude = "37.379297";
        longitude = "-122.060680";
        new GetCurrentLocation().execute(latitude, longitude);
    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }

    private class GetCurrentLocation extends AsyncTask<Object, String, Boolean> {

        @Override
        protected Boolean doInBackground(Object... myLocationObjs) {
            if(null != latitude && null != longitude) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            assert result;
            query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
            query.append("location=" +  latitude + "," + longitude + "&");
            query.append("radius=" + radius + "&");
            //query.append("types=" + type + "&");
            query.append("sensor=true&"); //Must be true if queried from a device with GPS
            query.append("key=" + APIKEY);
            new QueryGooglePlaces().execute(query.toString());
        }
    }

    /**
     * Based on: http://stackoverflow.com/questions/3505930
     */
    private class QueryGooglePlaces extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(args[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("ERROR", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Document xmlResult = loadXMLFromString(result);
                NodeList nodeList =  xmlResult.getElementsByTagName("result");
                for(int i = 0, length = nodeList.getLength(); i < length; i++) {
                    Node node = nodeList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) node;
                        Place place = new Place();
                        Node name = nodeElement.getElementsByTagName("name").item(0);
                        Node vicinity = nodeElement.getElementsByTagName("vicinity").item(0);
                        Node rating = nodeElement.getElementsByTagName("rating").item(0);
                        Node reference = nodeElement.getElementsByTagName("reference").item(0);
                        Node id = nodeElement.getElementsByTagName("id").item(0);
                        Node geometryElement = nodeElement.getElementsByTagName("geometry").item(0);
                        NodeList locationElement = geometryElement.getChildNodes();
                        Element latLngElem = (Element) locationElement.item(1);
                        Node lat = latLngElem.getElementsByTagName("lat").item(0);
                        Node lng = latLngElem.getElementsByTagName("lng").item(0);
                        float[] geometry =  {Float.valueOf(lat.getTextContent()),
                                Float.valueOf(lng.getTextContent())};
                        int typeCount = nodeElement.getElementsByTagName("type").getLength();
                        String[] types = new String[typeCount];
                        for(int j = 0; j < typeCount; j++) {
                            types[j] = nodeElement.getElementsByTagName("type").item(j).getTextContent();
                        }
                        place.setVicinity(vicinity.getTextContent());
                        place.setId(id.getTextContent());
                        place.setName(name.getTextContent());
                        if(null == rating) {
                            place.setRating(0.0f);
                        } else {
                            place.setRating(Float.valueOf(rating.getTextContent()));
                        }
                        place.setReference(reference.getTextContent());
                        place.setGeometry(geometry);
                        place.setTypes(types);
                        places.add(place);
                    }
                }
                //PlaceAdapter placeAdapter = new PlaceAdapter(HttpTestActivity.this, R.layout.httptestrow, places);
                //listView = (ListView)findViewById(R.id.httptestlist_listview);
                //listView.setAdapter(placeAdapter);

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
            
        }
        
    }

    //http://www.mkyong.com/java/jaxb-hello-world-example/
    /**
     * Represents a single Result of a places API call
     * https://developers.google.com/places/documentation/search
     */
    private class Place {
        public String vicinity;
        public float[] geometry; //array(0 => lat, 1 => lng)
        public String id;
        public String name;
        public float rating;
        public String reference;
        public String[] types;

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
 
        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String[] getTypes() {
            return types;
        }

        public void setTypes(String[] types) {
            this.types = types;
        }
    }

    private class PlaceAdapter extends ArrayAdapter<Place> {
        public Context context;
        public int layoutResourceId;
        public ArrayList<Place> places;

        public PlaceAdapter(Context context, int layoutResourceId, ArrayList<Place> places) {
            super(context, layoutResourceId, places);
            this.layoutResourceId = layoutResourceId;
            this.places = places;
        }

        @Override
        public View getView(int rowIndex, View convertView, ViewGroup parent) {
            View row = convertView;
            //if(null == row) {
        //        LayoutInflater layout = (LayoutInflater)getSystemService(
          //              Context.LAYOUT_INFLATER_SERVICE
               // );
            ///    row = layout.inflate(R.layout.httptestrow, null);
     //       }
            //Place place = places.get(rowIndex);
            //if(null != place) {
                //TextView name = (TextView) row.findViewById(R.id.htttptestrow_name);
              //  TextView vicinity = (TextView) row.findViewById(
                //        R.id.httptestrow_vicinity);
                //if(null != name) {
              ///      name.setText(place.getName());
                //}
                //if(null != vicinity) {
                  //  vicinity.setText(place.getVicinity());
                //}
            //}
            return row;
        }
    }
}