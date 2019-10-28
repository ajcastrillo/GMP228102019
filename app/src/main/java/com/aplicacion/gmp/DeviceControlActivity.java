package com.aplicacion.gmp;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aplicacion.gmp.Modelo.Magnitud;
import com.aplicacion.gmp.Modelo.Servicio;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeviceControlActivity extends Activity {

    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private boolean mConnected = false;
    private Button btnSend;
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private  byte[] txValueAux;

    private BluetoothLeService mService = null;

    final ArrayList<String> obj = new ArrayList<String>();
    final ArrayList<String> payload = new ArrayList<String>();
    final ArrayList<byte[]> obj1 = new ArrayList<byte[]>();
    ArrayList<byte[]> objaux = new ArrayList<byte[]>();
    String auxiliar ="";


    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private BluetoothLeService mBluetoothLeService;

//    firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        btnSend=(Button) findViewById(R.id.sendButton);

        // Sets up UI references.
        // Agregando la MAC
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
//        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        // para colocar el estado de conectado o desconectado
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data);

        getActionBar().setTitle(mDeviceName);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        //Conéctese a un servicio de aplicación, creándolo si es necesario.
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // inicializar firebase
        inicializarFirebase();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.sendText);
                String message = editText.getText().toString();
                byte[] value;
//                try {
//                    //send data to service
//
//
//                        String  jsonString = "{"+
//                                '"'+ "name"+'"'+ ":"+'"'+ "Ronaldo"+'"'+","+
//                                '"'+"sport"+'"'+ ":"+ '"'+"soccer"+'"'+","+
//                                '"'+"age"+'"'+ ":"+ 25+","+
//                                '"'+ "id"+'"'+ ":"+ 121+","+
//                                '"'+"lastScores"+'"'+ ":" +"["+ 2+","+ 1+","+ 3+","+ 5+","+ 0+","+ 0+","+ 1+","+ 1+"]"+"}" ;
//
//                        JSONObject jsonObjectp = new JSONObject(jsonString);
//                    JSONArray arr = jsonObjectp.getJSONArray("lastScores");
//                    String valor;
//                    int suma = 0;
//                    for(int i=0; i<arr.length(); i++){
//                        valor = arr.getString(i);
//                        suma = suma + Integer.parseInt(arr.getString(i));
//
//                    }
//                    Log.d("valor", String.valueOf(suma));
////                     list = jsonObjectp.get("lastScores");
//
////                        for (String nombre : lista) {
////                            System.out.println(nombre);
////                        }
//
//
//                        String formatJSON = "{"+'"'+"valor1"+'"'+":"+40+","+'"'+"valor2"+'"'+":"+60 +"}";
//                        JSONObject jsonObject = new JSONObject(formatJSON);
//                        String valor1 = jsonObject.get("valor1").toString();
//                        String valor2 = jsonObject.get("valor2").toString();
//
//                        Log.d("Error", valor1);
//                        Log.d("Error", valor2);
//                        Log.d("Error", String.valueOf(jsonObject));
//                    }catch (JSONException err){
//                        Log.d("Error", err.toString());
//                    }

                try {
                    String formatJSON = "{"+'"'+"DS"+'"'+":"+11333222+"}";
//                    value = message.getBytes("UTF-8");
                    value = formatJSON.getBytes("UTF-8");
                    mBluetoothLeService.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    editText.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });



//        messageListView = (ListView) findViewById(R.id.listMessage);
//        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
//        messageListView.setAdapter(listAdapter);
//        messageListView.setDivider(null);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    // Código para gestionar el ciclo de vida del servicio.
//    supervisa la conexión con el servicio.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService(); // Servicio enlazado,  Esto permite a los clientes puedan llamar a métodos públicos en el servicio
            if (!mBluetoothLeService.initialize()) { // se inicializo BluetoothManager y el adaptador si es verdadero
                Log.e(TAG, "No se puede inicializar Bluetooth");
                finish();
            }
            // Se conecta automáticamente al dispositivo tras una inicialización exitosa.
            mBluetoothLeService.connect(mDeviceAddress);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }


    };



    @Override
    protected void onResume() {
        super.onResume();
        // registrar el receptor
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }


    // Maneja varios eventos disparados por el Servicio.
    // ACTION_GATT_CONNECTED: conectado a un servidor GATT.
    // ACTION_GATT_DISCONNECTED: desconectado de un servidor GATT.
    // ACTION_GATT_SERVICES_DISCOVERED: descubrieron servicios GATT.
    // ACTION_DATA_AVAILABLE: datos recibidos del dispositivo. Esto puede ser el resultado de leer
    // u operaciones de notificación.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
            byte[] byteArr;

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mConnected = true;
                        updateConnectionState(R.string.connected);
                        invalidateOptionsMenu();
                    }
                });


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mConnected = false;
                        updateConnectionState(R.string.disconnected);
                        invalidateOptionsMenu();
//                        clearUI();

                    }
                });


            }            //*********************//
//            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                mService.enableTXNotification();
//            }
            //*********************//
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                final byte[] txValue = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);




                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
