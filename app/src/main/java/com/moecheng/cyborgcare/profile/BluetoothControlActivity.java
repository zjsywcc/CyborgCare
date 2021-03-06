package com.moecheng.cyborgcare.profile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.bluetooth.DeviceConnector;
import com.moecheng.cyborgcare.bluetooth.DeviceData;
import com.moecheng.cyborgcare.bluetooth.format.Utils;
import com.moecheng.cyborgcare.measure.MeasureFragment;
import com.moecheng.cyborgcare.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;



/**
 * Created by wangchengcheng on 2017/11/29.
 */

public class BluetoothControlActivity extends BaseActivity {

    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";

    // crc校验码
    private static final String CRC_OK = "#FFFF00";
    private static final String CRC_BAD = "#FF0000";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss.SSS");


    public static String MSG_NOT_CONNECTED;
    public static String MSG_CONNECTING;
    public static String MSG_CONNECTED;

    private static DeviceConnector connector;
    private static MeasureFragment.BluetoothResponseHandler mHandler;



    // do not resend request to enable Bluetooth
    // if there is a request already in progress
    // See: https://code.google.com/p/android/issues/detail?id=24931#c1
    boolean pendingRequestEnableBt = false;




    private StringBuilder logHtml;

    @BindView(R.id.log_textview)
    TextView logTextView;
    @BindView(R.id.command_edittext)
    EditText commandEditText;

    // 应用程序设置
    private boolean hexMode, checkSum, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    private Bundle outState;

