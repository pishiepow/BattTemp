package batttemp.me;

import android.graphics.Color;

class BatteryTemperature {
    private static final BatteryTemperature ourInstance = new BatteryTemperature();

    static BatteryTemperature getInstance() {
        return ourInstance;
    }

    protected static int TemperatureDrawable(float fTempC) {
        int iResult = R.drawable.e;

        if (fTempC >= 47.5) {
            if (fTempC != 999) {
                iResult = R.drawable.p50;
            }
        } else if (fTempC < -7.5) {
            iResult = R.drawable.m10;
        } else {
            if (fTempC >= 42.5) {
                iResult = R.drawable.p45;
            } else if (fTempC < -2.5) {
                iResult = R.drawable.m5;
            } else {
                if (fTempC >= 37.5) {
                    iResult = R.drawable.p40;
                } else if (fTempC < 2.5) {
                    iResult = R.drawable.p0;
                } else {
                    if (fTempC >= 32.5) {
                        iResult = R.drawable.p35;
                    } else if (fTempC < 7.5) {
                        iResult = R.drawable.p5;
                    } else {
                        if (fTempC >= 27.5) {
                            iResult = R.drawable.p30;
                        } else if (fTempC < 12.5) {
                            iResult = R.drawable.p10;
                        } else {
                            if (fTempC >= 22.5) {
                                iResult = R.drawable.p25;
                            } else if (fTempC < 17.5) {
                                iResult = R.drawable.p15;
                            } else {
                                iResult = R.drawable.p20;
                            }
                        }
                    }
                }
            }
        }

        return iResult;
    }

    protected static String TemperatureStatus(float fTempC) {
        String strResult = "";

        if (fTempC >= 45) {
            if (fTempC == 999) {
                strResult = "Error";
            } else {
                strResult = "Overheat";
            }
        } else if (fTempC <= 0) {
            strResult = "Freezing";
        } else {
            if (fTempC >= 40) {
                strResult = "Hot";
            } else if (fTempC <= 5) {
                strResult = "Cold";
            } else {
                if (fTempC >= 30) {
                    strResult = "Warm";
                } else if (fTempC <= 15) {
                    strResult = "Cool";
                } else {
                    strResult = "Optimal";
                }
            }
        }

        return strResult;
    }

    protected static String TemperatureChargeStatus(float fTempC) {
        String strResult = "";

        if (fTempC >= 45) {
            if (fTempC == 999) {
                strResult = "Unknown Temperature";
            } else {
                strResult = "Do NOT Charge";
            }
        } else if (fTempC <= 0) {
            strResult = "Do NOT Charge";
        } else {
            if (fTempC >= 40) {
                strResult = "Charging Not Advised";
            } else if (fTempC <= 5) {
                strResult = "Charging Not Advised";
            } else {
                if (fTempC >= 30) {
                    strResult = "Charging OK";
                } else if (fTempC <= 15) {
                    strResult = "Charging OK";
                } else {
                    strResult = "Charging Recommended";
                }
            }
        }

        return strResult;
    }

    protected static int TemperatureColour(float fTempC) {
        int iResult = Color.WHITE;

        if (fTempC >= 45) {
            iResult = Color.RED;
        } else if (fTempC <= 0) {
            iResult = Color.MAGENTA;
        } else {
            if (fTempC >= 40) {
                iResult = Color.rgb(255,140,0);
            } else if (fTempC <= 5) {
                iResult = Color.BLUE;
            } else {
                if (fTempC >= 30) {
                    iResult = Color.YELLOW;
                } else if (fTempC <= 15) {
                    iResult = Color.CYAN;
                } else {
                    iResult = Color.GREEN;
                }
            }
        }

        return iResult;
    }
}