//                            String textaux = new String(objaux[0], "UTF-8");
                            String text = new String(txValue, "UTF-8");
                             String aux = "";
//                            obj.add(text);
//                            int intindex =  text.indexOf("\n");
                            int intindex =  text.indexOf("}");
                            if(intindex != -1){
                                auxiliar += text;
//                                String nuevo =  auxiliar.replace("\n", "");
//                                JSONObject jsonObject = new JSONObject(nuevo);
                                JSONObject jsonObject = new JSONObject(auxiliar);
                                String serv = jsonObject.get("SERV").toString();
                                String matricula = jsonObject.get("MAT").toString();
                                int repeticion = Integer.parseInt(jsonObject.get("R").toString());
                                int punto = Integer.parseInt(jsonObject.get("Q").toString());
                                int prueba = Integer.parseInt(jsonObject.get("P").toString());
                                Double Ta = Double.parseDouble(jsonObject.get("Ta").toString());
                                Double Pa = Double.parseDouble(jsonObject.get("Pa").toString());
                                Double HR = Double.parseDouble(jsonObject.get("HR").toString());
                                Double Qb = Double.parseDouble(jsonObject.get("Qb").toString());
                                Double Qc = Double.parseDouble(jsonObject.get("Qc").toString());
                                Double PLmm = Double.parseDouble(jsonObject.get("PLmm").toString());
                                Double PLMUT = Double.parseDouble(jsonObject.get("PLmut").toString());
                                Double PMUT = Double.parseDouble(jsonObject.get("Pmut").toString());
                                Double TMUT = Double.parseDouble(jsonObject.get("Tmut").toString());
                                Double Pdif = Double.parseDouble(jsonObject.get("Pdif").toString());
                                long dataTime =System.currentTimeMillis() / 1000L;

                                System.out.println(dataTime);

                                Servicio servicio = new Servicio();
                                servicio.setUid(UUID.randomUUID().toString());
                                servicio.setCodigo(serv);
                                servicio.setMatricula(matricula);
                                servicio.setRepeticion(repeticion);
                                servicio.setPunto(punto);
                                servicio.setPrueba(prueba);

                                Magnitud ta = new Magnitud();
                                ta.setPrefijo("Ta");
                                ta.setValor(Ta);
                                Magnitud pa = new Magnitud();
                                pa.setPrefijo("Pa");
                                pa.setValor(Pa);
                                Magnitud hr = new Magnitud();
                                hr.setPrefijo("HR");
                                hr.setValor(HR);
                                Magnitud qb = new Magnitud();
                                qb.setPrefijo("Qb");
                                qb.setValor(Qb);
                                Magnitud qc = new Magnitud();
                                qc.setPrefijo("Qc");
                                qc.setValor(Qc);
                                Magnitud plmm = new Magnitud();
                                plmm.setPrefijo("PLmm");
                                plmm.setValor(PLmm);
                                Magnitud plmut = new Magnitud();
                                plmut.setPrefijo("PLmut");
                                plmut.setValor(PLMUT);
                                Magnitud pmut = new Magnitud();
                                pmut.setPrefijo("Pmut");
                                pmut.setValor(PMUT);
                                Magnitud tmut = new Magnitud();
                                tmut.setPrefijo("Tmut");
                                tmut.setValor(TMUT);
                                Magnitud pdif = new Magnitud();
                                pdif.setPrefijo("Pdif");
                                pdif.setValor(Pdif);
                                List<Magnitud> magnitudes = new ArrayList<Magnitud>();
                                magnitudes.add(ta);
                                magnitudes.add(pa);
                                magnitudes.add(hr);
                                magnitudes.add(qb);
                                magnitudes.add(qc);
                                magnitudes.add(plmm);
                                magnitudes.add(plmut);
                                magnitudes.add(pmut);
                                magnitudes.add(tmut);
                                magnitudes.add(pdif);
                                servicio.setMagnitudes(magnitudes);
                                servicio.setDateTime(dataTime);

//                                databaseReference.child("Historicos").child(servicio.getUid()).setValue(servicio);

//                                Query query = databaseReference.child("RegistrosCalibracion").orderByChild("codigo").equalTo("VERIF00090819");
//
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                        List<Magnitud> mag = new ArrayList<Magnitud>();
//                                        Double suma= 0.0;
//                                        long count=0;
//                                        for (DataSnapshot objSnaptahot : dataSnapshot.getChildren()){
//                                            Servicio s = objSnaptahot.getValue(Servicio.class);
//                                            count = objSnaptahot.getChildrenCount();
//                                            List<Magnitud> mag =  s.getMagnitudes();
//                                            suma += mag.get(0).getValor();
//
//                                        }
//
//                                        System.out.println(suma/count);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//
//
//                                });

                                mDataField.setText(auxiliar);
//                                payload.add(nuevo);
//                                obj.clear();
                                auxiliar = "";
                            } else {
                                auxiliar += text;
                            }

                        } catch (Exception e) {
                            auxiliar ="";
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }


        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }



}
