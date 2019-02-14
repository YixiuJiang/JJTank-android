package com.jjstudio.jjtank.model;

public class TankControlData {
    public static byte[] GO = {(byte) 0xA5,(byte) 0xC5,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] STOP = {(byte) 0xA5,(byte) 0xC5,(byte) 0x00,(byte) 0x00,(byte) 0xAA};
    public static byte[] TURRENT_LEFT = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x0A,(byte) 0xAA};
    public static byte[] TURRENT_RIGHT = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x4A,(byte) 0xAA};
    public static byte[] TURRENT_UP = {(byte) 0xA5,(byte) 0xC7,(byte) 0x60 ,(byte) 0x00,(byte) 0xAA};
    public static byte[] TURRENT_DOWN = {(byte) 0xA5,(byte) 0xC7,(byte) 0x02,(byte) 0x00,(byte) 0xAA};
    public static byte[] TURRENT_STOP = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] TURRENT_REVERSE = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] TURRENT_USUAL = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] LEFT_TRACK_REVERSE = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] LEFT_TRACK_USUAL = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] RIGHT_TRACK_REVERSE = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] RIGHT_TRACK_USUAL = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] GUN_RECOIL_REVERSE = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] GUN_RECOIL__USUAL = {(byte) 0xA5,(byte) 0xC7,(byte) 0x32,(byte) 0x00,(byte) 0xAA};
    public static byte[] GYRO_ON = {(byte) 0xC6,(byte) 0x0A,(byte) 0x00,(byte) 0xAA};
    public static byte[] GYRO_OFF = {(byte) 0xC6,(byte) 0x0A,(byte) 0x00,(byte) 0xAA};
    public static byte[] FIRE = {(byte) 0xA5,(byte) 0xC8,(byte) 0x32,(byte) 0x32,(byte) 0xAA};
    public static boolean isTest = true;
}