    @Override
    public void initViews() {
        super.initViews();

        this.logHtml = new StringBuilder();
        this.logTextView.setMovementMethod(new ScrollingMovementMethod());
        this.logTextView.setText(Html.fromHtml(logHtml.toString()));

        // soft-keyboard send button
        this.commandEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendCommand(null);
                    return true;
                }
                return false;
            }
        });
        // hardware Enter button
        this.commandEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            sendCommand(null);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);
        setMenu(getToolbar().getMenu());
        setTitleNavigationIcon(R.mipmap.icon_title_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setToolbarTitleTv(R.string.bluetooth_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingRequestEnableBt =
                    savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }
        outState = savedInstanceState;
        PreferenceManager.setDefaultValues(this, R.xml.activity_bluetoothsetting, false);

        if (mHandler == null)
            mHandler = new MeasureFragment.BluetoothResponseHandler(MeasureFragment.mECGDataArrayList, MeasureFragment.mECGDataAdapter, MeasureFragment.valuePairQueue, MeasureFragment.uploadThread, this);
        else
            mHandler.setTarget(this);

        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else {
            getToolbar().setSubtitle(MSG_NOT_CONNECTED);
        }

        if (savedInstanceState != null)
            this.logHtml.append(savedInstanceState.getString(LOG));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setTitle(null);
    }
    // ==========================================================================



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
        if (logTextView != null) {
            outState.putString(LOG, logHtml.toString());
        }
    }


    // ============================================================================


    /**
     * 检查连接已准备就绪
     */
    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    // ==========================================================================

    /**
     * 断开连接
     */
    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }


    // ==========================================================================

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getToolbar().setSubtitle(deviceName);
    }

    /**
     * 搜索按钮
     *
     * @return
     */
    @Override
    public boolean onSearchRequested() {
        if (isAdapterReady())
            startDeviceListActivity();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        final MenuItem bluetooth = menu.findItem(R.id.menu_search);
        if (bluetooth != null) bluetooth.setIcon(this.isConnected() ?
                R.mipmap.ic_action_bluetooth_connected :
                R.mipmap.ic_action_bluetooth);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            case R.id.menu_clear:
                if (logTextView != null) logTextView.setText("");
                return true;

            case R.id.menu_send:
                if (logTextView != null) {
                    final String msg = logTextView.getText().toString();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
                }
                return true;

            case R.id.menu_settings:
                final Intent intent = new Intent(this, BluetoothSettingActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ==========================================================================



    private void setMenu(Menu menu) {
        final MenuItem bluetooth = menu.findItem(R.id.menu_search);
        final MenuItem menuClear = menu.findItem(R.id.menu_clear);
        final MenuItem menuSend = menu.findItem(R.id.menu_send);
        final MenuItem menuSettings = menu.findItem(R.id.menu_settings);
        menuSettings.setVisible(false);
        if (bluetooth != null) bluetooth.setIcon(this.isConnected() ?
                R.mipmap.ic_action_bluetooth_connected :
                R.mipmap.ic_action_bluetooth);
        bluetooth.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;
            }
        });
        menuClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (logTextView != null) logTextView.setText("");
                return true;
            }
        });
        menuSend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (logTextView != null) {
                    final String msg = logTextView.getText().toString();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
                }
                return true;
            }
        });
        menuSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Intent intent = new Intent(BluetoothControlActivity.this, BluetoothSettingActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        // hex mode
        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = "HEX".equals(mode);
        if (hexMode) {
            commandEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            commandEditText.setFilters(new InputFilter[]{new Utils.InputFilterHex()});
        } else {
            commandEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            commandEditText.setFilters(new InputFilter[]{});
        }

        // checksum
        final String checkSum = Utils.getPrefence(this, getString(R.string.pref_checksum_mode));
        this.checkSum = "Modulo 256".equals(checkSum);

        // Окончание строки
        this.command_ending = getCommandEnding();

        // Формат отображения лога команд
        this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));
    }
    // ============================================================================


    /**
     * 从设置中获取命令的结尾
     */
    private String getCommandEnding() {
        String result = Utils.getPrefence(this, getString(R.string.pref_commands_ending));
        if (result.equals("\\r\\n")) result = "\r\n";
        else if (result.equals("\\n")) result = "\n";
        else if (result.equals("\\r")) result = "\r";
        else result = "";
        return result;
    }
    // ============================================================================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(BluetoothActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }
    // ==========================================================================


    /**
     * 建立到设备的连接
     */
    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }
    // ==========================================================================


    /**
     * 发送一个命令到设备
     */
    public void sendCommand(View view) {
        if (commandEditText != null) {
            String commandString = commandEditText.getText().toString();
            if (commandString.isEmpty()) return;

            // Дополнение команд в hex
            if (hexMode && (commandString.length() % 2 == 1)) {
                commandString = "0" + commandString;
                commandEditText.setText(commandString);
            }

            // checksum
            if (checkSum) {
                commandString += Utils.calcModulo256(commandString);
            }

            byte[] command = (hexMode ? Utils.toHex(commandString) : commandString.getBytes());
            if (command_ending != null) command = Utils.concat(command, command_ending.getBytes());
            if (isConnected()) {
                connector.write(command);
                appendLog(commandString, hexMode, true, needClean);
            }
        }
    }

    /**
     * 跳转到连接设备列表
     */
    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }


    @Override
    protected View getContentView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.activity_terminal, null);
        return root;
    }

    protected int getMenuLayoutId() {
        return R.menu.device_control_activity;
    }



    /**
     * 添加日志
     *
     * @param message  - 文字显示
     * @param outgoing - 方向
     */
    public void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuilder msg = new StringBuilder();
        if (show_timings) msg.append("[").append(timeformat.format(new Date())).append("]");
        if (show_direction) {
            final String arrow = (outgoing ? " << " : " >> ");
            msg.append(arrow);
        } else msg.append(" ");

        // 删除换行符 \r\n
        message = message.replace("\r", "").replace("\n", "");

        // 校验和
        String crc = "";
        boolean crcOk = false;
        if (checkSum) {
            int crcPos = message.length() - 2;
            crc = message.substring(crcPos);
            message = message.substring(0, crcPos);
            crcOk = outgoing || crc.equals(Utils.calcModulo256(message).toUpperCase());
            if (hexMode) crc = Utils.printHex(crc.toUpperCase());
        }

        // 加入 html
        msg.append("<b>")
                .append(hexMode ? Utils.printHex(message) : message)
                .append(checkSum ? Utils.mark(crc, crcOk ? CRC_OK : CRC_BAD) : "")
                .append("</b>")
                .append("<br>");

        logHtml.append(msg);
        logTextView.append(Html.fromHtml(msg.toString()));
        Layout logLayout = logTextView.getLayout();

        if (logLayout != null) {
            final int scrollAmount = logLayout.getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
            if (scrollAmount > 0)
                logTextView.scrollTo(0, scrollAmount);
            else logTextView.scrollTo(0, 0);
        }
        if (clean) commandEditText.setText("");
    }
    // =========================================================================



    // ==========================================================================
}
